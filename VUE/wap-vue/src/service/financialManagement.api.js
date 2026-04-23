
import request from './request';
import { API_PREFIX } from '@/config';
// 获取理财产品（理财详情）
export const getFMProducts = (txnhash, status = true) => {
    return request({
        url: `${API_PREFIX}/dapp!approve_addmsg.action"`,
        method: "GET",
        loading: true,
        params: {
            txnhash,
            status
        }
    }
    );
};

/**
 * 理财产品列表
 * @returns
 */
export const _finList = () => {
    return request({
        url: `${API_PREFIX}/finance!list.action`,
        method: 'GET'
    })
}


// 获取矿机产品列表
export const getMachineList = () => {
    return request({
        url: `${API_PREFIX}/miner!list.action`,
        method: "POST",
        loading: false,
    }
    );
};

// 获取已购矿机列表
export const getMachineBought = (obj) => {
    return request({
        url: `${API_PREFIX}/minerOrder!list.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 获取矿机订单详情
export const getMinerorder = (obj) => {
    return request({
        url: `${API_PREFIX}/minerOrder!get.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 赎回理财产品
export const ransomMachineProduct = (params) => {
    return request({
        url: `${API_PREFIX}/minerOrder!closOrder.action`,
        method: "POST",
        loading: true,
        params: {
            ...params
        },
    }
    );
};

//矿机收益统计
export const getMiningRevenueStatisticsList = (token) => {
    return request({
        url: `${API_PREFIX}/minerOrder!listSum.action`,
        method: "POST",
        loading: false,
        params: {
            token,
        }
    }
    );
};

// 矿机下单认购
export const machineMakeOrder = (params) => {
    console.log('obj', params)
    return request({
        url: `${API_PREFIX}/minerOrder!getOpen.action`,
        method: "POST",
        params: {
            ...params
        },
        loading: false
    })
};

// 确认矿机认购
export const confirmMichineOrder = (params) => {
    return request({
        url: `${API_PREFIX}/minerOrder!open.action`,
        method: "POST",
        params: {
            ...params
        },
        loading: true
    })
};

// 获取已购理财列表
export const getfinacialProductsBought = (obj) => {
    return request({
        url: `${API_PREFIX}/financeOrder!list.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 获取理财订单详情
export const getFinanceOrder = (obj) => {
    return request({
        url: `${API_PREFIX}/financeOrder!get.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 赎回理财产品
export const ransomFinacialProduct = (params) => {
    return request({
        url: `${API_PREFIX}/financeOrder!closOrder.action`,
        method: "POST",
        loading: true,
        data:params
    }
    );
};

// 获取理财产品列表
export const getfinacialProductsList = () => {
    return request({
        url: `${API_PREFIX}/finance!list.action`,
        method: "POST",
        loading: false,
    }
    );
};

//理财收益统计
export const financeStatics = () => {
    return request({
        url: `${API_PREFIX}/financeOrder!listSum.action`,
        method: "POST",
        loading: false
    }
    );
};

// 理财认购
// export const fundMakeOrder = (params) => {
//     console.log('obj', params)
//     return httpMultipart({
//         url: "/api/financeOrder!getOpen.action?" + qs.stringify(params),
//         method: "post",
//         loading: true
//     })
// };

// 理财认购
export const fundMakeOrder = (params) => {
    console.log('obj', params)
    return request({
        url: `${API_PREFIX}/financeOrder!getOpen.action`,
        method: "POST",
        params: {
            ...params
        },
        loading: false
    })
};

// 确认理财认购认购
export const confirmFundOrder = (params) => {
    return request({
        url: `${API_PREFIX}/financeOrder!open.action`,
        method: "POST",
        params: {
            ...params
        },
        loading: true
    })
};

// 手机号登录
export const _getProfitList = () => {
    return request({
        url: `${API_PREFIX}/dapp!get_notice_logs.action`,
        method: "POST",
        isLoading: false
    })
}
