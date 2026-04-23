// 主页接口
import request from "@/service/request";
import { API_PREFIX } from '@/config'
// 获取币种
export const _getCoins = (params) => {
    return request({
        url: `${API_PREFIX}/item!list.action`,
        method: "GET",
        params
    })
};

// 获取行情
export const _getHomeList = (symbol = 'btc') => {
    return request({
        url: `${API_PREFIX}/hobi!getRealtime.action`,
        method: "GET",
        params: {
            symbol
        }
    })
};


// 汇率
export const _getExchangeRate = (params) => {
    return request({
        url: `${API_PREFIX}/exchangerateuserconfig!get.action`,
        method: "GET",
        params
    })
};


// 查询是否加入自选
export const _checkIsInCollect = symbol => {
    return request({
        url: `${API_PREFIX}/itemUserOptional!getItemOptionalStatus.action`,
        method: "GET",
        params: {
            symbol
        }
    })
}

// 自选
export const _collect = (symbol) => {
    return request({
        url: `${API_PREFIX}/itemUserOptional!add.action`,
        method: "GET",
        params: {
            symbol
        }
    })
}

// 删除自选
export const _deleteCollect = symbol => {
    return request({
        url: `${API_PREFIX}/itemUserOptional!delete.action`,
        method: "GET",
        params: {
            symbol
        }
    })
}

// 我的自选
export const _myCoins = () => {
    return request({
        url: `${API_PREFIX}/itemUserOptional!list.action`,
        method: "GET",

    })
}

// 获取时区
export const _timeConvert = () => {
    return request({
        url: `${API_PREFIX}/timezone/info`,
        method: "GET",
    })
}