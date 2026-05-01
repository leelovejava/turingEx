package com.yami.trading.security.common.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.CharsetUtil;
import com.yami.trading.bean.model.RiskClient;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.*;
import com.yami.trading.security.common.util.RiskClientUtil;
import com.yami.trading.service.user.RiskClientService;
import com.yami.trading.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.handler.HttpHandler;
import com.yami.trading.security.common.adapter.AuthConfigAdapter;
import com.yami.trading.security.common.bo.UserInfoInTokenBO;
import com.yami.trading.security.common.enums.SysTypeEnum;
import com.yami.trading.security.common.manager.TokenStore;
import com.yami.trading.security.common.util.AuthUserContext;
import com.yami.trading.security.common.util.ItemRedisKeys;
import com.yami.trading.security.common.util.MD5;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 授权过滤，只要实现AuthConfigAdapter接口，添加对应路径即可：
 *
 * @author 菠萝凤梨
 * @date 2022/3/25 17:33
 */
@Component
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Value("${sign.encryption-key}")
    private String ENCRYPTION_KEY;

    @Value("${sign.version-number}")
    private String VERSION_NUMBER;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private HttpHandler httpHandler;

    @Autowired
    private AuthConfigAdapter authConfigAdapter;

    @Autowired
    private RiskClientService riskClientService;

    @Autowired
    UserService userService;

    /**
     * 白名单URL
     */
    private static final HashSet<String> WHITE_URLS2 = new HashSet<String>();

    /**
     * 随机数与时间戳字典
     */
    private static final Map<String, Long> ADMINCACHEMAP = new ConcurrentHashMap<>();

    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String requestUri = req.getRequestURI();
        String servletPath = req.getServletPath();

        boolean servletPathWhiteUri = false;
        if (WHITE_URLS2.contains(servletPath)) {
            servletPathWhiteUri = true;
        }

        // 无需识别 token 的白名单
        boolean ignoreTokenUri = false;
        List<String> excludePathPatterns = authConfigAdapter.excludePathPatterns();
        // 如果匹配不需要授权的路径，就不需要校验是否需要授权
        if (CollectionUtil.isNotEmpty(excludePathPatterns)) {
            for (String excludePathPattern : excludePathPatterns) {
                if (pathMatcher.match(excludePathPattern, requestUri)) {
                    ignoreTokenUri = true;
                }
            }
        }

        // 如果 uri 请求携带 token，则校验 token，如果未携带 token，则不校验 token
        boolean optionalTokenUri = false; // 其他接口，都务必需要携带 token，并且需要成功校验 token
        List<String> maybeAuthUris = authConfigAdapter.maybeAuthUri();
        if (CollectionUtil.isNotEmpty(maybeAuthUris)) {
            for (String maybe : maybeAuthUris) {
                if (pathMatcher.match(maybe, requestUri)) {
                    optionalTokenUri = true;
                    break;
                }
            }
        }

        String token = req.getHeader("token");
        String accessToken = req.getHeader("Authorization");
        if (StrUtil.isBlank(accessToken) || Objects.equals(accessToken, "null")) {
            accessToken = token;
        }
        if (StrUtil.isBlank(accessToken) || Objects.equals(accessToken, "null")) {
            accessToken = req.getParameter("token");
        }
        if (StrUtil.isBlank(accessToken) || Objects.equals(accessToken, "null")) {
            accessToken = "";
        }
        if (requestUri.contains("adminLogin")) {
            logger.info("---> requestUri:{}, accessToken:*{}*", requestUri, accessToken);
        }

        UserInfoInTokenBO userInfoInToken = null;
        String clientIp = IPHelper.getIpAddr();
        String userCode = "";
        // 解析token的异常对象
        YamiShopBindException tokenErr = null;
        if (StrUtil.isNotBlank(accessToken)) {
            try {
                userInfoInToken = tokenStore.getUserInfoByAccessToken(accessToken, true);
                if (userInfoInToken.getSysType().intValue() == SysTypeEnum.ORDINARY.value().intValue()) {
                    String userId = userInfoInToken.getUserId();
                    // 缓存优化 TODO
                    User userEntity = userService.cacheUserBy(userId);
                    if (userEntity != null) {
                        userCode = userEntity.getUserCode();
                    }
                }
            } catch (Exception e) {
                if (e instanceof YamiShopBindException) {
                    logger.error("---> AuthFilter doFilter 处理 uri:{}, accessToken:{} 报 YamiShopBindException 异常:{}", requestUri, accessToken, e.getMessage());
                    tokenErr = (YamiShopBindException)e;
                } else {
                    logger.error("---> AuthFilter doFilter 处理 uri:{}, accessToken:{} 报错:", requestUri, accessToken, e);
                    throw e;
                }
            }
        }
        // 处理黑名单访问，断网逻辑
        if (checkBlackRequest(req, resp, clientIp, userCode)) {
            return;
        }

        try {
            // 识别时区信息
            processTimezone(req);

            // 白名单
            if (servletPathWhiteUri) {
                chain.doFilter(req, resp);
                return;
            }

            //
            if (ObjectUtils.isNotEmpty(VERSION_NUMBER)) {
                // 验证时间戳签名
                if (checkSign(req,response)) {
                    return;
                }
            }

            // 当前 uri 不用检查是否携带 token，直接执行对应的接口
            if (ignoreTokenUri) {
                chain.doFilter(req, resp);
                return;
            }

            // 有 token 就用，没 token 也无所谓的 api
            if (userInfoInToken != null) {
                // 如果有 token，并且解析成功，则走以下处理逻辑
                if (userInfoInToken.getSysType().intValue() == SysTypeEnum.ADMIN.value().intValue()) {
                    if (!pathMatcher.match("/updateCheckIp", requestUri)) {
                        Object loginIP = RedisUtil.get(RedisKeys.ACCESS_IP + userInfoInToken.getUserId());
                        if (null != loginIP && !IPHelper.equalIpSegment(loginIP.toString(), clientIp)) {
                            logger.error("The Login IP Is Inconsistent With The Operation IP! Login-IP:{} Access-IP:{} Servlet-Path:{}", loginIP, clientIp, servletPath);
                            httpHandler.printServerResponseToWeb("", 1001);
                            return;
                        }
                    }
                }

                // 保存上下文
                AuthUserContext.set(userInfoInToken);
            } else if (!optionalTokenUri) {
                // token 必填的路径
                // 如果没有 token，或者 token 解析失败/过期，但是当前请求 uri 又不是一个可选 token 的uri，则报错，提示 token 无效
                // 返回前端401
                logger.error("---> requestUri:{} 未配置 optional 白名单", requestUri);
                httpHandler.printServerResponseToWeb("您的账号已过期或已经在其他地方登录，请重新登录", 403);
                return;
            }
            if (tokenErr != null) {
                // 前面解析 token 报错，此处抛出
                throw tokenErr;
            }

            // token 逻辑校验顺利
            chain.doFilter(req, resp);
        } catch (Exception e) {
            // 手动捕获下非controller异常
            if (e instanceof YamiShopBindException) {
                httpHandler.printServerResponseToWeb("您的账号已经在其他地方登录", 403);
                return;
            } else {
                logger.error("---> AuthFilter requestUri:{}, accessToken:{}, 请求处理报错: ", requestUri, accessToken, e);
                throw e;
            }
        } finally {
            AuthUserContext.clean();
            TimeZoneContext.clientTimeZoneId.remove();
            TimeZoneContext.showTimeZoneId.remove();
        }
    }

    /**
     * 普通请求处理处理
     * @throws IOException
     */
    public boolean checkSign(HttpServletRequest request, ServletResponse response) throws IOException {
        String servletPath2 = request.getServletPath();
        // 响应请求前参数校验
        // 获取请求头中的时间戳参数
        String timestamp = request.getHeader("tissuePaper");
        String systemRandom = request.getHeader("systemRandom");
        String waitSign = ENCRYPTION_KEY+timestamp;

        if (timestamp == null) {
            // 没有时间戳参数返回验签失败
            logger.error("---> AuthFilter checkSign 时间戳为空:{} ", servletPath2);
            ((HttpServletResponse)response).sendError(201, "时间戳为空");
            return true;
        }

        long currDate = System.currentTimeMillis()/1000L;
        long oldDate = currDate - 60;
        if (ADMINCACHEMAP.size() > 500) {
            for (Map.Entry<String, Long> entry : ADMINCACHEMAP.entrySet()) {
                if (entry.getValue().longValue() < oldDate) {
                    ADMINCACHEMAP.remove(entry.getKey());
                }
            }
        }
        if (ObjectUtils.isEmpty(systemRandom)) {
            // 没有时间戳参数返回验签失败
            logger.error("---> AuthFilter checkSign 时间戳和随机数为空:{}", servletPath2);
            ((HttpServletResponse)response).sendError(207, "时间戳和随机数为空");
            return true;
        }
        String key = ItemRedisKeys.ITEM_ADMIN_SYSTEM_RANDOM;
        if(ObjectUtils.isNotEmpty(ADMINCACHEMAP)) {
            if(ObjectUtils.isNotEmpty(ADMINCACHEMAP.get(key+systemRandom))) {
                //请求时间一致
                logger.error("---> AuthFilter checkSign 当前请求时间戳和随机数和上次一样,请求拒绝:{} ", servletPath2);
                ((HttpServletResponse)response).sendError(208, "当前请求时间戳和随机数和上次一样,请求拒绝");
                return true;
            }
        }
        ADMINCACHEMAP.put(key+systemRandom,Long.parseLong(timestamp));
        waitSign = ENCRYPTION_KEY+timestamp+systemRandom;


        try {
            // 20秒内有效
            long timestampDate = Long.parseLong(timestamp) + (60 * 2);
            if (timestampDate < currDate) {
                // 请求过期
                logger.error("---> AuthFilter checkSign 请求过期:{} ", servletPath2);
                ((HttpServletResponse)response).sendError(202, "请求过期");
                return true;
            }
        } catch (NumberFormatException e) {
            assert response != null;
            logger.error("---> AuthFilter checkSign 请求异常:{}", servletPath2);
            ((HttpServletResponse)response).sendError(204, "请求异常");
            return true;
        }

        String sign = request.getHeader("sign");
        if (sign == null || "".equals(sign.trim())) {
            // 没有签名返回验签失败
            assert response != null;
            logger.error("---> AuthFilter checkSign 签名为空:{}", servletPath2);
            ((HttpServletResponse)response).sendError(205, "签名为空");
            return true;
        }

        // 验签， 根据时间戳生成签名加盐值反复加密两次， 对比是否一致
        // 第一个参数为加密内容， 第二个参数为加密时的盐值
        // 获取后台管理MD5盐值
        String md5_result = MD5.sign(waitSign).toUpperCase();
        if (!md5_result.equals(sign)) {
            // 验签失败
            logger.error("---> AuthFilter checkSign 签名失败:{}", servletPath2);
            ((HttpServletResponse)response).sendError(206, "签名失败");
            return true;
        }

        return false;
    }

    private void processTimezone(HttpServletRequest req) throws IOException {
        // 前端定制了展示数据使用的时区
        String showTimeZone = req.getParameter("timezone");
        if (StrUtil.isBlank(showTimeZone)) {
            showTimeZone = req.getHeader("x-api-timezone");
        }
        if (StrUtil.isBlank(showTimeZone)) {
            // 默认使用配置的时区
            showTimeZone = ApplicationUtil.getProperty("config.timezone.show", "");
        } else {
            if (showTimeZone.contains("%")) {
                showTimeZone = URLDecoder.decode(showTimeZone, "UTF-8");
            }
        }
        if (StrUtil.isBlank(showTimeZone) || Objects.equals(showTimeZone, "default")) {
            // 默认使用存储数据使用的时区，内部 feign 调用时，需要传固定值：default，代表不执行时间转换
            showTimeZone = ZoneId.systemDefault().getId();
        }

        // 校验传入的 tz 格式是否正确
        ZoneId.of(showTimeZone);
        TimeZoneContext.showTimeZoneId.set(showTimeZone);

        // 前端告知本地使用的时区
        String clientTimeZone = req.getHeader("x-api-client-timezone");
        if (StrUtil.isBlank(clientTimeZone)) {
            clientTimeZone = ZoneId.systemDefault().getId();
        }
        // 校验传入的 tz 格式是否正确
        ZoneId.of(clientTimeZone);
        TimeZoneContext.clientTimeZoneId.set(clientTimeZone);
    }

    private boolean checkBlackRequest(HttpServletRequest req, HttpServletResponse resp, String clientIp, String userCode) {
        String requestUri = req.getRequestURI();
        if (requestUri.contains("riskclient/save") || requestUri.contains("demo/checkip")) {
            return false;
        }
        //logger.info("---> AuthFilter.checkBlackRequest 中的 urlBuffer = {}, clientIp:{}, userId:{}", requestUri, clientIp, userId);
        if (StrUtil.isNotBlank(userCode) && !Objects.equals(userCode, "0")) {
            // 先处理断网逻辑
            List<RiskClient> riskList = RiskClientUtil.getRiskInfoByUserCode(userCode, "badnetwork");
            if (CollectionUtil.isNotEmpty(riskList)) {
                logger.info("---> AuthFilter.checkBlackRequest 当前用户断网 requestUri = {}, clientIp:{}, userCode:{}", requestUri, clientIp, userCode);
                // 模拟基于用户断网
                //closeAction(requestUri, resp);
                return true;
            }

            // 处理黑名单逻辑
            riskList = RiskClientUtil.getRiskInfoByUserCode(userCode, "black");
            if (CollectionUtil.isNotEmpty(riskList)) {
                try {
                    logger.info("---> AuthFilter.checkBlackRequest 当前用户断网 requestUri = {}, clientIp:{}, userCode:{}", requestUri, clientIp, userCode);
                    resp.setCharacterEncoding(CharsetUtil.UTF_8);
                    resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    resp.setStatus(200);
                    //req.getRequestDispatcher("/html/404").forward(req, resp); // 断网展示网页，使用 forward 请求转发
                    String responseJson = "{\"code\":1,\"msg\":\"Forbidden\"}";
                    resp.getWriter().write(responseJson);
                } catch (Exception e) {
                    logger.error("---> AuthFilter.checkBlackRequest requestUri:{} 访问报错: ", requestUri, e);
                }
                return true;
            }
        }

        // 未能基于用户来识别，则继续尝试基于 ip 判断
        List<RiskClient> riskList = RiskClientUtil.getRiskInfoByIp(clientIp, "badnetwork");
        if (CollectionUtil.isNotEmpty(riskList)) {
            logger.info("---> AuthFilter.checkBlackRequest 当前IP断网 requestUri = {}, clientIp:{}", requestUri, clientIp);
            // 模拟基于客户端ip断网
            //closeAction(requestUri, resp);
            return true;
        }

        // 处理黑名单逻辑
        riskList = RiskClientUtil.getRiskInfoByIp(clientIp, "black");
        if (CollectionUtil.isNotEmpty(riskList)) {
            try {
                logger.info("---> AuthFilter.checkBlackRequest 当前IP断网 requestUri = {}, clientIp:{}", requestUri, clientIp);
                resp.setCharacterEncoding(CharsetUtil.UTF_8);
                resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                resp.setStatus(200);
                //req.getRequestDispatcher("/html/404").forward(req, resp); // 断网展示网页，使用 forward 请求转发
                String responseJson = "{\"code\":1,\"msg\":\"Forbidden\"}";
                resp.getWriter().write(responseJson);
            } catch (Exception e) {
                logger.error("---> AuthFilter.checkBlackRequest requestUri:{} 访问报错: ", requestUri, e);
            }
            return true;
        }

        return false;
    }

    private static void closeAction(String requestUri, HttpServletResponse resp) {
        if (requestUri.contains("!close.action")) {
            ThreadUtils.sleep(3000);
            resp.setCharacterEncoding(CharsetUtil.UTF_8);
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            resp.setStatus(200);
            String responseJson = "{\"code\":1,\"msg\":\"Network Unavailable\"}";
            try {
                resp.getWriter().write(responseJson);
            } catch (IOException e) {
                throw new BusinessException(e);
            }
        }
    }
}
