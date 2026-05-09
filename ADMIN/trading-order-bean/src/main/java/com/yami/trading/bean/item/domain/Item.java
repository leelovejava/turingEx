package com.yami.trading.bean.item.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.yami.trading.common.domain.BaseEntity;
import com.yami.trading.common.lang.LangUtils;
import com.yami.trading.common.util.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Locale;

/**
 * 产品Entity
 *
 * @author lucas
 * @version 2023-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_item")
public class Item extends BaseEntity {
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
     * 贵金属
     */
    public static final String UK_METALS = "UK-metals";

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
    /**
     * 印度股
     */
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
     * 加拿大股票
     */
    public final static String CAD_STOCKS = "CAD-stocks";
    /**
     * 法国股股票
     */
    public final static String FR_STOCKS = "FR-stocks";

    public final static String SG_STOCKS = "SG-stocks";



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

    private static final long serialVersionUID = 1L;


    public static List<String> types = Lists.newArrayList(forex, commodities, cryptos, indices, US_STOCKS, HK_STOCKS, UK_STOCKS,
            A_STOCKS, TW_STOCKS, JP_STOCKS, INDIA_STOCKS, UK_STOCKS, DE_STOCKS, BZ_STOCKS,
            CAD_STOCKS, FR_STOCKS, SG_STOCKS);

    private String name;

    private String enName;

    @NotBlank
    private String symbol;

    /**
     * 数据源编码
     */
    private String symbolData;

    /**
     * 最小浮动
     */
    private double pips;

    /**
     * 最小浮动金额（以交易金额计算）
     */
    private double pipsAmount;

    private double adjustmentValue;
    /**
     * 行情控制持续时间(秒)
     */
    private double adjustmentDurationSecond;

    /**
     * 每张金额
     */
    private double unitAmount;

    /**
     * 每张手续费
     */
    private double unitFee;

    /**
     * 合约手续费  百分比设置
     */
    private double unitPercentage;

    /**
     * 市场
     */
    private String market;

    /**
     * 小数位精度
     */
    private int decimals;

    /**
     * 交易量倍数
     */
    private double multiple;


    /**
     * 借贷利率
     */
    private double borrowingRate;

    /**
     * 币种全称
     */
    private String symbolFullName;

    private String type;

    private String category;

    private String openCloseType;

    private String fake;

    /**
     * 板块
     */
    private String board;

    /**
     * 默认： 999999
     */
    private String sorted;

    @ApiModelProperty("报价货币")
    private String quoteCurrency;

    @ApiModelProperty("前端显示状态，1显示，0不显示")
    private String showStatus;
    @ApiModelProperty("交易状态，1显示，0不显示")
    private String tradeStatus;

    @ApiModelProperty("状态，1启用，0禁止")
    private String enable;
    @ApiModelProperty("市价买，1是，0否")
    private String canBuyAtMarketPrice;
    @ApiModelProperty("市价卖，1是，0否")
    private String canSellAtMarketPrice;
    @ApiModelProperty("限价可买，1是，0否")
    private String limitCanBuy;
    @ApiModelProperty("限价可卖，1是，0否")
    private String limitCanSell;
    @TableField(exist = false)
    private boolean isOpen;

    //默认激活，一直采集的
    public final static String DEFAULT_ACTIVE = "default_active";
    //被动激活，一直采集的
    public final static String ACTIVE = "active";
    // 冻结，不采集的
    public final static String FREEZE = "freeze";

    // default_active 默认激活，active激活，freeze 冻结
    private String crawlStatus;

    public String getName() {
        if (LangUtils.hasLang()) {
            if (LangUtils.isTWItem()) {
                return HanLP.convertToTraditionalChinese(name);
            }
            if (LangUtils.isEnItem() && StringUtils.isNotEmpty(enName)) {
                return enName;
            }
        }
        return name;
    }

    /**
     * 数字货币采集数据源的名称
     */
    private String remarks;

    @JsonIgnore
    public String getSourceName() {
        return name;
    }

    public boolean isActive() {
        return StrUtil.toString(crawlStatus).toLowerCase(Locale.ROOT).contains("active");
    }
}
