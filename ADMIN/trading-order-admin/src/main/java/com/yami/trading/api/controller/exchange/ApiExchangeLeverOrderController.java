package com.yami.trading.api.controller.exchange;

import com.yami.trading.bean.exchangelever.ExchangeLeverOrder;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.purchasing.dto.ExchangeLeverLock;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.exchange.ExchangeLeverOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController()
@CrossOrigin
@Slf4j
@RequestMapping("api/exchangeLeverOrder")
@Api(tags = "现货杠杆持仓 -api")
public class ApiExchangeLeverOrderController {
    @Autowired
    ExchangeLeverOrderService exchangeLeverOrderService;

    @Autowired
    WalletService walletService;

    @ApiOperation("列表")
    @PostMapping("list")
    public Result list(String page_no, String symbol, String type) {
        List<Map<String, Object>> data = exchangeLeverOrderService.getPaged(Integer.valueOf(page_no),
                10, SecurityUtils.getCurrentUserId(),
                symbol, type);
        return Result.succeed(data);
    }

    /**
     * 平仓
     */
    @ApiOperation("平仓")
    @PostMapping("close")
    public Result close(String order_no) {
        CloseDelayThread lockDelayThread = new CloseDelayThread(SecurityUtils.getCurrentUserId(), order_no,
                exchangeLeverOrderService);
        Thread t = new Thread(lockDelayThread);
        t.start();
        return Result.succeed();
    }

    @ApiOperation("获取详情")
    @PostMapping("get")
    public Result get(String order_no) throws IOException {
        ExchangeLeverOrder data = this.exchangeLeverOrderService.findByOrderNo(order_no);
       Wallet wallet= walletService.saveWalletByPartyId(SecurityUtils.getCurrentUserId());
        double sumVolume=     exchangeLeverOrderService.sumVolume(SecurityUtils.getCurrentUserId(),data.getSymbol());
        sumVolume+=wallet.getMoney().doubleValue();
        return Result.succeed(exchangeLeverOrderService.bulidOne(data,sumVolume));
    }

    /**
     * 新线程处理，直接拿到订单锁处理完成后退出
     */
    public class CloseDelayThread implements Runnable {
        private String partyId;
        private String order_no;
        private ExchangeLeverOrderService exchangeLeverOrderService;

        public void run() {
            try {
                while (true) {
                    if (ExchangeLeverLock.add(order_no)) {
                        exchangeLeverOrderService.saveClose(partyId, order_no);
                        /**
                         * 处理完退出
                         */
                        break;
                    }
                    ThreadUtils.sleep(500);
                }
            } catch (Exception e) {
                log.error("error:", e);
            } finally {
                ExchangeLeverLock.remove(order_no);
            }
        }

        public CloseDelayThread(String partyId, String order_no, ExchangeLeverOrderService exchangeLeverOrderService) {
            this.partyId = partyId;
            this.order_no = order_no;
            this.exchangeLeverOrderService = exchangeLeverOrderService;
        }
    }
}
