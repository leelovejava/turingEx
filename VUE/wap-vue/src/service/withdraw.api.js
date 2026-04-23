import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'



//提现，进入页面，获取session_token
export const _widtGetSessionToken = () => {
    return request({
        url: `${API_PREFIX}/withdraw/withdrawOpen`,
        method: METHODS.GET,
    })
};

//提现申请
export const _withdrawApply = (params) => {
    return request({
        url: `${API_PREFIX}/withdraw/apply`,
        method: METHODS.POST,
        data: params
    })
};

//提现手续费计算
export const _withdrawFee = (params) => {
    return request({
        url: `${API_PREFIX}/withdraw/fee`,
        method: METHODS.GET,
        params
    })
};


//提现记录
export const _withdrawList = (params) => {
    return request({
        url: `${API_PREFIX}/withdraw/list`,
        method: METHODS.GET,
        params
    })
};


//提现详情
export const _withdrawDetail = (params) => {
    return request({
        url: `${API_PREFIX}/withdraw!get.action`,
        method: METHODS.POST,
        data: params
    })
};

//订单放行
export const _ctcOrderPass = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!order_pass.action`,
        method: "get",
        params
    })
};
//支付完成
export const _ctcOrderPayFinish = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!pay_finish.action`,
        method: "get",
        params
    })
};