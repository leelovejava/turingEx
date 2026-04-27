package com.yami.trading.bean.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.UUIDEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_c2c_translate")
public class C2cTranslate  extends UUIDEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 语言
	 */
	private String language;

	private String langKey;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 翻译
	 */
	private String translate;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 支付方式id，注意：对应的是 t_c2c_payment_method_config 表主键
	 */
	private String paymentMethodId;

}
