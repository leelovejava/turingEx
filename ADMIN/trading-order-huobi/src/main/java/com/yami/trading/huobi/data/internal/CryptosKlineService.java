package com.yami.trading.huobi.data.internal;

import com.yami.trading.bean.data.domain.Kline;
import com.yami.trading.bean.data.domain.Realtime;

import java.util.List;

public interface CryptosKlineService {
    public void saveInit(String symbol);

    public void saveOne(String symbol, String line);

    public List<Kline> find(String symbol, String line, int pageSie);

    public void delete(String line, int days);

    public Kline bulidKline(Realtime realtime, Kline lastOne, Kline hobiOne, String line) ;
}
