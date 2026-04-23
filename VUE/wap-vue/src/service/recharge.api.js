import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'

//根据币种获取链地址
export const _getBlock = (params) => {
    return request({
        url: `${API_PREFIX}/channelBlockchain!getBlockchainName.action`,
        method: METHODS.GET,
        params
    })
};

//获取USD充值session_token
export const _getRechargeToken = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/rechargeOpen`,
        method: METHODS.GET,
        params
    })
};

//提交充值申请
export const _rechargeApply = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/recharge`,
        method: METHODS.POST,
        data: params
    })
};

//获取银行卡充值session_token
export const _getSessionToken = () => {
    return request({
        url: `${API_PREFIX}/c2cOrder/orderOpen`,
        method: METHODS.POST,
    })
}

//数字货币充值记录
export const _getRechargeList = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/list`,
        method: METHODS.GET,
        params
    })
};

//获取 外汇货币充提记录
export const _foreignRechargeWith = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder/list`,
        method: METHODS.GET,
        params
    })
};
//充值提现
export const c2cOrder = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder/apply`,
        method: METHODS.POST,
        data: params
    })
};
//银行卡订单详情
export const getc2cOrder = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder/get`,
        method: METHODS.GET,
        params
    })
};
//银行卡取消订单
export const c2cOrderApply = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder/orderCancel`,
        method: METHODS.POST,
        data: params
    })
};
//充提订单详情
export const _getRechargeBlockchain = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/get`,
        method: METHODS.GET,
        params
    })
};
//提现订单详情
export const _getWithdraw = (params) => {
    return request({
        url: `${API_PREFIX}/withdraw/get`,
        method: METHODS.GET,
        params
    })
};
