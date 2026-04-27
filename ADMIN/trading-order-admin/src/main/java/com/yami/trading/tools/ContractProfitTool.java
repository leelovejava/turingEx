package com.yami.trading.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ContractProfitTool {
    public static void main(String[] args) {
        // 平仓价格
        BigDecimal currentPrice = new BigDecimal("15412.08");

        // 建仓价格
        BigDecimal tradeAvgPrice = new BigDecimal("15415.64");

        BigDecimal pips = new BigDecimal("1");
        BigDecimal pipsAmount = new BigDecimal("4");


        // 开仓量， select volume，volume_open from t_contract_order where order_no  like '%325784'  那个有值用那个
        BigDecimal volume = new BigDecimal("15165");

        /**
         * 偏差点位
         */
        BigDecimal point = currentPrice.subtract(tradeAvgPrice).abs().divide(pips, 10, RoundingMode.HALF_UP);
        /*
         * 根据偏 差点数和手数算出盈亏金额
         */
        BigDecimal amount = pipsAmount.multiply(point).multiply(volume);

        System.out.println(amount.toPlainString());
    }
}
