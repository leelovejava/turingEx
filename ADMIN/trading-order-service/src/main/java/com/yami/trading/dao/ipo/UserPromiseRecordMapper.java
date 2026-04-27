package com.yami.trading.dao.ipo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.ipo.Prospectus;
import com.yami.trading.bean.ipo.UserPromiseRecord;
import com.yami.trading.bean.ipo.dto.PromiseTopDto;
import com.yami.trading.bean.ipo.dto.SumUserPromiseDto;
import com.yami.trading.bean.ipo.dto.UserListDto;
import com.yami.trading.bean.ipo.dto.UserPromiseListDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UserPromiseRecordMapper   extends BaseMapper<UserPromiseRecord> {

    SumUserPromiseDto sumPromise(@Param("userId") String userId, @Param("productCode")  String productCode);

    Page<UserPromiseListDto> pageUserPromiseData(Page page, @Param("userId") String userId,
                                                  @Param("symbols")    List<String> symbols);



    PromiseTopDto topData(@Param("userId") String userId);


    Page<UserListDto> pagePromiseData(Page page,
                                      @Param("userCode") String userCode,
                                      @Param("productName") String productName,
                                      @Param("roleName") String roleName,
                                      @Param("productCode") String productCode,
                                      @Param("name") String name,
                                      @Param("status")  Integer status,
                                      @Param("children") List<String> children);

}
