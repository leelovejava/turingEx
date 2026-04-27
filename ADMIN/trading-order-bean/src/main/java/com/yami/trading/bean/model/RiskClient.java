package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;

import java.util.Date;

/**
 * 风控配置
 */
@Data
@TableName("t_risk_client")
public class RiskClient extends UUIDEntity {
    /**
     * 黑名单
     */
    public static final String BLACK = "black";

    /**
     * 断网
     */
    public static final String BAD_NETWORK = "badnetwork";

    /**
     * 不使用空值填充记录的时间值，约定该值代表一个不限时间的值
     */
    public static final String INIT_TIME = "2023-01-01 00:00:00";

    /**
     * 客户端值，可为 ip值（支持*号占位符）或 userCode 值
     */
    private String clientKey;

    /**
     * 客户端值类型：ip, userCode
     */
    private String clientType;

    private String clientName;

    /**
     * 类型 ：
     * black - 黑名单
     * white - 白名单
     * badnetwork - 断网
     *
     */
    private String type;

    /**
     * 0-无效，1-正常
     */
    private Integer status;

    /**
     * 起止时间，不支持空值
     * 当永久有效时设置为： 0
     */
    private Long beginTimeTs;

    /**
     * 起止时间，不支持空值
     * 当永久有效时设置为： 0
     */
    private Long endTimeTs;

    /**
     * 限制访问资源，扩展用途
     */
    //private String resources;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后处理时间
     */
    private Date lastOperaTime;

    private String remark;
}
