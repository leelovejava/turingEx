package com.yami.trading.dao.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.log.dto.LogDto;
import com.yami.trading.bean.model.Log;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogMapper  extends BaseMapper<Log> {

    Page<LogDto> listPage(Page page, @Param("rolename")  String rolename,
                          @Param("userName") String userName,
                          @Param("log") String log, @Param("category") String category,
                          @Param("operator") String operator,@Param("userIds") List<String> userIds,
                          @Param("isRoot")  boolean isRoot);

}
