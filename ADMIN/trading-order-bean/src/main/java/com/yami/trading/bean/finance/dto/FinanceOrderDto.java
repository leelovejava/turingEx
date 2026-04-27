package com.yami.trading.bean.finance.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class FinanceOrderDto {


    private String uuid;

    /**
     * 用户ID
     */
    @JSONField(name = "party_id")
    private String partyId;

    /**
     * 订单 号
     */
    @JSONField(name = "order_no")
    private String orderNo;

    /**
     * 理财产品名称
     */
    @JSONField(name = "name")
    private String name;

    /**
     * 理财产品名称繁体
     */
    @JSONField(name = "name_cn")
    private String nameCn;

    /**
     * 理财产品名称英文
     */
    @JSONField(name = "name_en")
    private String nameEn;

    /**
     * 理财产品Id
     */
    @JSONField(name = "finance_id")
    private String financeId;

    /**
     * 金额
     */
    @JSONField(name = "amount")
    private double amount;

    /**
     * 买入时间
     */
    @JSONField(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 起息时间 从买入时间第二天开始算
     */
    @JSONField(name = "earn_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date earnTime;

    /**
     * 截止时间
     */
    @JSONField(name = "stop_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date stopTime;

    /**
     * 赎回时间=截止时间+1天
     */
    @JSONField(name = "close_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closeTime;

    /**
     * 收益
     */
    @JSONField(name = "profit")
    private double profit;
    /**
     * 之前或累计收益
     */
    @JSONField(name = "profit_before")
    private double profitBefore;

    /**
     * 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
     */
    @JSONField(name = "state")
    private String state = "1";
    /**
     * 托管时间，周期
     */
    @JSONField(name = "cycle")
    private int cycle;

}
