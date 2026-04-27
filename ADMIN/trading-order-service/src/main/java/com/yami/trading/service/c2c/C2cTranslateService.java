package com.yami.trading.service.c2c;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.c2c.C2cOrder;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.C2cTranslate;
import com.yami.trading.bean.model.ChannelBlockchain;

import java.util.List;
import java.util.Map;

public interface C2cTranslateService  extends IService<C2cTranslate> {

    public C2cTranslate get(String id,String content, String language);
    public C2cTranslate get(String content, String language);

    public void saveC2cTranslate(C2cTranslate entity,String id);

    public void update(C2cTranslate entity);
    public void delete(String content, String language);

    public void delete(String  id,String content, String language);

    public void saveTranslate(String id, String langKey, String langTransStr, String content);

    public String getTranslate(String id, String langKey);

    public String getTranslate(String langKey);

    public C2cPaymentMethodConfig translatePmc(C2cPaymentMethodConfig config, String language);

    public C2cPaymentMethod translatePm(C2cPaymentMethod cpm, String language);

//    public C2cOrder translateOrder(C2cOrder order, String language);

    public List<String> getAllPaymentMethodTypeName();

    C2cOrder translateOrder(C2cOrder c2cOrder, String language);

    void translateOrder(Map map, String language);

}
