package com.yami.trading.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.exchange.ExchangeApplyOrder;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.bean.future.domain.FuturesRedisKeys;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.FollowWallet;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.bean.model.WalletTransferLog;
import com.yami.trading.common.constants.WalletRedisKeys;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.dao.user.FollowWalletMapper;
import com.yami.trading.dao.user.WalletTransferLogMapper;
import com.yami.trading.service.FollowWalletService;
import com.yami.trading.service.MoneyLogService;
import com.yami.trading.service.WalletTransferLogService;
import com.yami.trading.service.contract.ContractApplyOrderService;
import com.yami.trading.service.contract.ContractOrderCalculationService;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.exchange.ExchangeApplyOrderService;
import com.yami.trading.service.finance.service.FinanceOrderService;
import com.yami.trading.service.future.FuturesOrderService;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.miner.service.MinerOrderService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.WalletExtendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WalletTransferLogServiceImpl extends ServiceImpl<WalletTransferLogMapper, WalletTransferLog> implements WalletTransferLogService {
   @Resource
   WalletTransferLogMapper walletTransferLogMapper;

    @Override
    public void saveRecord(WalletTransferLog walletTransferLog) {
        walletTransferLogMapper.insert(walletTransferLog);
    }
}
