package com.yami.trading.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.dto.UserNoticeDto;
import com.yami.trading.bean.model.UserNotice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface UserNoticeMapper extends BaseMapper<UserNotice> {

    @Delete("DELETE FROM t_user_notice WHERE uuid = #{uuid}")
    int deleteByUuid(@Param("uuid") String uuid);

    Page<UserNoticeDto> pageNotice(Page page, @Param("title") String title, @Param("noticeType") String noticeType,
                                   @Param("userCode") String userCode);
}
