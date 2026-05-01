package com.yami.trading.bean.worker.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_worker_order")
public class WorkerOrder {
    public static final int STATUS_PROCESSING = 1;
    public static final int STATUS_FINISHED = 2;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String workOrderStatus;
    private String workOrderSn;
    private String account;
    private String memberId;
    private Date localCreateTime;
    private Date createTime;
}
