package com.yami.trading.service.c2c;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.c2c.dto.C2cPaymentMethodDto;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface C2cPaymentMethodService   extends IService<C2cPaymentMethod> {

    Page<C2cPaymentMethodDto> listPage(Page page,  String loginPartyId,
                                       String userCode,
                                       String methodType,
                                       String methodName,
                                       int type);

    public Map<String, C2cPaymentMethod> getByPartyId(String partyId);


    public Map<String, C2cPaymentMethod> getByC2CPartyId(String partyId);

    public Map<String, C2cPaymentMethod> getByBankCardPartyId(String partyId);


    String saveData(C2cPaymentMethod method);

    public C2cPaymentMethod get(String id);

    public C2cPaymentMethod getC2cPaymentMethod(String id);

    /*
     * 获取 C2C支付币种配置、C2C广告支付时效
     */
    public Map<String, String> getC2cSyspara(String syspara);
    /**
     * 获取 支付币种Map
     */
    public Map<String, String> getCurrencyMap();
    public List<C2cPaymentMethod> getMethodConfigListByPartyId(String partyId);

}
