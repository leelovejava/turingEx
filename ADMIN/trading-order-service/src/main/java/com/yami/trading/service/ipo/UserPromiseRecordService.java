package com.yami.trading.service.ipo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.ipo.ApplyNewSharesOrder;
import com.yami.trading.bean.ipo.NewSharesConfig;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.ipo.dto.PromiseTopDto;
import com.yami.trading.bean.ipo.dto.UserListDto;
import com.yami.trading.bean.ipo.dto.UserPromiseListDto;

import java.util.List;

public interface UserPromiseRecordService    extends IService<UserPromiseRecord> {
    void applyPromise(NewSharesConfig newSharesConfig, UserPromiseRecord userPromiseRecord, ApplyNewSharesOrder applyNewSharesOrder);

    public Page<UserPromiseListDto> pageUserPromiseData(Page page, String userId,
                                                        List<String> symbols);
    PromiseTopDto topData( String userId);
    Page<UserListDto> pagePromiseData(Page page,
                                      String userCode,
                                      String productName,
                                      String rolename, String productCode, String name,
                                      Integer status , List<String> children);

    public void sell(String orderNo, String userId);

    List<UserPromiseRecord> findByOrderNo(List<String> orderNos);

    List<UserPromiseRecord> findByOrderNo(String orderNo);

    List<UserPromiseRecord> findByProductCodeAndUserId(String productCode, String userId);

    List<UserPromiseRecord> findByProductCode(String productCode);

}
