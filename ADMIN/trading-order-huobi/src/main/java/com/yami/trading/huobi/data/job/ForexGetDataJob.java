package com.yami.trading.huobi.data.job;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.service.MarketOpenChecker;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.common.util.UTCDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ForexGetDataJob extends AbstractGetDataJob {

    @Override
    public void run() {

        if (first) {
            // data数据保存间隔时长(毫秒)
            this.interval = 3000;
            first = false;
        }
        while (true) {
            try {
                // 老外汇tradeView接口
                String symbols = itemService.findByType(Item.forex)
                        .stream().filter(i -> !i.getCategory().equalsIgnoreCase(Item.forex))
                        .map(Item::getSymbol).collect(Collectors.joining(","));
                // 新外汇
                String xinLangsymbols = itemService.findByType(Item.forex)
                        .stream().filter(i -> i.getCategory().equalsIgnoreCase(Item.forex))
                        .map(Item::getSymbol).collect(Collectors.joining(","));

                // 外汇指数
                String xueqiusymbols = itemService.findByType(Item.forex)
                        .stream().filter(i -> i.getCategory().equalsIgnoreCase(Item.indices))
                        .map(Item::getSymbol).collect(Collectors.joining(","));

                // 外汇贵金属
                String metas = itemService.findByType(Item.forex)
                        .stream().filter(i -> i.getOpenCloseType().equalsIgnoreCase(Item.UK_METALS))
                        .map(Item::getRemarks).collect(Collectors.joining(","));
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                f.setTimeZone(TimeZone.getTimeZone(UTCDateUtils.GMT_TIME_ZONE));
//		        log.info("当前UTS标准时间 {}", f.format(new Date()));
//				log.info("休市时间 {}" ,f.format(UTCDateUtils.getClosedTime()));
//				log.info("开市时间 {}", f.format(UTCDateUtils.getOpenTime()));
                Date now = f.parse(f.format(new Date()));
                this.realtimeHandleXueQiu(metas);
                this.realtimeHandleXueQiu(xueqiusymbols);
                this.realtimeHandle(symbols);
                this.realtimeHandleXinLang(xinLangsymbols);

            } catch (Exception e) {
                log.error("run fail", e);
            } finally {
                ThreadUtils.sleep(this.interval);
            }
        }

    }

    @Override
    public String getName() {
        return "外汇实时数据采集";
    }

    @Override
    public void realtimeHandle(String symbols) {
        if (StrUtil.isEmpty(symbols)) {
            log.error("当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = this.hobiDataService.realtime(symbols);
        super.handleRealTimeList(realtimeList);
    }
    public void realtimeHandleXinLang(String symbols) {
        if (StrUtil.isEmpty(symbols)) {
            log.error("当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = this.hobiDataService.realtimeXinLang(symbols);
        super.handleRealTimeList(realtimeList);
    }
    public void realtimeHandleXueQiu(String symbols) {
        if (StrUtil.isEmpty(symbols)) {
            log.error("当前没有行情数据可以采集");
            return;
        }
        List<Realtime> realtimeList = this.hobiDataService.realtimeXueQiu(symbols);
        super.handleRealTimeList(realtimeList);
    }

}
