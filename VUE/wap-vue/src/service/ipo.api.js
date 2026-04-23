import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'

//新股认购列表
export const newSharesList = (params) => {
    return request({
        url: `${API_PREFIX}/newSharesConfig/list`,
        method: METHODS.POST,
        params
    })
};

//新股认购详情
export const getNewSharesDesc = (params) => {
  return request({
      url: `${API_PREFIX}/newSharesConfig/getDesc`,
      method: METHODS.POST,
      params
  })
};

//招股书列表
export const getProspectus = (params) => {
  return request({
      url: `${API_PREFIX}/newSharesConfig/getProspectus`,
      method: METHODS.POST,
      params
  })
};

// 上市未上市列表
export const listListingAndlisted = (params) => {
  return request({
      url: `${API_PREFIX}/newSharesConfig/listListingAndlisted`,
      method: METHODS.POST,
      params
  })
};

// 已上市列表（新）
export const newIssueList = (params) => {
  return request({
      url: `${API_PREFIX}/newSharesConfig/newIssueList`,
      method: METHODS.GET,
      params
  })
};

//一键抽签
export const drawLotsApply = (params) => {
  return request({
      url: `${API_PREFIX}/sNewSharesOrder/apply`,
      method: METHODS.POST,
      params
  })
};

//现股库存Top数据
export const getNowTopData = (params) => {
  return request({
      url: `${API_PREFIX}/spotStock/getTopData`,
      method: METHODS.POST,
      params
  })
};

// //新股库存
// export const newSpotStockList = (params) => {
//   return request({
//       url: `${API_PREFIX}/spotStock/list`,
//       method: METHODS.POST,
//       params
//   })
// };

// //现股库存
// export const nowSpotStockList = (params) => {
//   return request({
//       url: `${API_PREFIX}/spotStock/listCurrentShares`,
//       method: METHODS.POST,
//       params
//   })
// };

//抽签记录 现股库存 新股库存
export const sNewSharesOrderList = (params) => {
  return request({
      url: `${API_PREFIX}/sNewSharesOrder/list`,
      method: METHODS.POST,
      params
  })
};

//递交招股书详情
export const itemSummary = (params) => {
  return request({
      url: `${API_PREFIX}/item/itemSummary/get`,
      method: METHODS.GET,
      params
  })
};
// 认缴记录
export const userPromiseList = (params) => {
  return request({
      url: `${API_PREFIX}/userPromise/list`,
      method: METHODS.POST,
      params
  })
};

// 一键认缴
export const applyPromise = (params) => {
  return request({
      url: `${API_PREFIX}/userPromise/applyPromise`,
      method: METHODS.POST,
      params
  })
};
//认缴记录顶部数据
export const getSubscribeTop = (params) => {
  return request({
      url: `${API_PREFIX}/userPromise/getTop`,
      method: METHODS.POST,
      params
  })
};

//卖出
export const spotStockSell = (params) => {
  return request({
      url: `${API_PREFIX}/spotStock/sell`,
      method: METHODS.POST,
      params
  })
};