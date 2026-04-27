package com.yami.trading.service.contract;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.contract.dto.ContractApplyOrderDTO;
import com.yami.trading.bean.contract.query.ContractApplyOrderQuery;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.item.dto.ItemLeverageDTO;
import com.yami.trading.bean.model.FollowWallet;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.Wallet;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.TipConstants;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.contract.ContractApplyOrderMapper;
import com.yami.trading.service.FollowWalletService;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.RechargeBonusService;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.item.ItemLeverageService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.rate.ExchangeRateService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单表Service
 *
 * @author lucas
 * @version 2023-03-29
 */
@Service
@Transactional
@Slf4j
public class ContractApplyOrderService extends ServiceImpl<ContractApplyOrderMapper, ContractApplyOrder> {

    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private UserService partyService;
    @Autowired
    private ItemService itemService;
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    @Autowired
    private ItemLeverageService itemLeverageService;
    @Autowired
    private MoneyLogService moneyLogService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private FollowWalletService followWalletService;
    @Autowired
    private ContractOrderService contractOrderService;
    @Autowired
    private TipService tipService;

    @Autowired
    private UserService userService;

    @Autowired
    private RechargeBonusService rechargeBonusService;
//    @Autowired
//    private ExchangeRateService exchangeRateService;

    private Logger logger = LogManager.getLogger(ContractApplyOrderService.class);
    public Page<Map<String, Object>> findList(Page<ContractApplyOrder> page, String partyId, String symbol, String type, String startTime, String endTime, String symbolType) {
        QueryWrapper<ContractApplyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        if ("orders".equalsIgnoreCase(type)) {
            queryWrapper.eq("state", ContractApplyOrder.STATE_SUBMITTED);
        } else if ("hisorders".equals(type)) {
            queryWrapper.in("state", ContractApplyOrder.STATE_CREATED, ContractApplyOrder.STATE_CANCELED);
        }
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        if (StringUtils.isNotEmpty(type) && StringUtils.isEmptyString(symbol)) {
            queryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        }
        queryWrapper.ge(StringUtils.isNotEmpty(startTime), "date_format(create_time,'%Y-%m-%d')", startTime);
        queryWrapper.le(StringUtils.isNotEmpty(endTime), "date_format(create_time,'%Y-%m-%d')", endTime);

        queryWrapper.orderByDesc("create_time");
        Page<ContractApplyOrder> contractApplyOrderPage = baseMapper.selectPage(page, queryWrapper);
        Page<Map<String, Object>> returnPage = new Page<>();
        BeanUtil.copyProperties(contractApplyOrderPage, returnPage);
        returnPage.setRecords(bulidData(contractApplyOrderPage.getRecords()));
        return returnPage;
    }

    public Page<ContractApplyOrder> findList(Page<ContractApplyOrder> page, String partyId,  String type,  String symbolType) {
        QueryWrapper<ContractApplyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_id", partyId);
        if ("orders".equalsIgnoreCase(type)) {
            queryWrapper.eq("state", ContractApplyOrder.STATE_SUBMITTED);
        } else if ("hisorders".equals(type)) {
            queryWrapper.in("state", ContractApplyOrder.STATE_CREATED, ContractApplyOrder.STATE_CANCELED);
        }
        List<String> symbols = itemService.findByType(symbolType).stream().map(Item::getSymbol).collect(Collectors.toList());
        symbols.add("-1");
        if (StringUtils.isNotEmpty(type)) {
            queryWrapper.in(StringUtils.isNotEmpty(symbolType), "symbol", symbols);
        }

        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectPage(page, queryWrapper);
    }

    public IPage<ContractApplyOrderDTO> listRecord(Page page, ContractApplyOrderQuery query) {
        return baseMapper.listRecord(page, query);
    }

    private List<Map<String, Object>> bulidData(List<ContractApplyOrder> list) {
        List<Map<String, Object>> data = new ArrayList();

        for (ContractApplyOrder order : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("order_no", order.getOrderNo());
            map.put("name", itemService.findBySymbol(order.getSymbol()).getName());
            map.put("symbol", order.getSymbol());

            long showCreateTimeTs = DateTimeTools.transferShowTimeToClientTime(order.getCreateTimeTs());
            map.put("create_time_ts", showCreateTimeTs);
            map.put("volume", order.getVolume());
            map.put("volume_open", order.getVolumeOpen());
            map.put("direction", order.getDirection());
            map.put("offset", order.getOffset());
            map.put("lever_rate", order.getLeverRate());
            map.put("price", order.getPrice());
            map.put("stop_price_profit", order.getStopPriceProfit());
            map.put("stop_price_loss", order.getStopPriceLoss());
            map.put("order_price_type", order.getOrderPriceType());
            map.put("state", order.getState());
            List<Realtime> realtime = dataService.realtime(order.getSymbol());
            if(realtime.size()>=1){
                map.put("mark_price",  realtime.get(0).getClose());
            }
            map.put("amount", order.getVolume().multiply(order.getUnitAmount()));
            map.put("amount_open", order.getVolumeOpen().multiply(order.getUnitAmount()));
            map.put("fee", order.getFee());
            map.put("deposit", order.getDeposit());
            data.add(map);
        }

        return data;
    }

    public void saveCreate(ContractApplyOrder order) {
        if (order.getVolumeOpen().doubleValue() % 1 != 0) {
            throw new YamiShopBindException("Parameter Error1");
        }

        if (order.getVolumeOpen().compareTo(BigDecimal.ZERO) <= 0) {
            throw new YamiShopBindException("Parameter Error2");
        }

        boolean orderOpen = this.sysparaService.find("order_open").getBoolean();
        if (!orderOpen) {
            throw new YamiShopBindException("不在交易时段");
        }

        Item item = this.itemService.findBySymbol(order.getSymbol());
        if (item == null) {
            throw new YamiShopBindException("Parameter Error3");
        }

        Date now = new Date();
        if (order.getCreateTime() == null) {
            order.setCreateTime(now);
        }
        if (order.getUpdateTime() == null) {
            order.setUpdateTime(now);
        }
        order.setCreateTimeTs(order.getCreateTime().getTime() / 1000L);
        order.setUpdateTimeTs(order.getUpdateTime().getTime() / 1000L);

        if (ContractApplyOrder.OFFSET_OPEN.equals(order.getOffset())) {
            this.open(order);
        } else if (ContractApplyOrder.OFFSET_CLOSE.equals(order.getOffset())) {
            this.close(order);
        }
    }


    /**
     * 开仓委托
     */
    public void open(ContractApplyOrder order) {
        Item item = this.itemService.findBySymbol(order.getSymbol());
//        String type = item.getType();
        List<ItemLeverageDTO> levers = itemLeverageService.findByItemId(item.getUuid());
        log.info("{}  --- order --- {}  --- {}", order.getSymbol(), item.getUuid(), levers.size());
        checkLever(order, levers);

        BigDecimal sourceLeverRate = order.getLeverRate();
        sourceLeverRate = sourceLeverRate == null ? BigDecimal.ZERO : sourceLeverRate;

        List<ContractOrder> contractOrderSubmitted = contractOrderService.findSubmitted(order.getPartyId(),
                order.getSymbol(), order.getDirection());
        for (int i = 0; i < contractOrderSubmitted.size(); i++) {
            BigDecimal targetLeverRate = contractOrderSubmitted.get(i).getLeverRate();
            targetLeverRate = targetLeverRate == null ? BigDecimal.ZERO : targetLeverRate;
            if (sourceLeverRate.compareTo(targetLeverRate) != 0) {
                throw new YamiShopBindException("存在不同杠杆的持仓单");
            }
        }

        List<ContractApplyOrder> applyOrderSubmittedList = this.findSubmitted(order.getPartyId(),
                order.getSymbol(), "open", order.getDirection());
        for (int i = 0; i < applyOrderSubmittedList.size(); i++) {
            BigDecimal targetLeverRate = applyOrderSubmittedList.get(i).getLeverRate();
            targetLeverRate = targetLeverRate == null ? BigDecimal.ZERO : targetLeverRate;
            if (sourceLeverRate.compareTo(targetLeverRate) != 0) {
                throw new YamiShopBindException("存在不同杠杆的持仓单");
            }
        }

        BigDecimal volume = order.getVolume();
        if(StringUtils.isEmptyString(order.getOrderNo())) {
            order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        }
        order.setUnitAmount(BigDecimal.valueOf(item.getUnitAmount()));

        order.setFee(BigDecimal.valueOf(item.getUnitFee()).multiply(order.getVolume()));
        order.setDeposit(BigDecimal.valueOf(item.getUnitAmount()).multiply(order.getVolume()));
        BigDecimal fee = order.getFee();
        if (order.getLeverRate() != null) {
            // 加上杠杆
            order.setVolume(order.getVolume().multiply(order.getLeverRate()));
            Syspara syspara = sysparaService.find("perpetual_contracts");
            if (ObjectUtils.isEmpty(syspara)||"0".equals(syspara.getSvalue())) {
                order.setFee(fee.multiply(order.getLeverRate()));
            }
        }
        
		Syspara syspara = sysparaService.find("u_standard_contract");
        BigDecimal deposit = order.getDeposit();
		if (ObjectUtils.isNotEmpty(syspara)) {
			if ("1".equals(syspara.getSvalue())) {
				BigDecimal volumeDeposit = order.getPrice().multiply(volume);
				order.setDeposit(volumeDeposit);
				logger.error("volumeDeposit::"+volumeDeposit+"LeverRate::"+order.getLeverRate());
				order.setFee(deposit.multiply(BigDecimal.valueOf(item.getUnitPercentage())));
			}
			logger.error("syspara::"+syspara.getSvalue());
		}
        
        order.setVolumeOpen(order.getVolume());

        if(ContractApplyOrder.ORDER_FOLLOW == order.getFollow()) { // 跟单订单
            FollowWallet followWallet = followWalletService.findByUserId(order.getPartyId());
            BigDecimal amountBefore = followWallet.getMoney();
//            fee = exchangeRateService.getUsdtByType(order.getFee(), type);
//            deposit = exchangeRateService.getUsdtByType(deposit, type);
            BigDecimal totalAmountCost = deposit.add(order.getFee());
            if (amountBefore.compareTo(totalAmountCost) < 0) {
                throw new YamiShopBindException("余额不足");
            }

            followWalletService.updateMoney(order.getSymbol(), order.getPartyId(), BigDecimal.ZERO.subtract(deposit), BigDecimal.ZERO
                    , Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_CONTRACT_OPEN, "委托单，订单号[" + order.getOrderNo() + "]"
            );
            followWalletService.updateMoney(order.getSymbol(), order.getPartyId(), BigDecimal.ZERO.subtract(fee), BigDecimal.ZERO
                    , Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_FEE, "委托单手续费，订单号[" + order.getOrderNo() + "]"
            );
        } else {
            Wallet wallet = this.walletService.findByUserId(order.getPartyId());
            BigDecimal amountBefore = wallet.getMoney();
//            fee = exchangeRateService.getUsdtByType(order.getFee(), type);
//            deposit = exchangeRateService.getUsdtByType(deposit, type);
            BigDecimal totalAmountCost = deposit.add(order.getFee());
            if (amountBefore.compareTo(totalAmountCost) < 0) {
                throw new YamiShopBindException("余额不足");
            }

            walletService.updateMoney(order.getSymbol(), order.getPartyId(), BigDecimal.ZERO.subtract(deposit), BigDecimal.ZERO
                    , Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_CONTRACT_OPEN, "委托单，订单号[" + order.getOrderNo() + "]"
            );
            walletService.updateMoney(order.getSymbol(), order.getPartyId(), BigDecimal.ZERO.subtract(fee), BigDecimal.ZERO
                    , Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_FEE, "委托单手续费，订单号[" + order.getOrderNo() + "]"
            );


            //ICE盘定制交易手续费返佣
           rechargeBonusService.saveTradeBounsHandle(order.getPartyId(),1, fee.doubleValue(),order.getOrderNo(),order.getSymbol());
        }
        save(order);
        User party = this.userService.getById(order.getPartyId());
        if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {
             Syspara isUnionStocks = sysparaService.find("is-union-stocks");

            if(isUnionStocks != null && isUnionStocks.getBoolean()){
                if(TipConstants.ACTION_APPLY_ITEM_MAP_NEW.get(item.getType()) != null){
                    tipService.saveTip(order.getUuid(), TipConstants.ACTION_APPLY_ITEM_MAP_NEW.get(item.getType()),party.getUserId());
                }
            }else{
                if(TipConstants.ACTION_APPLY_ITEM_MAP.get(item.getType()) != null){
                    logger.info("ACTION_APPLY_ITEM_MAP saveOpen1 -> " + item.getType() + "-->" + order.getOrderNo() + "-->" + order.getUuid());
                    tipService.saveTip(order.getUuid(), TipConstants.ACTION_APPLY_ITEM_MAP.get(item.getType()));
                }
            }
        }
    }

    /**
     * 平仓委托
     */
    public void close(ContractApplyOrder order) {
        Item item = this.itemService.findBySymbol(order.getSymbol());

        order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
        order.setUnitAmount(BigDecimal.valueOf(item.getUnitAmount()));

        BigDecimal volume = BigDecimal.ZERO;
        List<ContractOrder> contractOrderSubmitted = contractOrderService.findSubmitted(order.getPartyId(),
                order.getSymbol(), order.getDirection());
        for (int i = 0; i < contractOrderSubmitted.size(); i++) {
            volume = volume.add(contractOrderSubmitted.get(i).getVolume());
        }

        List<ContractApplyOrder> applyOrderSubmittedList = this.findSubmitted(order.getPartyId(),
                order.getSymbol(), ContractApplyOrder.OFFSET_CLOSE, order.getDirection());
        for (int i = 0; i < applyOrderSubmittedList.size(); i++) {
            volume = volume.subtract(applyOrderSubmittedList.get(i).getVolume());
        }
        if (order.getVolume().compareTo(volume) > 0) {
            throw new YamiShopBindException("可平仓合约张数不足");
        }
        save(order);
    }

    /**
     * 根据用户批量赎回订单
     *
     * @param partyId
     */
    public void saveCancelAllByPartyId(String partyId) {
        List<ContractApplyOrder> findSubmittedContractApplyOrders = findSubmitted(partyId, null, null, null);
        if (!CollectionUtils.isEmpty(findSubmittedContractApplyOrders)) {
            for (ContractApplyOrder applyOrder : findSubmittedContractApplyOrders) {
                saveCancel(applyOrder.getPartyId(), applyOrder.getOrderNo());
            }
        }
    }

    public void saveCancel(String partyId, String orderNo) {
        ContractApplyOrder order = this.findByOrderNo(orderNo);
        if (order == null || !"submitted".equals(order.getState()) || !partyId.equals(order.getPartyId())) {
            return;
        }
        order.setState("canceled");
        String symbol = order.getSymbol();
//        Item item = itemService.findBySymbol(symbol);
//        String type = item.getType();
        BigDecimal realAmount = order.getDeposit().add(order.getFee());
//        realAmount = exchangeRateService.getUsdtByType(realAmount, type);
        walletService.updateMoney(symbol,
                order.getPartyId(),
                realAmount,
                BigDecimal.ZERO,
                Constants.MONEYLOG_CATEGORY_CONTRACT, Constants.WALLET_USDT, Constants.MONEYLOG_CONTENT_CONTRACT_CONCEL, "撤单，订单号[" + order.getOrderNo() + "]");
        updateById(order);
        tipService.deleteTip(order.getUuid());
    }

    public ContractApplyOrder findByOrderNo(String orderNo) {
        QueryWrapper<ContractApplyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        queryWrapper.last("limit 1");
        List<ContractApplyOrder> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 验证杠杠倍数是否存在
     *
     * @param order
     * @param levers
     */
    private void checkLever(ContractApplyOrder order, List<ItemLeverageDTO> levers) {
        if (order.getLeverRate() != null && order.getLeverRate().compareTo(BigDecimal.ONE) != 0) {
            boolean findlevers = false;
            /**
             * 杠杆有配置
             */
            for (ItemLeverageDTO lever : levers) {
                if (order.getLeverRate().compareTo(new BigDecimal(lever.getLeverRate())) == 0){
                    findlevers = true;
                    break;
                }
            }
            if (!findlevers) {
                throw new YamiShopBindException("杠杆倍数不存在");
            }
        }
    }

    /**
     * 未处理状态的委托单
     *
     * @param partyId
     * @param symbol
     * @param offset
     * @param direction
     * @return
     */
    public List<ContractApplyOrder> findSubmitted(String partyId, String symbol, String offset, String direction) {
        QueryWrapper<ContractApplyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(partyId), "party_id", partyId);
        queryWrapper.eq(StrUtil.isNotBlank(symbol), "symbol", symbol);
        queryWrapper.eq(StrUtil.isNotBlank(offset), "offset", offset);
        queryWrapper.eq(StrUtil.isNotBlank(direction), "direction", direction);
        queryWrapper.eq("state", "submitted");
        return list(queryWrapper);
    }

    public List<ContractApplyOrder> findSubmitted() {
        QueryWrapper<ContractApplyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", "submitted");
        return list(queryWrapper);

    }
}
