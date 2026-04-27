package com.yami.trading.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.contract.domain.ContractOrder;
import com.yami.trading.bean.future.domain.FuturesOrder;
import com.yami.trading.bean.model.FollowWallet;
import com.yami.trading.bean.model.WalletExtend;
import com.yami.trading.bean.model.WalletTransferLog;
import com.yami.trading.service.impl.ContractAndFutureProfit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletTransferLogService extends IService<WalletTransferLog> {

    void saveRecord(WalletTransferLog walletTransferLog);
}
