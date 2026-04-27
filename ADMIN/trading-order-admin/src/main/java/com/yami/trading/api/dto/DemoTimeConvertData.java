package com.yami.trading.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DemoTimeConvertData {

    private Date fromTime;

    private String keyword;

    private int status;

    private Date userTime;

    private Long userId;

}
