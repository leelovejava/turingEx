package com.yami.trading.admin.controller.dapp;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yami.trading.bean.model.User;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.*;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.bo.UserInfoInTokenBO;
import com.yami.trading.security.common.enums.SysTypeEnum;
import com.yami.trading.security.common.manager.TokenStore;
import com.yami.trading.security.common.vo.TokenInfoVO;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * dapp api
 *
 */
@RestController
@CrossOrigin
public class DappController {
	
	private static final Logger logger = LoggerFactory.getLogger(DappController.class);
	
	@Resource
	private DAppService dAppService;
//	@Resource
//	private AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	@Resource
	private UserService UserService;
	// todo
	// @Resource
	// private PledgeOrderService pledgeOrderService;
	@Resource
	private WalletService walletService;
	@Resource
	private SysparaService sysparaService;
	@Resource
	private DataService dataService;
	@Resource
	private SessionTokenService tokenService;
	// todo
//	@Resource
//	private PledgeConfigService pledgeConfigService;
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private UserService userService;
	@Autowired
	private UserDataService userDataService;
	
	public final String action = "/api/dapp!";

	

	/**
	 * Dapp登录注册:备注 Snowball 盘 定制接口，等他下盘了，就删掉。
	 */
	@RequestMapping(action + "login.action")
	public Object login(String from, String code){
		ResultObject resultObject = new ResultObject();
		boolean lock = false;
		from = from.trim().toLowerCase();
		try {
			
			if (StringUtils.isNullOrEmpty(from)) {
				throw new BusinessException("address is empty");
			}
			
			Syspara syspara = sysparaService.find("project_type");
			if (null == syspara) {
				throw new BusinessException("projectType is not found");
			}
			
			// if (!syspara.getSvalue().contains("DAPP_EXCHANGE")) {
			// 	throw new BusinessException("Bad Request");
			// }
			
			String ip = this.getIp();
			if (!IpUtil.isCorrectIpRegular(ip)) {
				logger.error("校验IP不合法,参数{}", ip);
				throw new BusinessException("校验IP不合法");
			}
			
			if (!LockFilter.add(from)) {
				resultObject.setCode("0");
				return resultObject;
			}
			lock = true;
			
			User user = dAppService.saveLogin(from, code, ip);
			if (null == user) {
				throw new BusinessException("User is null");
			}
			// todo
//			if (User.getLogin_authority()==false) {
//				throw new BusinessException("登录失败");
//			}
			UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
			userInfoInToken.setUserId(user.getUserId());
			userService.online(user.getUserId());
			userInfoInToken.setSysType(SysTypeEnum.ORDINARY.value());
			userInfoInToken.setEnabled(user.getStatus() == 1);
			userDataService.saveRegister(user.getUserId());
			tokenStore.deleteAllToken(String.valueOf(SysTypeEnum.ORDINARY.value()), String.valueOf(user.getUserId()));
			// 存储token返回vo
			TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
			tokenInfoVO.setToken(tokenInfoVO.getAccessToken());
			user.setUserLastip(IPHelper.getIpAddr());
			user.setUserLasttime(new Date());
			userService.updateById(user);


//			String token = tokenService.savePut(String.valueOf(user.getUserId()));
			
			Map<String, Object> data = new HashMap<String, Object>();
			
			// 收益率 和usdt汇率
//			data.putAll(dAppService.getProfit(from));
			// 是否弹出加入挖矿的提示(0不弹出 1弹出)
//			Syspara dapp_approve_auto = sysparaService.find("dapp_approve_auto");
//			data.put("pop_up", dapp_approve_auto == null ? 0 : dapp_approve_auto.getInteger());
			data.put("token", tokenInfoVO.getAccessToken());
			data.put("username", from);
//			data.put("identityverif", User.getKyc_authority());
			data.put("uid", user.getUserCode());
			data.put("obj", tokenInfoVO);
			resultObject.setData(data);
			logger.info("登录成功，用户{}", from);
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(50);
				LockFilter.remove(from);
			}
		}
		return resultObject;
	}

	public String getIp() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}

}
