package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 老客户表实体类
 */
@Data
@TableName("tz_user_old")
public class UserOld implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码（备用）
     */
    private String phoneAll;
}