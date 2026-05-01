package com.yami.trading.bean.worker.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_worker_order_content")
public class WorkerOrderContent {
    public static final int ROLE_USER = 1;
    public static final int ROLE_SYSTEM = 2;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long workerOrderId;
    private String content;
    private String memberId;
    private String account;
    private Integer replyRoleType;
    private Date localCreateTime;
    private Date createTime;
}
