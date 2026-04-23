
import request from '@/service/request'

import {
    METHODS
} from '@/config'
import {
    API_PREFIX
} from '@/config'

//币币交易-买入-开仓页面参数
const tradeBuyToken = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!openview.action`,
        method: METHODS.GET,
        params
    })

};
//币币交易-卖出-开仓页面参数
const tradeSellToken = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!closeview.action`,
        method: METHODS.GET,
        params
    })
};


//币币交易-买入
const tradeBuy = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!open.action`,
        method: METHODS.GET,
        params
    })
};

//币币交易-卖出
const tradeSell = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!close.action`,
        method: METHODS.GET,
        params
    })
};

//币币交易-交易记录
const tradeRecord = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!list.action`,
        method: METHODS.GET,
        params
    })
};

//币币交易-撤单
const tradeCancel = (params) => {
    console.log(params)
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!cancel.action`,
        method: METHODS.GET,
        params
    })
};

//币币交易-订单详情
const tradeDetail = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!get.action`,
        method: METHODS.GET,
        params
    })
};


//钱包账户资产（币对）
const getPairs = (params) => {
    return request({
        url: `${API_PREFIX}/wallet/getPairs.action`,
        method: METHODS.GET,
        params
    })
};

const apiList = {
    tradeBuyToken, tradeSellToken, tradeBuy, tradeSell, tradeRecord, tradeCancel, tradeDetail, getPairs
}

export default apiList
