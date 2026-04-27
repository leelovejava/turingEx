package com.yami.trading.api.dto;

import com.yami.trading.bean.data.domain.Realtime;
import lombok.Data;

@Data
public class BoardDto {
    private String boardName;
    private String boardPrice;
    private String boardRatio;
    private Realtime realtime;
}
