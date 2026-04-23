
import request from './request'
import {
    METHODS
} from '@/config'
import {
    API_PREFIX
} from '@/config'

//根据币种获取链地址
const getBlock = (params) => {
    return request({
        url: `${API_PREFIX}/channelBlockchain!getBlockchainName.action`,
        method: "get",
        params
    })
};

//获取session_token
const getRechargeToken = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/rechargeOpen`,
        method: "get",
        isLoading: false,
        params
    })
};

//充值申请
const rechargeApply = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/recharge`,
        method: "get",
        params
    })
};


//充值详情
const getRechargeDetail = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain!get`,
        method: "get",
        isLoading: false,
        params
    })
};

//充值记录
const getRechargeList = (params) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/list`,
        method: "get",
        isLoading: false,
        params
    })
};


// 获取充值说明
const getRechargeTips = (params) => {
    return request({
        url: `${API_PREFIX}/cms!get.action`,
        method: "get",
        isLoading: false,
        params
    })
};


export default { getRechargeTips, getBlock, getRechargeToken, rechargeApply, getRechargeDetail, getRechargeList }
