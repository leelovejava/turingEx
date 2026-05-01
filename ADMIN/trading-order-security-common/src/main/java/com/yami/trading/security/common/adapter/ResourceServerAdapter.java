package com.yami.trading.security.common.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 授权过滤器的配置
 *
 * @author admin
 */
@Component
public class ResourceServerAdapter extends DefaultAuthConfigAdapter {

    @Value("${spring.profiles.active}")
    private String env;

    public static final List<String> EXCLUDE_PATH = Arrays.asList(
            "/webjars/**",
            "/swagger/**",
            "/v2/api-docs",
            "/doc.html",
            "/favicon.ico",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/captcha/**",
            "/adminLogin",
            "/api/adminLogin",
            "/hobi!**",
            "/api/public**",
            "/websocket/**",
            "/api/captcha/**",
            "/api/login",
            "/api/user/login",
            "/api/syspara!getSyspara.action",
            "/api/user/getImageCode",
            "/api/registerNoVerifcode",
            "/api/registerVerifcode",
            "/api/user/registerUsername",
            "/api/systemInfoSocketServer",
            "/api/hobi**",
            "/api/normal/**",
            "/api/cms!get.action",
            "/api/user/register",
            "/api/uploadFile",
            "/api/api/uploadFile",
            "/api/idcode/execute",
            "/api/item!queryByScene",
            "/api/item!moneyFlow.action",
            "/api/item!board.action",
            "/api/item!queryBySymbol.action",
            "/api/item!relateStocks.action",
            "/api/public/uploadimg/!execute.action",
            "/api/websocket/**",
            "/api/projectBreed/getAll",
            "/api/projectBreed/getConstituentStockList",
            "/api/item/itemSummary/**",
//            "/etf/klineConfig/**",
            "/api/hobi!getDepth.action",
            "/api/user/getUserNameVerifTarget",
            "/api/user/resetPsw",
            "/api/exchangerate!list.action",
            "/api/information!list.action",
            "/etf/robot/list",
            "/api/banner!list.action",
            "/api/news!list_v2_popup.action",
            "/api/news!list.action",
            "/api/tip/getRandomByType",
            "/api/tip/getRandom",
            "/api/dapp!login.action",
            "/druid/**",
            "/api/timezone/info",
            "/api/newSharesConfig/newIssueList"

    );


    @Override
    public List<String> excludePathPatterns() {
//        if(StringUtils.isNotEmpty(env) && env.contains("local")){
//            return Lists.newArrayList("/**");
//        }
        return EXCLUDE_PATH;
    }

    @Override
    public List<String> maybeAuthUri() {
        return Arrays.asList(
                "/api/contractApplyOrder!openview.action",
                "/api/contractApplyOrder!closeview.action",
                "/api/assets!getContractBySymbolType.action",
                "/api/futuresOrder!openview.action",
                "/api/newOnlinechat**",
                "/api/exchangerateuserconfig!get.action",
                "/api/item/itemUserOptionalList/isItemHasAddGlobal",
                "/api/item/itemUserOptionalList/list",
                "/api/wallet/getUsdt",
                "/api/demo/**",
                "/api/timezone/info",
                "/api/check/network",
                "/adminLogin",
                "/api/item!list.action",
                "/riskclient/**"
        );
    }
}
