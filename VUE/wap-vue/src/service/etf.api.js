// 接口文档地址：https://enjdhdg.site/api/doc.html#/%E5%A4%96%E6%B1%87%E7%AE%A1%E7%90%86%E5%B9%B3%E5%8F%B0/h5%E7%AE%80%E5%86%B5/queryByIdUsingGET_13
import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'

/**
 * 根据symbol获取简况数据
 * @returns {*}
 * @private
 */
export const _getItemSummary = (symbol) => {
  return request({
    url: `${API_PREFIX}/item/itemSummary/get`,
    method: METHODS.GET,
    params: {
      symbol
    }
  })
}

// 获取etf 简况f10
export const _getETFItemList = (params) => {
  return request({
    url: `${API_PREFIX}/item!list.action`,
    method: 'GET',
    params
  })
}

// 获取etf详情
export const _getConstituentStockList = (symbol) => {
  return request({
    url: `${API_PREFIX}/projectBreed/getConstituentStockList`,
    method: 'GET',
    params: {
      symbol
    }
  })
}

/**
 * 美股获取板块指数成分股
 * @private
 */
export const _getRelateStocks = (symbol) => {
  return request({
    url: `${API_PREFIX}/item!relateStocks.action`,
    method: METHODS.GET,
    params: {
      symbol
    }
  })
}

/**
 * etf查币对详情
 * @private
 */
export const _queryBySymbol = (symbol) => {
  return request({
    url: `${API_PREFIX}/item!queryBySymbol.action`,
    method: METHODS.GET,
    params: {
      symbol
    }
  })
}

/**
 * etf查持仓列表
 * @private
 */
export const _getETFList = (symbolType) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!getETFList.action`,
    method: METHODS.GET,
    params: {
      symbolType
    }
  })
}

/**
 * etf查币仓位
 * @private
 */
export const _getETFSum = (symbolType) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!getETFSum.action`,
    method: METHODS.GET,
    params: {
      symbolType
    }
  })
}

// 获取订单列表
export const _contractApplyOrder = (params) => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!list.action`,
    method: METHODS.POST,
    data: params
  })
}

export const _getAllAssets = () => {
  return request({
    url: `${API_PREFIX}/assets!getAll.action`,
    method: 'GET'
  })
}

// 获取交易成交列表
export const getExchangeApplyHisList = (symbolType) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!list.action`,
    method: METHODS.POST,
    data: {
      page_no: 1,
      type: 'hisorders',
      symbolType,
      isAll: true
    }
  })
}

// 获取交易委托列表
export const getExchangeApplyOrderList = (symbolType) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!list.action`,
    method: METHODS.POST,
    data: {
      page_no: 1,
      type: 'orders',
      symbolType,
      isAll: true
    }
  })
}

// 获取交易成交列表
export const _getExchangeCanceledHisList = (symbolType) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!list.action`,
    method: METHODS.POST,
    data: {
      page_no: 1,
      type: 'canceled',
      symbolType,
      isAll: true
    }
  })
}

// 获取交易自选
export const _getOptionalList = (type) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/listItemsByType`,
    method: METHODS.GET,
    params: {
      type
    }
  })
}


// 获取资讯
export const _getInformationList = (maxTime) => {
  return request({
    url: `${API_PREFIX}/information!list.action`,
    method: METHODS.GET,
    params: {
      limit: 50,
      maxTime
    }
  })
}

// 获取美股交易记录
export const _getStockTradeList = (symbol) => {
  return request({
    url: `${API_PREFIX}/hobi!getStockTradeList.action`,
    method: METHODS.GET,
    params: {
      symbol
    }
  })
}

/**
 * etf热力图
 * @private
 */
export const _getHotMapList = () => {
  return request({
    url: `${API_PREFIX}/tip/getRandom`,
    method: METHODS.GET
  })
}

/**
 * 美股热力图
 * @private
 */
export const _getUsStocksHotMapList = () => {
  return request({
    url: `${API_PREFIX}/tip/getRandomByType?type=US-stocks`,
    method: METHODS.GET
  })
}

// 获取etf 简况f10
export const _getHkStocksItemList = (params) => {
  return request({
    url: `${API_PREFIX}/item!queryByScene`,
    method: 'GET',
    params
  })
}
