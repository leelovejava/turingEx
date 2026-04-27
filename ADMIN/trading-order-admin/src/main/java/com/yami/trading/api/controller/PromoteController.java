package com.yami.trading.api.controller;

import com.yami.trading.bean.model.UserData;
import com.yami.trading.bean.user.dto.ChildrenLever;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 我的推广
 */
@RestController
@CrossOrigin
@Slf4j
public class PromoteController {
    @Autowired
    protected UserRecomService userRecomService;
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected UserService partyService;
    @Autowired
    protected SysparaService sysparaService;

    @RequestMapping("api/promote!getPromote.action")
    public Result getPromote(HttpServletRequest request) {
        // 层级 1为第一级 1,2,3,4总共4级代理
        String  level_temp= request.getParameter("level");
        if (StringUtils.isNullOrEmpty(level_temp)
                || !StringUtils.isInteger(level_temp) || Integer.valueOf(level_temp) <= 0) {
            throw new YamiShopBindException("代理层级错误");
        }
        int level = Integer.valueOf(level_temp);
        String page_no = request.getParameter("page_no");
        if (StringUtils.isNullOrEmpty(page_no)
                || !StringUtils.isInteger(page_no) || Integer.valueOf(page_no) <= 0) {
            page_no = "1";
        }
        int pageNo = Integer.valueOf(page_no);
        String partyId = SecurityUtils.getUser().getUserId();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> data_total = new HashMap<String, Object>();
        List<Map<String, Object>> dataChilds = new ArrayList<Map<String, Object>>();
        ChildrenLever childrenLever = userDataService.cacheChildrenLever4(partyId);
        data.put("children", childrenLever.getLever1().size()
                + childrenLever.getLever2().size()
                + childrenLever.getLever3().size());
        data.put("level_1", childrenLever.getLever1().size());
        data.put("level_2", childrenLever.getLever2().size());
        data.put("level_3", childrenLever.getLever3().size());
//			data.put("level_4", childrenLever.getLever4().size());
        data_total.put("total", data);
        // 资金盘 定制化需求，后面盘口下架可以删
        dataChilds = this.userDataService.getChildrenLevelPagedForGalaxy(pageNo, 10, partyId, level);

        Map<String, UserData> map = userDataService.cacheByPartyId(partyId);
        double sum = 0;
        if (null != map && map.size() > 0) {
            for (UserData userData : map.values()) {
                sum += userData.getRecharge();
            }
        }
        // 总充值
        data_total.put("recharge_sum", sum);

        // 加密用户名
        handleChilds(dataChilds);
        data_total.put("list", dataChilds);
        return Result.succeed(data_total);
    }

    /**
     * 交易所-数据总览-PC端
     */
    @RequestMapping( "api/promote!getPromoteData.action")
    public Result getPromoteData(HttpServletRequest request) {
        String partyId = SecurityUtils.getUser().getUserId();
        Map<String, String> dataMap = new HashMap<>();
        try {
            Date date = new Date();
            Date startTime = null;
            Date endTime = null;
            String type = request.getParameter("type");
            if (type == null) {
                return Result.failed("类型不能为空");
            }
            if (type.equals("day")) {
                startTime = DateUtils.getDayStart(DateUtils.addDate(date, 1));
                endTime = DateUtils.getDayEnd(DateUtils.addDate(date, 1));
            } else if (type.equals("week")) {
                startTime = DateUtil.getFirstDateOfWeek(date);
                endTime = DateUtil.getLastDateOfWeek(date);
            } else if (type.equals("month")) {
                startTime = DateUtil.getFirstDateOfMonth(date);
                endTime = DateUtil.getLastDateOfMonth(date);
            }
            System.out.println("推广数据总览 开始时间" + startTime);
            System.out.println("推广数据总览 结束时间" + endTime);
            dataMap = userDataService.getPromoteData(partyId, dataMap, startTime, endTime);

            Map<String, UserData> map = userDataService.cacheByPartyId(partyId);
            double sum = 0;
            if (null != map && map.size() > 0) {
                for (UserData userData : map.values()) {
                    sum += userData.getRechargeRecom();
                }
            }

            dataMap.put("rechargeRecom", String.valueOf(sum));

            return Result.succeed(dataMap);
        } catch (BusinessException e) {
            return Result.failed(e.getMessage());
        } catch (Throwable e) {
            log.error("error:", e);
            return Result.failed("程序错误");
        }
    }

	/**
	 * 加密用户名
	 */
	protected void handleChilds(List<Map<String, Object>> dataChilds) {
		for (Map<String, Object> data : dataChilds) {
			String username = data.get("username").toString();
			int length = username.length();
			if (username.length() > 2) {
				data.put("username", username.substring(0, 3) + "***" + username.substring(length - 3));
			}
		}
	}
}
