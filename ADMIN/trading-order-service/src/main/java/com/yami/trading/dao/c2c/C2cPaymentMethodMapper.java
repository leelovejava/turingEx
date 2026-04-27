package com.yami.trading.dao.c2c;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.c2c.dto.C2cPaymentMethodDto;
import com.yami.trading.bean.model.C2cPaymentMethod;
import org.apache.ibatis.annotations.Param;

public interface C2cPaymentMethodMapper extends BaseMapper<C2cPaymentMethod> {


    Page<C2cPaymentMethodDto> listPage(Page page, @Param("loginPartyId") String loginPartyId,
                                       @Param("userCode") String userCode,
                                       @Param("methodType") String methodType,
                                       @Param("methodName") String methodName,
                                       @Param("type")  int type);
}
