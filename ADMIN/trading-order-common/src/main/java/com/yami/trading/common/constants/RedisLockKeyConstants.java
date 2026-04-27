package com.yami.trading.common.constants;

public interface RedisLockKeyConstants {
    // ContractApplyOrder 记录产生 ContractOrder 记录，后缀： ContractApplyOrder 记录主键
    String LOCK_CONTRACT_APPLY_TO_ORDER_PREFIX = "lock_contract_apply_to_order_";

}
