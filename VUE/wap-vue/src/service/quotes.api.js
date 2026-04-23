import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'

/**
 * 品种查询
 * @returns 
 */
export const _getCoinList = () => {
  return request({
    url: `${API_PREFIX}/item!list.action`,
    method: METHODS.GET,
    params: {
      type: 'forex'
    }
  })
}

/**
 * 品种查询
 * @returns 
 */
export const _getCoinList2 = () => {
  return request({
    url: `${API_PREFIX}/item!list.action`,
    method: METHODS.GET,
    params: {
      type: 'commodities'
    }
  })
}

/**
 * 获取行情
 * @returns 
 */
export const _getQuotes = (list = []) => {
  const arr = []
  list.map(item => {
    arr.push(item.symbol)
  })
  return request({
    url: `${API_PREFIX}/hobi!getRealtime.action`,
    method: 'GET',
    params: {
      symbol: arr.join(',')
    }
  })
}

// quotation页用
export const _getRealtimeByType = (params) => {
  return request({
    url: `${API_PREFIX}/publicRealtimeByType`,
    method: METHODS.GET,
    params
  })
}
//热门
export const _publicRealtimeTop = (params) => {
  return request({
    url: `${API_PREFIX}/publicRealtimeTop`,
    method: METHODS.GET,
    params
  })
}

// 获取服务器时间
export const _getTime = (params) => {
  return request({
    url: `${API_PREFIX}/assets!getTime.action`,
    method: METHODS.POST,
    data: params
  })
}

// 获取服务器时区
export const _getTimezone = () => {
  return request({
    url: `${API_PREFIX}/timezone/info`,
    method: METHODS.GET,
  })
}

/**
 * 获取自选列表
 * @returns 
 */
export const _itemUserOptionalList = () => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/list`,
    method: METHODS.GET,
  })
}

/**
* 创建加入自选列表
* @returns 
*/
export const _itemUserOptionalListAdd = (params) => {
  return request({
    url: `${API_PREFIX}/itemUserOptionalList!add.action`,
    method: METHODS.POST,
    data: params
  })
}

/**
* 编辑自选列表
* @returns 
*/
export const _itemUserOptionalListUpdate = (params) => {
  return request({
    url: `${API_PREFIX}/itemUserOptionalList!update.action`,
    method: METHODS.POST,
    data: params
  })
}

/**
* 删除自选列表
* @returns 
*/
export const _itemUserOptionalListDelete = (params) => {
  return request({
    url: `${API_PREFIX}/itemUserOptionalList!delete.action`,
    method: METHODS.POST,
    data: params
  })
}


/**
* 添加币种到自选列表
* @returns 
*/
export const _ItemUserOptionalItemAdd = (params) => {
  return request({
    url: `${API_PREFIX}/ItemUserOptionalItem!add.action`,
    method: METHODS.POST,
    data: params
  })
}


/**
* 删除自选列表中币种
* @returns 
*/
export const _ItemUserOptionalItemDelete = (params) => {
  return request({
    url: `${API_PREFIX}/ItemUserOptionalItem!delete.action`,
    method: METHODS.POST,
    data: params
  })
}
/**
* 新增自选分组
* @returns 
*/
export const _itemUserOptionaAddList = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/save`,
    method: METHODS.POST,
    data: params
  })
}
/**
* 获取新增自选组时候的币种列表
* @returns 
*/
export const _listExchanges = () => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/listExchanges`,
    method: METHODS.GET,
  })
}
/**
* 删除分组
* @returns 
*/
export const _itemUserOptionalDelete = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/delete`,
    method: METHODS.GET,
    params
  })
}
/**
* 编辑分组
* @returns 
*/
export const _itemUserOptionalUpdate = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/update`,
    method: METHODS.POST,
    data: params
  })
}
/**
* 是否存在币种
* @returns 
*/
export const _isItemHasAdd = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/isItemHasAdd`,
    method: METHODS.GET,
    params
  })
}

/**
* 币对添加到分组
* @returns 
*/
export const _itemUserOptionaSaveItem = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/saveItem`,
    method: METHODS.GET,
    params
  })
}
/**
* 根据Id获取自选分组数据
* @returns 
*/
export const _listItemsById = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/listItemsById`,
    method: METHODS.GET,
    params
  })
}
/**
* 根据TYPE获取自选分组数据
* @returns 
*/
export const _listItemsByType = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/listItemsByType`,
    method: METHODS.GET,
    params
  })
}
/**
* 判断币对是否已经被全局加入某个分组
* @returns 
*/
export const _isItemHasAddGlobal = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/isItemHasAddGlobal`,
    method: METHODS.GET,
    params
  })
}
/**
* 判断币对是否已经被全局加入某个分组
* @returns 
*/
export const _removeItem = (params) => {
  return request({
    url: `${API_PREFIX}/item/itemUserOptionalList/removeItem`,
    method: METHODS.GET,
    params
  })
}
/**
 * 获取实时价格
 *
 */

export const _getRealtime = (symbol = 'btc') => {
  return request({
    url: `${API_PREFIX}/hobi!getRealtime.action`,
    method: 'GET',
    params: {
      symbol
    }
  })
}