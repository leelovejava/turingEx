package com.yami.trading.api.controller.trader;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.trading.api.service.UserCacheService;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.*;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.*;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.*;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.WalletLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("api/follow/wallet")
@Api(tags = "跟单钱包")
@Slf4j
public class ApiFollowWalletController {
    @Autowired
    ChannelBlockchainService channelBlockchainService;
    @Autowired
    RechargeBlockchainOrderService rechargeBlockchainOrderService;
    @Autowired
    WalletService walletService;
    @Autowired
    FollowWalletService followWalletService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserCacheService userCacheService;
    @Autowired
    WithdrawService withdrawService;
    @Autowired
    SysparaService sysparaService;
    @Autowired
    DataService dataService;
    @Autowired
    TipService tipService;
    @Autowired
    ItemService itemService;
    @Autowired
    WalletLogService walletLogService;

    @Autowired
    WalletTransferLogService walletTransferLogService;

    @GetMapping("/getusdt.action")
    @ApiOperation(value = "获取usdt余额")
    public Result getUsdt() {
        // usdt余额
        Map<String, Object> data = new HashMap<>();
        String partyId = SecurityUtils.getUser().getUserId();
        DecimalFormat df2 = new DecimalFormat("#.##");
        // 向下取整
        df2.setRoundingMode(RoundingMode.FLOOR);
        FollowWallet wallet = new FollowWallet();
        if (!"".equals(partyId) && partyId != null) {
            wallet = followWalletService.saveWalletByPartyId(partyId);
        }
        double money = wallet.getMoney().doubleValue();
        // 账户剩余资金
        data.put("money", df2.format(money));
        return Result.succeed(data);
    }

    /**
     * 资金划转
     */
    @GetMapping("transfer.action")
    @ApiOperation("资金划转")
    public Result transfer(HttpServletRequest request) {
        String type = request.getParameter("type"); // 0-现货账户转跟单账户，1-跟单账户转现货账户
        String amount_param = request.getParameter("amount"); // 划转金额
        String partyId = SecurityUtils.getCurrentUserId();
        Wallet wallet = walletService.findByUserId(partyId);
        FollowWallet followWallet = followWalletService.findByUserId(partyId);

        if (StringUtils.isEmptyString(type)) {
            throw new BusinessException("转出入类型不能为空!");
        }
        if (StringUtils.isEmptyString(amount_param)) {
            throw new BusinessException("转出入金额不能为空!");
        }
        if (!StringUtils.isDouble(amount_param)) {
            throw new BusinessException("转出入金额格式不正确!");
        }
        BigDecimal amount = new BigDecimal(amount_param);
        if (amount.compareTo(new BigDecimal(0)) <= 0) {
            throw new BusinessException("转出入金额必须大于0!");
        }

        Date now = new Date();
        WalletTransferLog walletTransferLog = new WalletTransferLog();
        walletTransferLog.setUuid(ApplicationUtil.getCurrentTimeUUID());
        walletTransferLog.setPartyId(partyId);
        walletTransferLog.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        walletTransferLog.setAmount(amount.doubleValue());
        walletTransferLog.setCreateTime(now);
        walletTransferLog.setCategory(type);
        walletTransferLog.setWallettype(Constants.WALLET_USDT);
        walletTransferLog.setCreateTimeTs(now.getTime() / 1000L);

        if ("0".equals(type)) {
            if(wallet.getMoney().compareTo(amount) < 0) {
                throw new BusinessException("现货账户余额不足!");
            }
//            wallet.setMoney(wallet.getMoney().subtract(amount));
//            followWallet.setMoney(followWallet.getMoney().add(amount));

            walletService.updateMoney(Constants.WALLET_USDT, partyId, BigDecimal.ZERO.subtract(amount), BigDecimal.ZERO, Constants.MONEYLOG_TRANSFER_IN,
                    Constants.WALLET_USDT, Constants.MONEYLOG_TRANSFER_IN, "现货账户转入跟单账户，订单号[" + walletTransferLog.getOrderNo() + "]");
//            followWalletService.updateMoney(Constants.WALLET_USDT, partyId, BigDecimal.ZERO.add(amount), BigDecimal.ZERO, Constants.MONEYLOG_TRANSFER_IN,
//                    Constants.WALLET_USDT, Constants.MONEYLOG_TRANSFER_IN, "现货账户转入跟单账户，订单号[" + walletTransferLog.getOrderNo() + "]");
//            followWalletService.update(followWallet, Wrappers.<FollowWallet>lambdaUpdate().eq(FollowWallet::getUserId, partyId));
            followWalletService.updateMoney(Constants.WALLET_USDT, partyId, amount, BigDecimal.ZERO, Constants.MONEYLOG_TRANSFER_IN,
                    Constants.WALLET_USDT, Constants.MONEYLOG_TRANSFER_IN, "现货账户转入跟单账户，订单号[" + walletTransferLog.getOrderNo() + "]");
        } else {
            if(followWallet.getMoney().compareTo(amount) < 0) {
                throw new BusinessException("跟单账户余额不足!");
            }
//            followWallet.setMoney(followWallet.getMoney().subtract(amount));
//            wallet.setMoney(wallet.getMoney().add(amount));

            walletService.updateMoney(Constants.WALLET_USDT, partyId, amount, BigDecimal.ZERO, Constants.MONEYLOG_TRANSFER_OUT,
                    Constants.WALLET_USDT, Constants.MONEYLOG_TRANSFER_OUT, "跟单账户转入现货账户，订单号[" + walletTransferLog.getOrderNo() + "]");
            followWalletService.updateMoney(Constants.WALLET_USDT, partyId, BigDecimal.ZERO.subtract(amount), BigDecimal.ZERO, Constants.MONEYLOG_TRANSFER_OUT,
                    Constants.WALLET_USDT, Constants.MONEYLOG_TRANSFER_OUT, "跟单账户转入现货账户，订单号[" + walletTransferLog.getOrderNo() + "]");
        }
        walletTransferLogService.saveRecord(walletTransferLog);
        return Result.succeed("划转成功");
    }

    /**
     * 资金划转历史记录
     */
    @GetMapping("history.action")
    @ApiOperation("资金划转历史记录")
    public Result history(HttpServletRequest request) {
        String partyId = SecurityUtils.getCurrentUserId();
        String type = request.getParameter("type"); // 0-现货账户转跟单账户，1-跟单账户转现货账户
        LambdaQueryWrapper<WalletTransferLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WalletTransferLog::getPartyId, partyId);
        if (!StringUtils.isEmptyString(type)) {
            lambdaQueryWrapper.eq(WalletTransferLog::getCategory, type);
        }

        List<WalletTransferLog> walletTransferLogs = walletTransferLogService.list(lambdaQueryWrapper);
        return Result.succeed(walletTransferLogs);
    }

    /**
     * 获取币种钱包
     */
    @GetMapping("list.action")
    @ApiOperation("获取币种钱包")
    public Result list() {
        Map<String, Object> map = new LinkedHashMap<>();
        FollowWallet usdt = null;
        String partyId = SecurityUtils.getUser().getUserId();
        if (StringUtils.isNotEmpty(partyId)) {
            usdt = this.followWalletService.saveWalletByPartyId(partyId);
        }
        DecimalFormat df2 = new DecimalFormat("#.########");
        // 向下取整
        df2.setRoundingMode(RoundingMode.FLOOR);
        if (null == usdt) {
            usdt = new FollowWallet();
            usdt.setMoney(new BigDecimal(0));
            map.put("USDT", usdt);
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            usdt.setMoney(new BigDecimal(df.format(usdt.getMoney())));
            map.put("USDT", usdt);
        }

        // 其他币账户
        List<Item> list_it = this.itemService.cacheGetByMarket("");
        List<String> list_symbol = new ArrayList<String>();
        for (int i = 0; i < list_it.size(); i++) {
            Item items = list_it.get(i);
            list_symbol.add(items.getSymbol());
        }
        List<Item> items = this.itemService.cacheGetAll();
        Collections.sort(items, new Comparator<Item>() {
            // 按id排序
            @Override
            public int compare(Item arg0, Item arg1) {
                return arg0.getUuid().compareTo(arg1.getUuid());
            }
        });

        List<FollowWalletExtend> walletExtends = null;
        if (StringUtils.isNotEmpty(partyId)) {
            walletExtends = this.followWalletService.findExtend(partyId, list_symbol);
        }
        FollowWalletExtend walletExtend = new FollowWalletExtend();
        // 如果是空
        if (null == walletExtends) {
            for (int i = 0; i < items.size(); i++) {
                walletExtend.setWallettype(items.get(i).getSymbol().toUpperCase());
                walletExtend.setAmount(0);
                map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
            }
        }
        // 如果不为空且2个相同
        if (walletExtends != null && walletExtends.size() == items.size()) {
            for (int i = 0; i < walletExtends.size(); i++) {
                if (null == walletExtends.get(i)) {
                    continue;
                }
                walletExtend = walletExtends.get(i);
                usdt.setMoney(new BigDecimal(df2.format(usdt.getMoney())));
                walletExtend.setAmount(Double.valueOf(df2.format(walletExtend.getAmount())));
                map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
            }
        }
        // 如果不为空 且数据库里的少于币种
        int temp = 0;
        if (walletExtends != null && walletExtends.size() < items.size()) {
            for (int i = 0; i < items.size(); i++) {
                for (int j = 0; j < walletExtends.size(); j++) {
                    walletExtend = walletExtends.get(j);
                    if (walletExtend.getWallettype().equals(items.get(i).getSymbol())) {
                        walletExtend.setAmount(Double.valueOf(df2.format(walletExtend.getAmount())));
                        map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
                        temp = 1;
                        break;
                    }
                }
                if (0 == temp) {
                    walletExtend = new FollowWalletExtend();
                    walletExtend.setWallettype(items.get(i).getSymbol());
                    walletExtend.setAmount(0);
                    map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
                } else {
                    temp = 0;
                }
            }
        }
        return Result.succeed(map);
    }

    /**
     * 钱包账户资产（所有币种）
     */
    @GetMapping("getAll.action")
    public Object getAll(HttpServletRequest request) {
        String symbolType = request.getParameter("symbolType");
        return this.getWalletExtends(request, true, symbolType);
    }

    /**
     * all：true/获取全部；false/获取usdt、btc、eth；
     */
    public Result getWalletExtends(HttpServletRequest request, boolean all, String symbolType) {
        String symbol = request.getParameter("symbol");
        Map<String, Object> mapRet = new LinkedHashMap<String, Object>();
        DecimalFormat df2 = new DecimalFormat("#.########");
        // 向下取整
        df2.setRoundingMode(RoundingMode.FLOOR);
        // String partyId ="dcc0dd35a49c383dadabc4dc030afe70";
        String partyId = SecurityUtils.getCurrentUserId();
        FollowWallet usdt = null;
        if (StringUtils.isNotEmpty(partyId)) {
            usdt = followWalletService.saveWalletByPartyId(partyId);
        }
        if (null == usdt) {
            usdt = new FollowWallet();
            usdt.setMoney(new BigDecimal(0));
            usdt.setLockMoney(new BigDecimal(0));
            usdt.setFreezeMoney(new BigDecimal(0));
            mapRet.put("usdt", usdt.getMoney());
            mapRet.put("lock_money", usdt.getLockMoney());
            mapRet.put("freeze_money", usdt.getFreezeMoney());
        } else {
            DecimalFormat df = new DecimalFormat("#.########");
            df.setRoundingMode(RoundingMode.FLOOR);
            usdt.setMoney(new BigDecimal(Double.valueOf(df.format(usdt.getMoney()))));
            usdt.setLockMoney(new BigDecimal(Double.valueOf(df.format(usdt.getLockMoney()))));
            usdt.setFreezeMoney(new BigDecimal(Double.valueOf(df.format(usdt.getFreezeMoney()))));
            mapRet.put("usdt", usdt.getMoney());
            mapRet.put("lock_money", usdt.getLockMoney());
            mapRet.put("freeze_money", usdt.getFreezeMoney());
        }
        // 其他币账户
        List<Item> list_it = this.itemService.cacheGetByMarket("");
        if (StringUtils.isNotEmpty(symbolType)) {
            list_it = list_it.stream().filter(i -> symbolType.equalsIgnoreCase(i.getType())).collect(Collectors.toList());
        }
        List<String> list_symbol = new ArrayList<>();
        if (!StringUtils.isNotEmpty(symbol)) {
            // symbol为空，获取所有的
            for (int i = 0; i < list_it.size(); i++) {
                Item items = list_it.get(i);
                list_symbol.add(items.getSymbol());
            }
        } else {
            List<String> symbolList = Arrays.asList(symbol.split(","));
            for (int i = 0; i < list_it.size(); i++) {
                Item items = list_it.get(i);
                // 只添加所有币种和参数symbol都有的
                if (symbolList.contains(items.getSymbol())) {
                    list_symbol.add(items.getSymbol());
                }
            }
        }
        List<Item> items = this.itemService.cacheGetAll();
        // 按id排序
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item arg0, Item arg1) {
                return arg0.getUuid().compareTo(arg1.getUuid());
            }
        });
        Map<String, Item> itemMap = new HashMap<String, Item>();
        for (int i = 0; i < items.size(); i++) {
            itemMap.put(items.get(i).getSymbol(), items.get(i));
        }
        List<FollowWalletExtend> walletExtends = null;
        if (StringUtils.isNotEmpty(partyId)) {
            if (CollectionUtil.isNotEmpty(list_symbol)){
                walletExtends = followWalletService.findExtend(partyId, list_symbol);
            }
        }
        if (null == walletExtends) {
            walletExtends = new ArrayList<>();
        }
        List<FollowWalletExtend> walletExtendsRet = new ArrayList<FollowWalletExtend>();
        int temp = 0;
        for (int i = 0; i < list_symbol.size(); i++) {
            for (int j = 0; j < walletExtends.size(); j++) {
                FollowWalletExtend walletExtend = walletExtends.get(j);
                if (walletExtend.getWallettype().toUpperCase().equals(list_symbol.get(i).toUpperCase())) {
                    walletExtend.setAmount(Double.valueOf(df2.format(walletExtend.getAmount())));
                    walletExtend.setLockAmount(Double.valueOf(df2.format(walletExtend.getLockAmount())));
                    walletExtend.setFreezeAmount(Double.valueOf(df2.format(walletExtend.getFreezeAmount())));
                    walletExtendsRet.add(walletExtend);
                    temp = 1;
                }
            }
            if (0 == temp) {
                FollowWalletExtend walletExtend = new FollowWalletExtend();
                if (StringUtils.isNotEmpty(partyId)) {
                    walletExtend.setPartyId(partyId);
                }
                walletExtend.setWallettype(list_symbol.get(i));
                walletExtend.setAmount(0);
                walletExtend.setLockAmount(0);
                walletExtend.setFreezeAmount(0);
                walletExtend.setName(itemMap.get(list_symbol.get(i)).getName());
                walletExtendsRet.add(walletExtend);
            }
            temp = 0;
        }
        String symbolsStr = "";
        for (int i = 0; i < list_symbol.size(); i++) {
            if (i != 0) {
                symbolsStr = symbolsStr + "," + list_symbol.get(i);
            } else {
                symbolsStr = list_symbol.get(i);
            }
        }
        List<Realtime> realtime_all = dataService.realtime(symbolsStr);
        if (realtime_all.size() <= 0) {
//				throw new BusinessException("系统错误，请稍后重试");
        }
        Map<String, Realtime> realtimeMap = new HashMap<String, Realtime>();
        for (int i = 0; i < realtime_all.size(); i++) {
            realtimeMap.put(realtime_all.get(i).getSymbol(), realtime_all.get(i));
        }
        List<Map<String, Object>> extendsList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < walletExtendsRet.size(); i++) {
            if (false == all) {
                // 只要btc、eth
                if (!walletExtendsRet.get(i).getWallettype().equals("btc") && !walletExtendsRet.get(i).getWallettype().equals("eth")) {
                    continue;
                }
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", walletExtendsRet.get(i).getName());
            String wallettype = walletExtendsRet.get(i).getWallettype();
            map.put("symbol", wallettype);
            Item item = itemMap.get(wallettype);
            if(item == null){
                log.error("get item by walletType error : {}", wallettype);
                continue;
            }
            map.put("symbol_data", item.getSymbolData());

            double volume = Arith.add(walletExtendsRet.get(i).getAmount(), walletExtendsRet.get(i).getFreezeAmount());
            map.put("volume", df2.format(volume));
            map.put("lock_amount", walletExtendsRet.get(i).getLockAmount());
            map.put("freeze_amount", walletExtendsRet.get(i).getFreezeAmount());
            map.put("usable", df2.format(walletExtendsRet.get(i).getAmount()));
            map.put("frozenAmount", walletExtendsRet.get(i).getFreezeAmount());
            Realtime rt = realtimeMap.get(walletExtendsRet.get(i).getWallettype());
            if (null != rt) {
                map.put("usdt", df2.format(Arith.mul(rt.getClose(), volume)));
            } else {
                map.put("usdt", 0);
            }
            extendsList.add(map);
        }
        if (!StringUtils.isNotEmpty(symbol) || symbol.contains("usdt")) {
            // 添加usdt到列表最前面
            Map<String, Object> mapUsdt = new HashMap<String, Object>();
            mapUsdt.put("name", "USDT/USDT");
            mapUsdt.put("symbol", "usdt");
            mapUsdt.put("symbol_data", "usdt");

            mapUsdt.put("volume", df2.format(Double.parseDouble(mapRet.get("usdt").toString())));
            if (mapRet.get("lock_money") != null) {
                mapUsdt.put("lock_amount", df2.format(Double.parseDouble(mapRet.get("lock_money").toString())));
            } else {
                mapUsdt.put("lock_amount", null);
            }
            if (mapRet.get("freeze_money") != null) {
                mapUsdt.put("freeze_amount", df2.format(Double.parseDouble(mapRet.get("freeze_money").toString())));
            } else {
                mapUsdt.put("freeze_amount", null);
            }
            mapUsdt.put("usdt", df2.format(Double.parseDouble(mapRet.get("usdt").toString())));
            mapUsdt.put("usable", df2.format(Double.parseDouble(mapRet.get("usdt").toString())));
            mapUsdt.put("frozenAmount", 0);
            extendsList.add(0, mapUsdt);
        }
        mapRet.put("extends", extendsList);
        return Result.succeed(mapRet);
    }

    /**
     * 钱包历史记录
     */
    @RequestMapping("records.action")
    public Object records(HttpServletRequest request) {
        // 页码
        String page_no = request.getParameter("page_no");
        // 充值category=recharge；提现category=withdraw；
        String category = request.getParameter("category");
        // 开始时间
        String start_time = request.getParameter("start_time");
        // 结束时间
        String end_time = request.getParameter("end_time");
        // 钱包类型 btc、eth...
        String wallet_type = request.getParameter("wallet_type");
        // 状态：0/初始状态，未知；1/成功；2/失败；
        String status = request.getParameter("status");
        Integer status_int = null;
        if (StringUtils.isNullOrEmpty(status)) {
            status_int = null;
        } else {
            if (!StringUtils.isInteger(status)) {
                throw new YamiShopBindException("状态不是整数");
            }
            if (Integer.valueOf(status).intValue() < 0) {
                throw new YamiShopBindException("状态不能小于0");
            }
            status_int = Integer.valueOf(status);
        }
        if (StringUtils.isNullOrEmpty(page_no)) {
            page_no = "1";
        }
        if (!StringUtils.isInteger(page_no)) {
            throw new YamiShopBindException("页码不是整数");
        }
        if (Integer.valueOf(page_no).intValue() <= 0) {
            throw new YamiShopBindException("页码不能小于等于0");
        }
        int page_no_int = Integer.valueOf(page_no).intValue();
        List<Map<String, Object>> data = this.walletLogService.pagedQueryRecords(page_no_int, 10, SecurityUtils.getUser().getUserId(), category, start_time, end_time, wallet_type, status_int).getRecords();
        for (Map<String, Object> log : data) {
            if (null == log.get("wallet_type") || !StringUtils.isNotEmpty(log.get("wallet_type").toString())) {
                log.put("wallet_type", Constants.WALLET);
            } else {
                log.put("wallet_type", log.get("wallet_type").toString().toUpperCase());
            }
        }
        return Result.succeed(data);
    }
}
