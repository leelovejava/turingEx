package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_notice")
public class UserNotice extends BaseEntity {

    /** 用户ID */
    private String userId;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 状态：1未读 2已读 */
    private Integer status;

    /** 通知类型 */
    private String noticeType;
}
