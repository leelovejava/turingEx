package com.yami.trading.huobi.data.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TimeseriesResult implements Serializable {

	private static final long serialVersionUID = -2654740103586762267L;

	/**
     * 收盘价
     */
    private double close;

    /**
     * 开盘价
     */
    private double open;

    /**
     * 最高价
     */
    private double high;

    /**
     * 最低价
     */
    private double low;

    /**
     * 时间
     */
    private String date;

}
