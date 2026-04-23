import request from "@/service/request";
import { API_PREFIX } from '@/config'
import { signatureGenerate } from "@/utils/signatureUtil"
import { compress } from 'image-conversion';
import axios from "axios";
import { useUserStore } from "@/store/user.js";
const userStore = useUserStore()
const TOKEN = userStore.userInfo.token

// 申请认证
export const _applyIdentify = data => {
    return request({
        url: `${API_PREFIX}/kyc!apply.action`,
        method: 'GET',
        loading: true,
        duration: 0,
        params: {
            nationality: data.countryName, // 国籍
            idname: data.idname || 'id/passpost', // 证件名称
            idnumber: data.idnumber, // 证件号码
            name: data.name, // 姓名
            idimg_1: data.frontFile.length && data.frontFile[0].resURL || '',
            idimg_2: data.reverseFile.length && data.reverseFile[0].resURL || '',
            idimg_3: data.fileList.length && data.fileList[0].resURL || ''
        }
    })
}

// 认证信息
export const _getIdentify = () => {
    return request({
        url: `${API_PREFIX}/kyc!get.action`,
        method: 'GET'
    })
}

// =================   闪兑 ================


export const _getWallet = () => {
    return request({
        url: `${API_PREFIX}/wallet!get.action`,
        loading: false,
        method: 'GET'
    })
}

//闪兑
export const _getAllWallet = (params) => {
    return request({
        url: `${API_PREFIX}/wallet/getAll.action`,
        loading: false,
        method: 'GET',
        params
    })
}


// 初始化接口
export const _initExchange = () => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!view.action`,
        method: 'GET'
    })
}

// 闪兑
export const _exchange = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!buy_and_sell.action`,
        method: 'GET',
        loading: true,
        params: {
            volume: params.volume, // 数量
            symbol: params.symbol.toLowerCase(), // 闪兑币
            symbol_to: params.symbol_to.toLowerCase(), // 闪兑后的币
            session_token: params.session_token //
        }
    })
}

// 汇率
export const _exchangeRage = (params) => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!buy_and_sell_fee.action`,
        method: 'GET',
        params: {
            symbol: params.symbol, // 闪兑币
            symbol_to: params.symbol_to, // 闪兑后的币
            volume: params.volume || 1 // 闪兑数量
        }
    })
}

// 币币历史
export const _exchangeHistory = page_no => {
    return request({
        url: `${API_PREFIX}/exchangeapplyorder!list.action`,
        method: 'GET',
        params: {
            page_no
        }
    })
}

// 闪兑记录
export const _exchangeapplyorde = () => {
  return request({
      url: `${API_PREFIX}/exchangeapplyorder!queryExchangeList`,
      method: 'GET',
  })
}

// 获取质押余额
export const _getBalance = () => {
    return request({
        url: `${API_PREFIX}/dapp!getbalance.action`,
        method: "GET"
    })
}

// 提现初始化参数
export const _initWidthdrawl = (eth) => {
    return request({
        url: `${API_PREFIX}/dapp!exchange_fee.action`,
        method: "GET",
        params: {
            eth
        }
    })
}

// 提现申请
export const _widthdrawl = (eth) => {
    return request({
        url: `${API_PREFIX}/dapp!exchange.action`,
        method: "GET",
        loading: true,
        params: {
            eth
        }
    })
}

// 交易记录
export const _records = (action = 'exchange', page_no = 1) => { // transfer
    return request({
        url: `${API_PREFIX}/dapp!logs.action`,
        method: "GET",
        loading: false,
        params: {
            action,
            page_no
        }
    })
}


// 充值详情
export const _rechargeDetail = (order_no) => {
    return request({
        url: `${API_PREFIX}/rechargeBlockchain/get`,
        method: "get",
        params: {
            order_no
        }
    })
}

// 提现详情
export const _withdrawDetail = (order_no) => {
    return request({
        url: `${API_PREFIX}/withdraw/get`,
        method: "get",
        params: {
            order_no
        }
    })
}

// 帐变记录
export const _fundRecord = (category = 'exchange', page_no = 1, contentType = '') => {
    return request({
        url: `${API_PREFIX}/moneylog!list.action`,
        method: "get",
        params: {
            category,
            page_no,
            contentType
        }
    })
}


//助力贷配置信息
export const _getLoan = ()=>{
    return request({
        url: `${API_PREFIX}/loan!getLoanParamList.action`,
        method: "GET"
    })
    
}
//助力贷订单列表
export const _loanOrderList = () =>{
    return request({
        url: `${API_PREFIX}/loan!getOrders.action`,
        method: "GET"
    })
}
//贷款提交
export const _loanApply = (params)=>{
    return request({
        url: `${API_PREFIX}/loan!apply.action`,
        method: "GET",
        params
    })
}

// 助力贷订单详情
export const _loanOrderDetail = (orderNo) => {
    return request({
        url: `${API_PREFIX}/loan!getOrderDetail.action`,
        method: "get",
        params: {
            orderNo
        }
    })
}
