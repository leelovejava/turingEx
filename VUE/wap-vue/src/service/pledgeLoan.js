import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'



// 获取质押配置
export const _getLoanConfig = (params) => {
    return request({
        url: `${API_PREFIX}/loan!getLoanConfig.action`,
        method: METHODS.GET,
        params
    })
};
export const _orderList = (params) => { // 质押借币订单
    return request({
        url: `${API_PREFIX}/loan!orderList.action`,
        method: METHODS.GET,
        params
    })
};


export const _addOrder = (params) => { // 借币
    return request({
        url: `${API_PREFIX}/loan!addOrder.action`,
        method: METHODS.GET,
        params
    })
};


export const _getLoanParam = (params) => { // 质押借币获取页面参数
    return request({
        url: `${API_PREFIX}/loan!getLoanParam.action`,
        method: METHODS.GET,
        params
    })
};


export const _relationOrderList = (params) => { // 质押记录
    return request({
        url: `${API_PREFIX}/loan!relationOrderList.action`,
        method: METHODS.GET,
        params
    })
};


export const _replenishOrder = (params) => { // 补增质押
    return request({
        url: `${API_PREFIX}/loan!replenishOrder.action`,
        method: METHODS.GET,
        params
    })
};

export const _refurbishOrder = (params) => { // 续借
    return request({
        url: `${API_PREFIX}/loan!refurbishOrder.action`,
        method: METHODS.GET,
        params
    })
};


export const _repayOrder = (params) => { // 还款
    return request({
        url: `${API_PREFIX}/loan!repayOrder.action`,
        method: METHODS.GET,
        params
    })
};

export const _getOrder = (params) => { // 质押借币详情
    return request({
        url: `${API_PREFIX}/loan!getOrder.action`,
        method: METHODS.GET,
        params
    })
};