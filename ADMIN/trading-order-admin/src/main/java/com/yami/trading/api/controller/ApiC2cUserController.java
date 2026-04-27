package com.yami.trading.api.controller;

import com.yami.trading.bean.c2c.C2cAdvert;
import com.yami.trading.bean.c2c.C2cUser;
import com.yami.trading.bean.c2c.C2cUserParamBaseSet;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.PayTemplateParamEnum;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.*;
import com.yami.trading.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * C2C承兑商
 */
@RestController
@CrossOrigin
public class ApiC2cUserController {
    @Autowired
    private C2cUserParamBaseSetService c2cUserParamBaseSetService;
    @Autowired
    private C2cUserService c2cUserService;
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;
    @Autowired
    UserService userService;
    @Autowired
    C2cAdvertService c2cAdvertService;
    @Autowired
    C2cPaymentMethodConfigService c2cPaymentMethodConfigService;
    @Autowired
    C2cTranslateService c2cTranslateService;

    /**
     * 获取 承兑商 详情
     */
    @RequestMapping("/api/c2cUser!get.action")
    public Result get(HttpServletRequest request) {
        String c2c_user_id = request.getParameter("c2c_user_id");
        String language = request.getParameter("language");
        Map<String, Object> result = new HashMap<>();
        C2cUser c2cUser = this.c2cUserService.getById(c2c_user_id);
        if (null == c2cUser) {
            throw new BusinessException("承兑商不存在");
        }
        C2cUserParamBaseSet paramBaseSet = c2cUserParamBaseSetService.getByPartyId(c2cUser.getC2cUserPartyId());
        if (null == paramBaseSet) {
            throw new BusinessException("承兑商参数基础设置不存在");
        }
        if (StringUtils.isNotEmpty(c2cUser.getHeadImg())) {
            c2cUser.setHeadImg(awsS3OSSFileService.getUrl(c2cUser.getHeadImg()));
        }
        User party = userService.getById(c2cUser.getC2cUserPartyId());
        if (null == party) {
            throw new BusinessException("承兑商的用户信息不存在");
        }

        DecimalFormat df = new DecimalFormat("#.########");
        c2cUser.setThirtyDaysOrder(paramBaseSet.getThirtyDaysOrder() + c2cUser.getThirtyDaysOrder());
        c2cUser.setThirtyDaysOrderRatio(0 != paramBaseSet.getThirtyDaysOrderRatio() ? paramBaseSet.getThirtyDaysOrderRatio() : c2cUser.getThirtyDaysOrderRatio());
        c2cUser.setThirtyDaysPassAverageTime(0 != paramBaseSet.getThirtyDaysPassAverageTime() ? paramBaseSet.getThirtyDaysPassAverageTime() : c2cUser.getThirtyDaysPassAverageTime());
        c2cUser.setThirtyDaysPayAverageTime(0 != paramBaseSet.getThirtyDaysPayAverageTime() ? paramBaseSet.getThirtyDaysPayAverageTime() : c2cUser.getThirtyDaysPayAverageTime());
        c2cUser.setThirtyDaysAmount(Double.valueOf(df.format(Arith.add(paramBaseSet.getThirtyDaysAmount(), c2cUser.getThirtyDaysAmount()))).doubleValue());
        c2cUser.setBuyAmount(Double.valueOf(df.format(Arith.add(paramBaseSet.getBuyAmount(), c2cUser.getBuyAmount()))).doubleValue());
        c2cUser.setSellAmount(Double.valueOf(df.format(Arith.add(paramBaseSet.getSellAmount(), c2cUser.getSellAmount()))).doubleValue());
        c2cUser.setTotalAmount(Double.valueOf(df.format(Arith.add(paramBaseSet.getTotalAmount(), c2cUser.getTotalAmount()))).doubleValue());
        c2cUser.setAccountCreateDays(paramBaseSet.getAccountCreateDays() + c2cUser.getAccountCreateDays());
        c2cUser.setFirstExchangeDays(paramBaseSet.getFirstExchangeDays() + c2cUser.getFirstExchangeDays());
        c2cUser.setExchangeUsers(paramBaseSet.getExchangeUsers() + c2cUser.getExchangeUsers());
        c2cUser.setBuySuccessOrders(paramBaseSet.getBuySuccessOrders() + c2cUser.getBuySuccessOrders());
        c2cUser.setSellSuccessOrders(paramBaseSet.getSellSuccessOrders() + c2cUser.getSellSuccessOrders());
        c2cUser.setTotalSuccessOrders(paramBaseSet.getTotalSuccessOrders() + c2cUser.getTotalSuccessOrders());
        c2cUser.setAppraiseGood(paramBaseSet.getAppraiseGood() + c2cUser.getAppraiseGood());
        c2cUser.setAppraiseBad(paramBaseSet.getAppraiseBad() + c2cUser.getAppraiseBad());
        c2cUser.setOrderMailNoticeOpen(1 == paramBaseSet.getOrderMailNoticeOpen() || 1 == c2cUser.getOrderMailNoticeOpen() ? 1 : 0);
        c2cUser.setOrderSmsNoticeOpen(1 == paramBaseSet.getOrderSmsNoticeOpen() || 1 == c2cUser.getOrderSmsNoticeOpen() ? 1 : 0);
        c2cUser.setOrderAppNoticeOpen(1 == paramBaseSet.getOrderAppNoticeOpen() || 1 == c2cUser.getOrderAppNoticeOpen() ? 1 : 0);
        c2cUser.setAppealMailNoticeOpen(1 == paramBaseSet.getAppealMailNoticeOpen() || 1 == c2cUser.getAppealMailNoticeOpen() ? 1 : 0);
        c2cUser.setAppealSmsNoticeOpen(1 == paramBaseSet.getAppealSmsNoticeOpen() || 1 == c2cUser.getAppealSmsNoticeOpen() ? 1 : 0);
        c2cUser.setAppealAppNoticeOpen(1 == paramBaseSet.getAppealAppNoticeOpen() || 1 == c2cUser.getAppealAppNoticeOpen() ? 1 : 0);
        c2cUser.setChatAppNoticeOpen(1 == paramBaseSet.getChatAppNoticeOpen() || 1 == c2cUser.getChatAppNoticeOpen() ? 1 : 0);
        c2cUser.setSecurityMailNoticeOpen(1 == paramBaseSet.getSecurityMailNoticeOpen() || 1 == c2cUser.getSecurityMailNoticeOpen() ? 1 : 0);
        c2cUser.setSecuritySmsNoticeOpen(1 == paramBaseSet.getSecuritySmsNoticeOpen() || 1 == c2cUser.getSecuritySmsNoticeOpen() ? 1 : 0);
        c2cUser.setSecurityAppNoticeOpen(1 == paramBaseSet.getSecurityAppNoticeOpen() || 1 == c2cUser.getSecurityAppNoticeOpen() ? 1 : 0);
        Map<String, Object> partyMap = new HashMap<String, Object>();
        partyMap.put("phoneAuthority", "Y".equals(paramBaseSet.getPhoneAuthority()) || true == party.isUserMobileBind() ? true : false);
        partyMap.put("emailAuthority", "Y".equals(paramBaseSet.getEmailAuthority()) || true == party.isMailBind() ? true : false);
        partyMap.put("kycAuthority", "Y".equals(paramBaseSet.getKycAuthority()) || true == party.isRealNameAuthority() ? true : false);
        partyMap.put("kycHighlevelAuthority", "Y".equals(paramBaseSet.getKycHighlevelAuthority()) || true == party.isHighlevelAuthority() ? true : false);
        List<C2cAdvert> adverts = this.c2cAdvertService.getByC2cUserId(c2c_user_id);
        for (int i = 0; i < adverts.size(); i++) {
            C2cAdvert adv = adverts.get(i);
            if (null != adv) {
                if (StringUtils.isNotEmpty(adv.getPayType())) {
                    List<String> pay_type = new LinkedList<String>();
                    String[] types = adv.getPayType().split(",");
                    for (String type : types) {
                        C2cPaymentMethodConfig method = c2cPaymentMethodConfigService.get(type.trim());
                        if (null != method) {
                            String payParamLangKeyPrefix = "pay.param.";
                            String payMethodNameLangKey = payParamLangKeyPrefix + PayTemplateParamEnum.METHOD_NAME.getCode() + "." + method.getUuid();

                            C2cTranslate trans = c2cTranslateService.get(method.getUuid(), payMethodNameLangKey, language);
                            if (null != trans) {
                                pay_type.add(trans.getTranslate());
                            } else {
                                pay_type.add(method.getMethodName());
                            }
                        }
                    }
                    adv.setPayTypeName(String.join(",", pay_type));
                }
            }
        }
        result.put("c2c_user", c2cUser);
        result.put("party", partyMap);
        result.put("advert", adverts);
        return Result.succeed(result);
    }
}
