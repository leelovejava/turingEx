package com.yami.trading.common.constants;

import java.util.HashMap;
import java.util.Map;

import com.yami.trading.common.util.ApplicationUtil;

public class TipConstants {
    ///
    /**
     * 外汇
     */
    public final static String forex = "forex";
    /**
     * 指数
     */
    public final static String indices = "indices";

    /**
     * 大宗商品
     */
    public final static String commodities = "commodities";

    /**
     * 虚拟货币
     */
    public final static String cryptos = "cryptos";


    /**
     * 美股
     */
    public final static String US_STOCKS = "US-stocks";

    /**
     * 港股
     */
    public final static String HK_STOCKS = "HK-stocks";

    public final static String TW_STOCKS = "TW-stocks";
    /**
     * 港股
     */
    public final static String A_STOCKS = "A-stocks";

    /**
     * 日股
     */
    public final static String JP_STOCKS = "JP-stocks";

    public final static String INDIA_STOCKS = "INDIA-stocks";
    /**
     * 英国股票
     */
    public final static String UK_STOCKS = "UK-stocks";
	/**
     * 德国股票
     */
    public final static String DE_STOCKS = "DE-stocks";
    /**
     * 巴西股票
     */
    public final static String BZ_STOCKS = "BZ-stocks";


    /**
     * 全球ETF
     */
    public final static String CATEGORY_GLOBAL = "global";


    /**
     * 黄金ETF
     */
    public final static String CATEGORY_GOLD = "gold";

    /**
     * 人工智能ETF
     */
    public final static String CATEGORY_AI = "ai";

    /**
     * 能源ETF
     */
    public final static String CATEGORY_ENERGY = "energy";
    ///

    /**
     * C2C订单模块
     */
    public static final String C2C_ORDER = "OP_ADMIN_C2C_ORDER_TIP";
    /**
     * C2C申诉模块
     */
    public static final String C2C_APPEAL = "OP_ADMIN_C2C_APPEAL_TIP";

    /**
     * 交割单
     */
    public static final String FUTURES_ORDER = "OP_ADMIN_FUTURES_ORDER_TIP";


    /**
     * 区块链充值模块
     */
    public static final String RECHARGE_BLOCKCHAIN = "OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP";

    /**
     * 三方充值模块
     */
    public static final String RECHARGE = "OP_ADMIN_RECHARGE_TIP";

    /**
     * 提现模块
     */
    public static final String WITHDRAW = "OP_ADMIN_WITHDRAW_TIP";

    /**
     * 认证模块
     */
    public static final String KYC = "OP_ADMIN_KYC_TIP";

    /**
     * 高级认证模块
     */
    public static final String KYC_HIGH_LEVEL = "OP_ADMIN_KYC_HIGH_LEVEL_TIP";

    /**
     * OTC订单模块
     */
    public static final String OTCORDER = "OP_ADMIN_OTC_ORDER_TIP";

    /**
     * OTC订单聊天模块
     */
    public static final String OTCORDER_ONLINECHAT = "OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP";

    /**
     * 银行卡订单模块
     */
    public static final String BANK_CARD_ORDER = "OP_ADMIN_BANK_CARD_ORDER_TIP";
   /**
     * 银行卡订单消息模块
     */
    public static final String BANK_CARD_ORDER_ONLINECHAT = "OP_ADMIN_BANK_CARD_ORDER_ONLINECHAT_TIP";
    

    /**
     * 用户资金密码申请模块
     */
    public static final String USER_SAFEWORD_APPLY = "OP_ADMIN_USER_SAFEWORD_APPLY_TIP";

    /**
     * 永续合约持仓单
     */
    public static final String CONTRACT_ORDER = "OP_ADMIN_CONTRACT_ORDER_TIP";

    /**
     * 用户客服模块
     */
    public static final String ONLINECHAT = "OP_ADMIN_ONLINECHAT";

    /**OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP
     * 活动申请模块
     */
    public static final String ACTIVITY_USER_APPLY = "OP_ADMIN_ACTIVITY_USER_APPLY_TIP";

    /**
     * 数字货币-永续合约持仓单
     */
    public static final String CRYPTOS_CONTRACT_ORDER = "OP_CRYPTOS_CONTRACT_ORDER_TIP";

    /**
     * 数字货币-永续合约持仓单
     */
    public static final String CRYPTOS_FUTURES_ORDER = "OP_CRYPTOS_FUTURES_ORDER_TIP";

    /**
     * 数字货币-永续委托单
     */
    public static final String CRYPTOS_APPLY_ORDER = "OP_CRYPTOS_APPLY_ORDER_TIP";

    /**
     * ETF-永续合约持仓单
     */
    public static final String ETF_CONTRACT_ORDER = "OP_ETF_CONTRACT_ORDER_TIP";

    /**
     * ETF-永续合约持仓单
     */
    public static final String ETF_FUTURES_ORDER = "OP_ETF_FUTURES_ORDER_TIP";

    /**
     * ETF-永续委托单
     */
    public static final String ETF_APPLY_ORDER = "OP_ETF_APPLY_ORDER_TIP";

    /**
     * 外汇-永续合约持仓单
     */
    public static final String FOREIGN_CONTRACT_ORDER = "OP_FOREIGN_CONTRACT_ORDER_TIP";

    /**
     * 外汇-永续合约持仓单
     */
    public static final String FOREIGN_FUTURES_ORDER = "OP_FOREIGN_FUTURES_ORDER_TIP";

    /**
     * 外汇-永续委托单
     */
    public static final String FOREIGN_APPLY_ORDER = "OP_FOREIGN_APPLY_ORDER_TIP";


    //commodities

    /**
     * 大宗商品-永续合约持仓单
     */
    public static final String COMMODITIES_CONTRACT_ORDER = "OP_COMMODITIES_CONTRACT_ORDER_TIP";

    /**
     * 大宗商品-永续合约持仓单
     */
    public static final String COMMODITIES_FUTURES_ORDER = "OP_COMMODITIES_FUTURES_ORDER_TIP";

    /**
     * 大宗商品-永续委托单
     */
    public static final String COMMODITIES_APPLY_ORDER = "OP_COMMODITIES_APPLY_ORDER_TIP";

    //美股
    /**
     * 美股-永续合约持仓单
     */
    public static final String US_STOCKS_CONTRACT_ORDER = "OP_US_STOCKS_CONTRACT_ORDER_TIP";

    /**
     * 美股-永续合约持仓单
     */
    public static final String US_STOCKS_FUTURES_ORDER = "OP_US_STOCKS_FUTURES_ORDER_TIP";

    /**
     * 美股-永续委托单
     */
    public static final String US_STOCKS_APPLY_ORDER = "OP_US_STOCKS_APPLY_ORDER_TIP";

    //台股
    /**
     * 台股-永续合约持仓单
     */
    public static final String TW_STOCKS_CONTRACT_ORDER = "OP_TW_STOCKS_CONTRACT_ORDER_TIP";

    /**
     * 台股-永续合约持仓单
     */
    public static final String TW_STOCKS_FUTURES_ORDER = "OP_TW_STOCKS_FUTURES_ORDER_TIP";

    /**
     * 台股-永续委托单
     */
    public static final String TW_STOCKS_APPLY_ORDER = "OP_TW_STOCKS_APPLY_ORDER_TIP";

    //港股
    /**
     * 港股-永续合约持仓单
     */
    public static final String HK_STOCKS_CONTRACT_ORDER = "OP_HK_STOCKS_CONTRACT_ORDER_TIP";

    /**
     * 港股-永续合约持仓单
     */
    public static final String HK_STOCKS_FUTURES_ORDER = "OP_HK_STOCKS_FUTURES_ORDER_TIP";

    /**
     * 港股-永续委托单
     */
    public static final String HK_STOCKS_APPLY_ORDER = "OP_HK_STOCKS_APPLY_ORDER_TIP";

    //A股
    /**
     * A股-永续合约持仓单
     */
    public static final String A_STOCKS_CONTRACT_ORDER = "OP_A_STOCKS_CONTRACT_ORDER_TIP";

    /**
     * A股-永续合约持仓单
     */
    public static final String A_STOCKS_FUTURES_ORDER = "OP_A_STOCKS_FUTURES_ORDER_TIP";

    /**
     * A股-永续委托单
     */
    public static final String A_STOCKS_APPLY_ORDER = "OP_A_STOCKS_APPLY_ORDER_TIP";

    //日股
    /**
     * 日股-永续合约持仓单
     */
    public static final String JP_STOCKS_CONTRACT_ORDER = "OP_JP_STOCKS_CONTRACT_ORDER_TIP";

    /**
     * 日股-永续合约持仓单
     */
    public static final String JP_STOCKS_FUTURES_ORDER = "OP_JP_STOCKS_FUTURES_ORDER_TIP";

    /**
     * 日股-永续委托单
     */
    public static final String JP_STOCKS_APPLY_ORDER = "OP_JP_STOCKS_APPLY_ORDER_TIP";

    //印度股
    /**
     * 永续合约持仓单
     */
    public static final String INDIA_STOCKS_CONTRACT_ORDER = "OP_INDIA_STOCKS_CONTRACT_ORDER_TIP";

    /**
     * 永续合约持仓单
     */
    public static final String INDIA_STOCKS_FUTURES_ORDER = "OP_INDIA_STOCKS_FUTURES_ORDER_TIP";

    /**
     * 永续委托单
     */
    public static final String INDIA_STOCKS_APPLY_ORDER = "OP_INDIA_STOCKS_APPLY_ORDER_TIP";
    //英股
    /**
     * 永续合约持仓单
     */
    public static final String UK_STOCKS_CONTRACT_ORDER = "OP_UK_STOCKS_CONTRACT_ORDER_TIP";

    /**
     * 永续合约持仓单
     */
    public static final String UK_STOCKS_FUTURES_ORDER = "OP_UK_STOCKS_FUTURES_ORDER_TIP";

    /**
     * 永续委托单
     */
    public static final String UK_STOCKS_APPLY_ORDER = "OP_UK_STOCKS_APPLY_ORDER_TIP";


    //新股票(合并)提示
    //美股
    /**
     * 美股-永续合约持仓单
     */
    public static final String US_STOCKS_CONTRACT_ORDER_NEW = "OP_US_STOCKS_CONTRACT_ORDER_NEW_TIP";

    /**
     * 美股-永续合约持仓单
     */
    public static final String US_STOCKS_FUTURES_ORDER_NEW = "OP_US_STOCKS_FUTURES_ORDER_NEW_TIP";

    /**
     * 美股-永续委托单
     */
    public static final String US_STOCKS_APPLY_ORDER_NEW = "OP_US_STOCKS_APPLY_ORDER_NEW_TIP";

    //台股
    /**
     * 台股-永续合约持仓单
     */
    public static final String TW_STOCKS_CONTRACT_ORDER_NEW = "OP_TW_STOCKS_CONTRACT_ORDER_NEW_TIP";

    /**
     * 台股-永续合约持仓单
     */
    public static final String TW_STOCKS_FUTURES_ORDER_NEW = "OP_TW_STOCKS_FUTURES_ORDER_NEW_TIP";

    /**
     * 台股-永续委托单
     */
    public static final String TW_STOCKS_APPLY_ORDER_NEW = "OP_TW_STOCKS_APPLY_ORDER_NEW_TIP";

    //港股
    /**
     * 港股-永续合约持仓单
     */
    public static final String HK_STOCKS_CONTRACT_ORDER_NEW = "OP_HK_STOCKS_CONTRACT_ORDER_NEW_TIP";

    /**
     * 港股-永续合约持仓单
     */
    public static final String HK_STOCKS_FUTURES_ORDER_NEW = "OP_HK_STOCKS_FUTURES_ORDER_NEW_TIP";

    /**
     * 港股-永续委托单
     */
    public static final String HK_STOCKS_APPLY_ORDER_NEW = "OP_HK_STOCKS_APPLY_ORDER_NEW_TIP";

    //A股
    /**
     * A股-永续合约持仓单
     */
    public static final String A_STOCKS_CONTRACT_ORDER_NEW = "OP_A_STOCKS_CONTRACT_ORDER_NEW_TIP";

    /**
     * A股-永续合约持仓单
     */
    public static final String A_STOCKS_FUTURES_ORDER_NEW = "OP_A_STOCKS_FUTURES_ORDER_NEW_TIP";

    /**
     * A股-永续委托单
     */
    public static final String A_STOCKS_APPLY_ORDER_NEW = "OP_A_STOCKS_APPLY_ORDER_NEW_TIP";

    //日股
    /**
     * 日股-永续合约持仓单
     */
    public static final String JP_STOCKS_CONTRACT_ORDER_NEW = "OP_JP_STOCKS_CONTRACT_ORDER_NEW_TIP";

    /**
     * 日股-永续合约持仓单
     */
    public static final String JP_STOCKS_FUTURES_ORDER_NEW = "OP_JP_STOCKS_FUTURES_ORDER_NEW_TIP";

    /**
     * 日股-永续委托单
     */
    public static final String JP_STOCKS_APPLY_ORDER_NEW = "OP_JP_STOCKS_APPLY_ORDER_NEW_TIP";

    //印度股
    /**
     * 永续合约持仓单
     */
    public static final String INDIA_STOCKS_CONTRACT_ORDER_NEW = "OP_INDIA_STOCKS_CONTRACT_ORDER_NEW_TIP";

    /**
     * 永续合约持仓单
     */
    public static final String INDIA_STOCKS_FUTURES_ORDER_NEW = "OP_INDIA_STOCKS_FUTURES_ORDER_NEW_TIP";

    /**
     * 永续委托单
     */
    public static final String INDIA_STOCKS_APPLY_ORDER_NEW = "OP_INDIA_STOCKS_APPLY_ORDER_NEW_TIP";
    //英股
    /**
     * 永续合约持仓单
     */
    public static final String UK_STOCKS_CONTRACT_ORDER_NEW = "OP_UK_STOCKS_CONTRACT_ORDER_NEW_TIP";

    /**
     * 永续合约持仓单
     */
    public static final String UK_STOCKS_FUTURES_ORDER_NEW = "OP_UK_STOCKS_FUTURES_ORDER_NEW_TIP";

    /**
     * 永续委托单
     */
    public static final String UK_STOCKS_APPLY_ORDER_NEW = "OP_UK_STOCKS_APPLY_ORDER_NEW_TIP";

    public static final String ADMIN_URL = ApplicationUtil.getProperty("admin_url");

    public static Map<String, String> ACTION_CONTRACT_ITEM_MAP = new HashMap<String, String>();
    static {
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.cryptos,  TipConstants.CRYPTOS_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.indices,  TipConstants.ETF_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.forex,  TipConstants.FOREIGN_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.commodities,  TipConstants.COMMODITIES_CONTRACT_ORDER);

        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.US_STOCKS,  TipConstants.US_STOCKS_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.HK_STOCKS,  TipConstants.HK_STOCKS_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.TW_STOCKS,  TipConstants.TW_STOCKS_CONTRACT_ORDER);

        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.A_STOCKS,  TipConstants.A_STOCKS_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.JP_STOCKS, TipConstants.JP_STOCKS_CONTRACT_ORDER);

        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.INDIA_STOCKS, TipConstants.INDIA_STOCKS_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP.put(TipConstants.UK_STOCKS, TipConstants.UK_STOCKS_CONTRACT_ORDER);
    }

    public static Map<String, String> ACTION_FUTURES_ITEM_MAP = new HashMap<String, String>();
    static {
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.cryptos,  TipConstants.CRYPTOS_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.indices,  TipConstants.ETF_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.forex,  TipConstants.FOREIGN_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.commodities,  TipConstants.COMMODITIES_FUTURES_ORDER);

        ACTION_FUTURES_ITEM_MAP.put(TipConstants.US_STOCKS,  TipConstants.US_STOCKS_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.HK_STOCKS,  TipConstants.HK_STOCKS_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.TW_STOCKS,  TipConstants.TW_STOCKS_FUTURES_ORDER);

        ACTION_FUTURES_ITEM_MAP.put(TipConstants.A_STOCKS,  TipConstants.A_STOCKS_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.JP_STOCKS, TipConstants.JP_STOCKS_FUTURES_ORDER);

        ACTION_FUTURES_ITEM_MAP.put(TipConstants.INDIA_STOCKS,  TipConstants.INDIA_STOCKS_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP.put(TipConstants.UK_STOCKS, TipConstants.UK_STOCKS_FUTURES_ORDER);
    }

    public static Map<String, String> ACTION_APPLY_ITEM_MAP = new HashMap<String, String>();
    static {
        ACTION_APPLY_ITEM_MAP.put(TipConstants.cryptos,  TipConstants.CRYPTOS_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP.put(TipConstants.indices,  TipConstants.ETF_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP.put(TipConstants.forex,  TipConstants.FOREIGN_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP.put(TipConstants.commodities,  TipConstants.COMMODITIES_APPLY_ORDER);

        ACTION_APPLY_ITEM_MAP.put(TipConstants.US_STOCKS,  TipConstants.US_STOCKS_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP.put(TipConstants.HK_STOCKS,  TipConstants.HK_STOCKS_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP.put(TipConstants.TW_STOCKS,  TipConstants.TW_STOCKS_APPLY_ORDER);

        ACTION_APPLY_ITEM_MAP.put(TipConstants.A_STOCKS,  TipConstants.A_STOCKS_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP.put(TipConstants.JP_STOCKS, TipConstants.JP_STOCKS_APPLY_ORDER);

        ACTION_APPLY_ITEM_MAP.put(TipConstants.INDIA_STOCKS,  TipConstants.INDIA_STOCKS_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP.put(TipConstants.UK_STOCKS, TipConstants.UK_STOCKS_APPLY_ORDER);
    }

    //股票(合并)
    public static Map<String, String> ACTION_CONTRACT_ITEM_MAP_NEW = new HashMap<String, String>();
    static {
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.cryptos,  TipConstants.CRYPTOS_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.indices,  TipConstants.ETF_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.forex,  TipConstants.FOREIGN_CONTRACT_ORDER);
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.commodities,  TipConstants.COMMODITIES_CONTRACT_ORDER);

        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.US_STOCKS,  TipConstants.US_STOCKS_CONTRACT_ORDER_NEW);
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.HK_STOCKS,  TipConstants.HK_STOCKS_CONTRACT_ORDER_NEW);
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.TW_STOCKS,  TipConstants.TW_STOCKS_CONTRACT_ORDER_NEW);

        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.A_STOCKS,  TipConstants.A_STOCKS_CONTRACT_ORDER_NEW);
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.JP_STOCKS, TipConstants.JP_STOCKS_CONTRACT_ORDER_NEW);

        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.INDIA_STOCKS, TipConstants.INDIA_STOCKS_CONTRACT_ORDER_NEW);
        ACTION_CONTRACT_ITEM_MAP_NEW.put(TipConstants.UK_STOCKS, TipConstants.UK_STOCKS_CONTRACT_ORDER_NEW);
    }

    public static Map<String, String> ACTION_FUTURES_ITEM_MAP_NEW = new HashMap<String, String>();
    static {
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.cryptos,  TipConstants.CRYPTOS_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.indices,  TipConstants.ETF_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.forex,  TipConstants.FOREIGN_FUTURES_ORDER);
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.commodities,  TipConstants.COMMODITIES_FUTURES_ORDER);

        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.US_STOCKS,  TipConstants.US_STOCKS_FUTURES_ORDER_NEW);
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.HK_STOCKS,  TipConstants.HK_STOCKS_FUTURES_ORDER_NEW);
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.TW_STOCKS,  TipConstants.TW_STOCKS_FUTURES_ORDER_NEW);

        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.A_STOCKS,  TipConstants.A_STOCKS_FUTURES_ORDER_NEW);
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.JP_STOCKS, TipConstants.JP_STOCKS_FUTURES_ORDER_NEW);

        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.INDIA_STOCKS,  TipConstants.INDIA_STOCKS_FUTURES_ORDER_NEW);
        ACTION_FUTURES_ITEM_MAP_NEW.put(TipConstants.UK_STOCKS, TipConstants.UK_STOCKS_FUTURES_ORDER_NEW);
    }

    public static Map<String, String> ACTION_APPLY_ITEM_MAP_NEW = new HashMap<String, String>();
    static {
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.cryptos,  TipConstants.CRYPTOS_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.indices,  TipConstants.ETF_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.forex,  TipConstants.FOREIGN_APPLY_ORDER);
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.commodities,  TipConstants.COMMODITIES_APPLY_ORDER);

        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.US_STOCKS,  TipConstants.US_STOCKS_APPLY_ORDER_NEW);
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.HK_STOCKS,  TipConstants.HK_STOCKS_APPLY_ORDER_NEW);
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.TW_STOCKS,  TipConstants.TW_STOCKS_APPLY_ORDER_NEW);

        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.A_STOCKS,  TipConstants.A_STOCKS_APPLY_ORDER_NEW);
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.JP_STOCKS, TipConstants.JP_STOCKS_APPLY_ORDER_NEW);

        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.INDIA_STOCKS,  TipConstants.INDIA_STOCKS_APPLY_ORDER_NEW);
        ACTION_APPLY_ITEM_MAP_NEW.put(TipConstants.UK_STOCKS, TipConstants.UK_STOCKS_APPLY_ORDER_NEW);
    }


    /**
     * 请求action数据
     */
    public static Map<String, String> ACTION_MAP = new HashMap<String, String>();

    static {
        ACTION_MAP.put(RECHARGE_BLOCKCHAIN,  "/order-rechange");
        ACTION_MAP.put(RECHARGE,  "/order-rechange");
        ACTION_MAP.put(WITHDRAW,  "/order-withdraw");
        ACTION_MAP.put(KYC,  "/user-relation-basics");
        ACTION_MAP.put(KYC_HIGH_LEVEL, "/user-relation-senior");
        ACTION_MAP.put(OTCORDER, "/normal/adminOtcOrderAction!list.action");
        ACTION_MAP.put(BANK_CARD_ORDER,  "/c2c-bank_pay_order");
        ACTION_MAP.put(BANK_CARD_ORDER_ONLINECHAT,  "/c2c-bank_pay_order");
        
        ACTION_MAP.put(USER_SAFEWORD_APPLY,  "/user-relation-reset");
        ACTION_MAP.put(CONTRACT_ORDER,  "/normal/adminContractOrderAction!list.action");
        ACTION_MAP.put(OTCORDER_ONLINECHAT,  "/c2c-c2c_pay_order");

        ACTION_MAP.put(USER_SAFEWORD_APPLY+"-0",  "/user-relation-reset");
        ACTION_MAP.put(USER_SAFEWORD_APPLY+"-1",  "/user-relation-reset");
        ACTION_MAP.put(USER_SAFEWORD_APPLY+"-2",  "/user-relation-reset");
        ACTION_MAP.put(USER_SAFEWORD_APPLY+"-3",  "/user-relation-reset");
        ACTION_MAP.put(ONLINECHAT, "/message");

        ACTION_MAP.put(C2C_ORDER,  "/c2c-c2c_pay_order");
        ACTION_MAP.put(C2C_APPEAL,  "/c2c-c2c-pay-appeal");

        ACTION_MAP.put(CRYPTOS_CONTRACT_ORDER,  "/cryptos-spots-cryptos-spots-transport");
        ACTION_MAP.put(CRYPTOS_FUTURES_ORDER,  "/cryptos-spots-cryptos-pickAddr");
        ACTION_MAP.put(CRYPTOS_APPLY_ORDER,  "/cryptos-spots-cryptos-hotSearch");

        ACTION_MAP.put(ETF_CONTRACT_ORDER,  "/etf-spots-etf-transport");
        ACTION_MAP.put(ETF_FUTURES_ORDER,  "/etf-spots-etf-pickAddr");
        ACTION_MAP.put(ETF_APPLY_ORDER,  "/etf-spots-etf-hotSearch");

        ACTION_MAP.put(FOREIGN_CONTRACT_ORDER,  "/shop-transport");
        ACTION_MAP.put(FOREIGN_FUTURES_ORDER,  "/shop-pickAddr");
        ACTION_MAP.put(FOREIGN_APPLY_ORDER,  "/shop-hotSearch");

        ACTION_MAP.put(COMMODITIES_CONTRACT_ORDER,  "/commodity-cryptos-spots-transport");
        ACTION_MAP.put(COMMODITIES_FUTURES_ORDER,  "/commodity-cryptos-pickAddr");
        ACTION_MAP.put(COMMODITIES_APPLY_ORDER,  "/commodity-cryptos-hotSearch");

        ACTION_MAP.put(US_STOCKS_CONTRACT_ORDER,  "/us-spots-us-transport");
        ACTION_MAP.put(US_STOCKS_FUTURES_ORDER,  "/us-spots-us-pickAddr");
        ACTION_MAP.put(US_STOCKS_APPLY_ORDER,  "/us-spots-us-hotSearch");

        ACTION_MAP.put(TW_STOCKS_CONTRACT_ORDER,  "/tw-stocks-transport");
        ACTION_MAP.put(TW_STOCKS_FUTURES_ORDER,  "/tw-stocks-pickAddr");
        ACTION_MAP.put(TW_STOCKS_APPLY_ORDER,  "/tw-stocks-hotSearch");

        ACTION_MAP.put(HK_STOCKS_CONTRACT_ORDER,  "/hk-stocks-transport");
        ACTION_MAP.put(HK_STOCKS_FUTURES_ORDER,  "/hk-stocks-pickAddr");
        ACTION_MAP.put(HK_STOCKS_APPLY_ORDER,  "/hk-stocks-hotSearch");

        ACTION_MAP.put(A_STOCKS_CONTRACT_ORDER,  "/A-stocks-transport");
        ACTION_MAP.put(A_STOCKS_FUTURES_ORDER,  "/A-stocks-pickAddr");
        ACTION_MAP.put(A_STOCKS_APPLY_ORDER,  "/A-stocks-hotSearch");

        ACTION_MAP.put(JP_STOCKS_CONTRACT_ORDER,  "/JP-stocks-transport");
        ACTION_MAP.put(JP_STOCKS_FUTURES_ORDER,  "/JP-stocks-pickAddr");
        ACTION_MAP.put(JP_STOCKS_APPLY_ORDER,  "/JP-stocks-hotSearch");

        ACTION_MAP.put(INDIA_STOCKS_CONTRACT_ORDER,  "/INDIA-stocks-transport");
        ACTION_MAP.put(INDIA_STOCKS_FUTURES_ORDER,  "/INDIA-stocks-pickAddr");
        ACTION_MAP.put(INDIA_STOCKS_APPLY_ORDER,  "/INDIA-stocks-hotSearch");

        ACTION_MAP.put(UK_STOCKS_CONTRACT_ORDER,  "/UK-stocks-transport");
        ACTION_MAP.put(UK_STOCKS_FUTURES_ORDER,  "/UK-stocks-pickAddr");
        ACTION_MAP.put(UK_STOCKS_APPLY_ORDER,  "/UK-stocks-hotSearch");
        //股票(合并)
        ACTION_MAP.put(US_STOCKS_CONTRACT_ORDER_NEW,  "/union-stocks-transport@US-stocks");
        ACTION_MAP.put(US_STOCKS_FUTURES_ORDER_NEW,  "/union-stocks-pickAddr@US-stocks");
        ACTION_MAP.put(US_STOCKS_APPLY_ORDER_NEW,  "/union-stocks-hotSearch@US-stocks");

        ACTION_MAP.put(TW_STOCKS_CONTRACT_ORDER_NEW,  "/union-stocks-transport@WT-stocks");
        ACTION_MAP.put(TW_STOCKS_FUTURES_ORDER_NEW,  "/union-stocks-pickAddr@WT-stocks");
        ACTION_MAP.put(TW_STOCKS_APPLY_ORDER_NEW,  "/union-stocks-hotSearch@WT-stocks");

        ACTION_MAP.put(HK_STOCKS_CONTRACT_ORDER_NEW,  "/union-stocks-transport@HK-stocks");
        ACTION_MAP.put(HK_STOCKS_FUTURES_ORDER_NEW,  "/union-stocks-pickAddr@HK-stocks");
        ACTION_MAP.put(HK_STOCKS_APPLY_ORDER_NEW,  "/union-stocks-hotSearch@HK-stocks");

        ACTION_MAP.put(A_STOCKS_CONTRACT_ORDER_NEW,  "/union-stocks-transport@A-stocks");
        ACTION_MAP.put(A_STOCKS_FUTURES_ORDER_NEW,  "/union-stocks-pickAddr@A-stocks");
        ACTION_MAP.put(A_STOCKS_APPLY_ORDER_NEW,  "/union-stocks-hotSearch@A-stocks");

        ACTION_MAP.put(JP_STOCKS_CONTRACT_ORDER_NEW,  "/union-stocks-transport@JP-stocks");
        ACTION_MAP.put(JP_STOCKS_FUTURES_ORDER_NEW,  "/union-stocks-pickAddr@JP-stocks");
        ACTION_MAP.put(JP_STOCKS_APPLY_ORDER_NEW,  "/union-stocks-hotSearch@JP-stocks");

        ACTION_MAP.put(INDIA_STOCKS_CONTRACT_ORDER_NEW,  "/union-stocks-transport@INDIA-stocks");
        ACTION_MAP.put(INDIA_STOCKS_FUTURES_ORDER_NEW,  "/union-stocks-pickAddr@INDIA-stocks");
        ACTION_MAP.put(INDIA_STOCKS_APPLY_ORDER_NEW,  "/union-stocks-hotSearch@INDIA-stocks");

        ACTION_MAP.put(UK_STOCKS_CONTRACT_ORDER_NEW,  "/union-stocks-transport@UK-stocks");
        ACTION_MAP.put(UK_STOCKS_FUTURES_ORDER_NEW,  "/union-stocks-pickAddr@UK-stocks");
        ACTION_MAP.put(UK_STOCKS_APPLY_ORDER_NEW,  "/union-stocks-hotSearch@UK-stocks");


    };

    /**
     * 消息格式数据
     */
    public static Map<String, String> MESSAGE_MAP = new HashMap<String, String>();

    static {
        MESSAGE_MAP.put(RECHARGE_BLOCKCHAIN, "您有{0}条新的区块链充值订单");
        MESSAGE_MAP.put(RECHARGE, "您有{0}条新的三方充值订单");
        MESSAGE_MAP.put(WITHDRAW, "您有{0}条新的提现订单");
        MESSAGE_MAP.put(KYC, "您有{0}条新的基础认证");
        MESSAGE_MAP.put(KYC_HIGH_LEVEL, "您有{0}条新的高级认证");
        MESSAGE_MAP.put(BANK_CARD_ORDER, "您有{0}条新的银行卡订单");
        MESSAGE_MAP.put(BANK_CARD_ORDER_ONLINECHAT, "您有{0}条新的银行卡订单消息");
        MESSAGE_MAP.put(USER_SAFEWORD_APPLY, "您有{0}条新的用户资金密码修改申请");
        MESSAGE_MAP.put(USER_SAFEWORD_APPLY+"-0", "您有{0}条新的用户资金密码修改申请");
        MESSAGE_MAP.put(USER_SAFEWORD_APPLY+"-1", "您有{0}条新的用户取消谷歌绑定申请");
        MESSAGE_MAP.put(USER_SAFEWORD_APPLY+"-2", "您有{0}条新的用户取消手机绑定申请");
        MESSAGE_MAP.put(USER_SAFEWORD_APPLY+"-3", "您有{0}条新的用户取消邮箱绑定申请");
        MESSAGE_MAP.put(ONLINECHAT, "您有{0}条新的聊天消息");
        MESSAGE_MAP.put(OTCORDER_ONLINECHAT, "您有{0}条新的聊天消息");
        MESSAGE_MAP.put(C2C_ORDER, "您有{0}条新的C2C订单");
		MESSAGE_MAP.put(C2C_APPEAL, "您有{0}条新的C2C申诉");

        MESSAGE_MAP.put(CRYPTOS_CONTRACT_ORDER, "您有{0}条新的数字货币永续合约持仓单");
        MESSAGE_MAP.put(CRYPTOS_FUTURES_ORDER, "您有{0}条新的数字货币交割合约");
        MESSAGE_MAP.put(CRYPTOS_APPLY_ORDER, "您有{0}条新的数字货币委托单");

        MESSAGE_MAP.put(ETF_CONTRACT_ORDER, "您有{0}条新的ETF永续合约持仓单");
        MESSAGE_MAP.put(ETF_FUTURES_ORDER, "您有{0}条新的ETF交割合约");
        MESSAGE_MAP.put(ETF_APPLY_ORDER, "您有{0}条新的ETF委托单");

        MESSAGE_MAP.put(FOREIGN_CONTRACT_ORDER, "您有{0}条新的外汇永续合约持仓单");
        MESSAGE_MAP.put(FOREIGN_FUTURES_ORDER, "您有{0}条新的外汇交割合约");
        MESSAGE_MAP.put(FOREIGN_APPLY_ORDER, "您有{0}条新的外汇委托单");

        MESSAGE_MAP.put(COMMODITIES_CONTRACT_ORDER, "您有{0}条新的现货永续合约持仓单");
        MESSAGE_MAP.put(COMMODITIES_FUTURES_ORDER, "您有{0}条新的现货交割合约");
        MESSAGE_MAP.put(COMMODITIES_APPLY_ORDER, "您有{0}条新的现货委托单");

        MESSAGE_MAP.put(US_STOCKS_CONTRACT_ORDER, "您有{0}条新的美股永续合约持仓单");
        MESSAGE_MAP.put(US_STOCKS_FUTURES_ORDER, "您有{0}条新的美股交割合约");
        MESSAGE_MAP.put(US_STOCKS_APPLY_ORDER, "您有{0}条新的美股委托单");

        MESSAGE_MAP.put(TW_STOCKS_CONTRACT_ORDER, "您有{0}条新的台股永续合约持仓单");
        MESSAGE_MAP.put(TW_STOCKS_FUTURES_ORDER, "您有{0}条新的台股交割合约");
        MESSAGE_MAP.put(TW_STOCKS_APPLY_ORDER, "您有{0}条新的台股委托单");

        MESSAGE_MAP.put(HK_STOCKS_CONTRACT_ORDER, "您有{0}条新的港股永续合约持仓单");
        MESSAGE_MAP.put(HK_STOCKS_FUTURES_ORDER, "您有{0}条新的港股交割合约");
        MESSAGE_MAP.put(HK_STOCKS_APPLY_ORDER, "您有{0}条新的港股委托单");

        MESSAGE_MAP.put(A_STOCKS_CONTRACT_ORDER, "您有{0}条新的A股永续合约持仓单");
        MESSAGE_MAP.put(A_STOCKS_FUTURES_ORDER, "您有{0}条新的A股交割合约");
        MESSAGE_MAP.put(A_STOCKS_APPLY_ORDER, "您有{0}条新的A股委托单");

        MESSAGE_MAP.put(JP_STOCKS_CONTRACT_ORDER, "您有{0}条新的日股永续合约持仓单");
        MESSAGE_MAP.put(JP_STOCKS_FUTURES_ORDER, "您有{0}条新的日股交割合约");
        MESSAGE_MAP.put(JP_STOCKS_APPLY_ORDER, "您有{0}条新的日股委托单");

        MESSAGE_MAP.put(INDIA_STOCKS_CONTRACT_ORDER, "您有{0}条新的印股永续合约持仓单");
        MESSAGE_MAP.put(INDIA_STOCKS_FUTURES_ORDER, "您有{0}条新的印股交割合约");
        MESSAGE_MAP.put(INDIA_STOCKS_APPLY_ORDER, "您有{0}条新的印股委托单");

        MESSAGE_MAP.put(UK_STOCKS_CONTRACT_ORDER, "您有{0}条新的英股永续合约持仓单");
        MESSAGE_MAP.put(UK_STOCKS_FUTURES_ORDER, "您有{0}条新的英股交割合约");
        MESSAGE_MAP.put(UK_STOCKS_APPLY_ORDER, "您有{0}条新的英股委托单");

        //股票(合并)
        MESSAGE_MAP.put(US_STOCKS_CONTRACT_ORDER_NEW, "您有{0}条新的美股永续合约持仓单");
        MESSAGE_MAP.put(US_STOCKS_FUTURES_ORDER_NEW, "您有{0}条新的美股交割合约");
        MESSAGE_MAP.put(US_STOCKS_APPLY_ORDER_NEW, "您有{0}条新的美股委托单");

        MESSAGE_MAP.put(TW_STOCKS_CONTRACT_ORDER_NEW, "您有{0}条新的台股永续合约持仓单");
        MESSAGE_MAP.put(TW_STOCKS_FUTURES_ORDER_NEW, "您有{0}条新的台股交割合约");
        MESSAGE_MAP.put(TW_STOCKS_APPLY_ORDER_NEW, "您有{0}条新的台股委托单");

        MESSAGE_MAP.put(HK_STOCKS_CONTRACT_ORDER_NEW, "您有{0}条新的港股永续合约持仓单");
        MESSAGE_MAP.put(HK_STOCKS_FUTURES_ORDER_NEW, "您有{0}条新的港股交割合约");
        MESSAGE_MAP.put(HK_STOCKS_APPLY_ORDER_NEW, "您有{0}条新的港股委托单");

        MESSAGE_MAP.put(A_STOCKS_CONTRACT_ORDER_NEW, "您有{0}条新的A股永续合约持仓单");
        MESSAGE_MAP.put(A_STOCKS_FUTURES_ORDER_NEW, "您有{0}条新的A股交割合约");
        MESSAGE_MAP.put(A_STOCKS_APPLY_ORDER_NEW, "您有{0}条新的A股委托单");

        MESSAGE_MAP.put(JP_STOCKS_CONTRACT_ORDER_NEW, "您有{0}条新的日股永续合约持仓单");
        MESSAGE_MAP.put(JP_STOCKS_FUTURES_ORDER_NEW, "您有{0}条新的日股交割合约");
        MESSAGE_MAP.put(JP_STOCKS_APPLY_ORDER_NEW, "您有{0}条新的日股委托单");

        MESSAGE_MAP.put(INDIA_STOCKS_CONTRACT_ORDER_NEW, "您有{0}条新的印股永续合约持仓单");
        MESSAGE_MAP.put(INDIA_STOCKS_FUTURES_ORDER_NEW, "您有{0}条新的印股交割合约");
        MESSAGE_MAP.put(INDIA_STOCKS_APPLY_ORDER_NEW, "您有{0}条新的印股委托单");

        MESSAGE_MAP.put(UK_STOCKS_CONTRACT_ORDER, "您有{0}条新的英股永续合约持仓单");
        MESSAGE_MAP.put(UK_STOCKS_FUTURES_ORDER, "您有{0}条新的英股交割合约");
        MESSAGE_MAP.put(UK_STOCKS_APPLY_ORDER, "您有{0}条新的英股委托单");
    };

    /**
     * 消息格式数据
     */
    public static Map<String, String> MESSAGE_TYPE = new HashMap<String, String>();

    static {
        MESSAGE_TYPE.put(RECHARGE_BLOCKCHAIN, "1");
        MESSAGE_TYPE.put(RECHARGE, "1");
        MESSAGE_TYPE.put(WITHDRAW, "2");
        MESSAGE_TYPE.put(KYC, "3");
        MESSAGE_TYPE.put(KYC_HIGH_LEVEL, "3");
        MESSAGE_TYPE.put(USER_SAFEWORD_APPLY, "4");
        MESSAGE_TYPE.put(BANK_CARD_ORDER, "4");
        MESSAGE_TYPE.put(BANK_CARD_ORDER_ONLINECHAT, "5");
//        MESSAGE_MAP.put(BANK_CARD_ORDER, "您有{0}条新的银行卡订单");
//        MESSAGE_MAP.put(USER_SAFEWORD_APPLY, "您有{0}条新的用户资金密码修改申请");
        MESSAGE_MAP.put(ONLINECHAT, "您有{0}条新的聊天消息");
        MESSAGE_TYPE.put(OTCORDER_ONLINECHAT, "您有{0}条新的聊天消息");


        MESSAGE_TYPE.put(CRYPTOS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(CRYPTOS_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(CRYPTOS_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(ETF_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(ETF_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(ETF_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(FOREIGN_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(FOREIGN_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(FOREIGN_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(COMMODITIES_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(COMMODITIES_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(COMMODITIES_APPLY_ORDER, "99");
        //
        MESSAGE_TYPE.put(US_STOCKS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(US_STOCKS_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(US_STOCKS_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(TW_STOCKS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(TW_STOCKS_FUTURES_ORDER, "99");
         MESSAGE_TYPE.put(TW_STOCKS_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(HK_STOCKS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(HK_STOCKS_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(HK_STOCKS_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(A_STOCKS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(A_STOCKS_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(A_STOCKS_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(JP_STOCKS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(JP_STOCKS_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(JP_STOCKS_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(INDIA_STOCKS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(INDIA_STOCKS_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(INDIA_STOCKS_APPLY_ORDER, "99");

        MESSAGE_TYPE.put(UK_STOCKS_CONTRACT_ORDER, "99");
        MESSAGE_TYPE.put(UK_STOCKS_FUTURES_ORDER, "99");
        MESSAGE_TYPE.put(UK_STOCKS_APPLY_ORDER, "99");

        //
        MESSAGE_TYPE.put(US_STOCKS_CONTRACT_ORDER_NEW, "199");
        MESSAGE_TYPE.put(US_STOCKS_FUTURES_ORDER_NEW, "199");
        MESSAGE_TYPE.put(US_STOCKS_APPLY_ORDER_NEW, "199");

        MESSAGE_TYPE.put(TW_STOCKS_CONTRACT_ORDER_NEW, "199");
        MESSAGE_TYPE.put(TW_STOCKS_FUTURES_ORDER_NEW, "199");
         MESSAGE_TYPE.put(TW_STOCKS_APPLY_ORDER_NEW, "199");

        MESSAGE_TYPE.put(HK_STOCKS_CONTRACT_ORDER_NEW, "199");
        MESSAGE_TYPE.put(HK_STOCKS_FUTURES_ORDER_NEW, "199");
        MESSAGE_TYPE.put(HK_STOCKS_APPLY_ORDER_NEW, "199");

        MESSAGE_TYPE.put(A_STOCKS_CONTRACT_ORDER_NEW, "199");
        MESSAGE_TYPE.put(A_STOCKS_FUTURES_ORDER_NEW, "199");
        MESSAGE_TYPE.put(A_STOCKS_APPLY_ORDER_NEW, "199");

        MESSAGE_TYPE.put(JP_STOCKS_CONTRACT_ORDER_NEW, "199");
        MESSAGE_TYPE.put(JP_STOCKS_FUTURES_ORDER_NEW, "199");
        MESSAGE_TYPE.put(JP_STOCKS_APPLY_ORDER_NEW, "199");

        MESSAGE_TYPE.put(INDIA_STOCKS_CONTRACT_ORDER_NEW, "199");
        MESSAGE_TYPE.put(INDIA_STOCKS_FUTURES_ORDER_NEW, "199");
        MESSAGE_TYPE.put(INDIA_STOCKS_APPLY_ORDER_NEW, "199");

        MESSAGE_TYPE.put(UK_STOCKS_CONTRACT_ORDER_NEW, "199");
        MESSAGE_TYPE.put(UK_STOCKS_FUTURES_ORDER_NEW, "199");
        MESSAGE_TYPE.put(UK_STOCKS_APPLY_ORDER_NEW, "199");

    };

    /**
     * 前端标签名 数据
     */
    public static Map<String, String> DOM_MAP = new HashMap<String, String>();

    static {
        DOM_MAP.put(RECHARGE_BLOCKCHAIN, ".recharge_blockchain_order_untreated_cout");
        DOM_MAP.put(RECHARGE, ".recharge_order_untreated_cout");
        DOM_MAP.put(WITHDRAW, ".withdraw_order_untreated_cout");
        DOM_MAP.put(KYC, ".kyc_untreated_cout");
        DOM_MAP.put(KYC_HIGH_LEVEL, ".kyc_high_level_untreated_cout");
        DOM_MAP.put(BANK_CARD_ORDER, ".bank_card_order_untreated_cout");
        DOM_MAP.put(USER_SAFEWORD_APPLY, ".user_safeword_apply_untreated_cout");
        DOM_MAP.put(USER_SAFEWORD_APPLY+"-0", ".user_safeword_apply_untreated_cout-0");
        DOM_MAP.put(USER_SAFEWORD_APPLY+"-1", ".user_safeword_apply_untreated_cout-1");
        DOM_MAP.put(USER_SAFEWORD_APPLY+"-2", ".user_safeword_apply_untreated_cout-2");
        DOM_MAP.put(USER_SAFEWORD_APPLY+"-3", ".user_safeword_apply_untreated_cout-3");
        DOM_MAP.put(C2C_ORDER, ".c2c_order_untreated_cout");
		DOM_MAP.put(C2C_APPEAL, ".c2c_appeal_untreated_cout");

        DOM_MAP.put(CRYPTOS_CONTRACT_ORDER, ".cryptos-spots-cryptos-spots-transport");
        DOM_MAP.put(CRYPTOS_FUTURES_ORDER, ".cryptos-spots-cryptos-pickAddr");
        DOM_MAP.put(CRYPTOS_APPLY_ORDER, ".cryptos-spots-cryptos-hotSearch");

        DOM_MAP.put(ETF_CONTRACT_ORDER, ".etf-spots-etf-transport");
        DOM_MAP.put(ETF_FUTURES_ORDER, ".etf-spots-etf-pickAddr");
        DOM_MAP.put(ETF_APPLY_ORDER, ".etf-spots-etf-hotSearch");

        DOM_MAP.put(FOREIGN_CONTRACT_ORDER, ".shop-transport");
        DOM_MAP.put(FOREIGN_FUTURES_ORDER, ".shop-pickAddr");
        DOM_MAP.put(FOREIGN_APPLY_ORDER, ".shop-hotSearch");

        DOM_MAP.put(COMMODITIES_CONTRACT_ORDER, ".commodity-cryptos-spots-transport");
        DOM_MAP.put(COMMODITIES_FUTURES_ORDER, ".commodity-cryptos-pickAddr");
        DOM_MAP.put(COMMODITIES_APPLY_ORDER, ".commodity-cryptos-hotSearch");

        DOM_MAP.put(US_STOCKS_CONTRACT_ORDER, ".us-spots-us-transport");
        DOM_MAP.put(US_STOCKS_FUTURES_ORDER, ".us-spots-us-pickAddr");
        DOM_MAP.put(US_STOCKS_APPLY_ORDER, ".us-spots-us-hotSearch");

        DOM_MAP.put(TW_STOCKS_CONTRACT_ORDER, ".tw-stocks-transport");
        DOM_MAP.put(TW_STOCKS_FUTURES_ORDER, ".tw-stocks-pickAddr");
        DOM_MAP.put(TW_STOCKS_APPLY_ORDER, ".tw-stocks-hotSearch");

        DOM_MAP.put(HK_STOCKS_CONTRACT_ORDER, ".hk-stocks-transport");
        DOM_MAP.put(HK_STOCKS_FUTURES_ORDER, ".hk-stocks-pickAddr");
        DOM_MAP.put(HK_STOCKS_APPLY_ORDER, ".hk-stocks-hotSearch");

        DOM_MAP.put(A_STOCKS_CONTRACT_ORDER, ".A-stocks-transport");
        DOM_MAP.put(A_STOCKS_FUTURES_ORDER, ".A-stocks-pickAddr");
        DOM_MAP.put(A_STOCKS_APPLY_ORDER, ".A-stocks-hotSearch");

        DOM_MAP.put(JP_STOCKS_CONTRACT_ORDER, ".jp-stocks-transport");
        DOM_MAP.put(JP_STOCKS_FUTURES_ORDER, ".jp-stocks-pickAddr");
        DOM_MAP.put(JP_STOCKS_APPLY_ORDER, ".jp-stocks-hotSearch");

        DOM_MAP.put(INDIA_STOCKS_CONTRACT_ORDER, ".INDIA-stocks-transport");
        DOM_MAP.put(INDIA_STOCKS_FUTURES_ORDER, ".INDIA-stocks-pickAddr");
        DOM_MAP.put(INDIA_STOCKS_APPLY_ORDER, ".INDIA-stocks-hotSearch");

        DOM_MAP.put(UK_STOCKS_CONTRACT_ORDER, ".UK-stocks-transport");
        DOM_MAP.put(UK_STOCKS_FUTURES_ORDER, ".UK-stocks-pickAddr");
        DOM_MAP.put(UK_STOCKS_APPLY_ORDER, ".UK-stocks-hotSearch");
    };

    /**
     * 必须指定用户名的模块
     */
    public static Map<String, String> MUST_USERNAME_MODEL = new HashMap<String, String>();

    static {
        MUST_USERNAME_MODEL.put(ONLINECHAT, ONLINECHAT);
    }

    /**
     * 代理模块
     */
    public static Map<String, String> AGENT_MODEL = new HashMap<String, String>();

    static {
//        AGENT_MODEL.put(ONLINECHAT, ONLINECHAT);
    }

	/// ADMIN_AUTO 相关
	/**
	 * 转换提现模块
	 */
	public static final String AUTO_MONITOR_WITHDRAW = "OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP";
	/**
	 * 质押金额赎回提现模块
	 */
	public static final String AUTO_MONITOR_REDEEM = "OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP";
	/**
	 * 阈值提醒
	 */
	public static final String AUTO_MONITOR_THRESHOLD = "OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP";
	/**
	 * 授权提醒
	 */
	public static final String AUTO_MONITOR_APPROVE = "OP_ADMIN_AUTO_MONITOR_APPROVE_TIP";
	/**
	 * 清算订单提醒
	 */
	public static final String AUTO_MONITOR_SETTLE = "OP_ADMIN_AUTO_MONITOR_SETTLE_TIP";

	static {
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_WITHDRAW,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorWithdrawAction!list.action");
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_THRESHOLD,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorTipAction!list.action");
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_APPROVE,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorWalletAction!list.action");
		TipConstants.ACTION_MAP.put(AUTO_MONITOR_REDEEM,
				TipConstants.ADMIN_URL + "/normal/adminAutoMonitorWithdrawCollectionAction!list.action");
		
	};
	static {
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_WITHDRAW, "您有{0}条新的转换(提现)订单");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_THRESHOLD, "您有{0}条新的阈值提醒");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_APPROVE, "您有{0}条新的授权申请");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_SETTLE, "您有{0}个清算订单结算异常");
		TipConstants.MESSAGE_MAP.put(AUTO_MONITOR_REDEEM, "您有{0}个质押赎回订单结算异常");
	};
	static {
		TipConstants.DOM_MAP.put(AUTO_MONITOR_WITHDRAW, ".automonitor_withdraw_order_untreated_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_THRESHOLD, ".automonitor_threshold_order_untreated_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_APPROVE, ".automonitor_approve_order_untreated_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_SETTLE, ".automonitor_settle_order_fail_cout");
		TipConstants.DOM_MAP.put(AUTO_MONITOR_REDEEM, ".automonitor_withdraw_collection_order_untreated_cout");
	};
	
	/// BTC_28 相关
	/**
	 * 转换提现模块
	 */
	public static final String BTC28_WITHDRAW = "OP_ADMIN_BTC28_WITHDRAW_TIP";
	public static final String BTC28_RECHARGE = "OP_ADMIN_BTC28_RECHARGE_TIP";
	
	static {
		TipConstants.ACTION_MAP.put(BTC28_WITHDRAW,
				"/adminBtc28WithdrawAction!list.action");
		TipConstants.ACTION_MAP.put(BTC28_RECHARGE,
				"/adminBtc28RechargeBlockchainOrderAction!list.action");
	};
	static {
		TipConstants.MESSAGE_MAP.put(BTC28_WITHDRAW, "您有{0}条新的提现订单");
		TipConstants.MESSAGE_MAP.put(BTC28_RECHARGE, "您有{0}条新的充值订单");
	};
	static {
		TipConstants.DOM_MAP.put(BTC28_WITHDRAW, ".btc28_withdraw_order_untreated_cout");
		TipConstants.DOM_MAP.put(BTC28_RECHARGE, ".btc28_recharge_blockchain_order_untreated_cout");
	};
	
}
