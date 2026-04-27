package com.yami.trading.common.web;

import java.io.Serializable;

public class ResultObject implements Serializable {
	private static final long serialVersionUID = 6952247764513362272L;
	private String code = "0";
	private String msg;
	private Object data;
	// private Object info;

	public String getCode() {
		return this.code;
	}

	public ResultObject setCode(String code) {
		this.code = code;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public ResultObject setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public Object getData() {
		return data;
	}

	public ResultObject setData(Object data) {
		this.data = data;
		return this;
	}

	// public Object getInfo() {
	// 	return info;
	// }

	// public void setInfo(Object info) {
	// 	this.info = info;
	// }

}
