import request from './request';
import { METHODS } from '@/config';
import { API_PREFIX } from '@/config';
/**
 * 获取自选列表
 * @returns 
 */
export const _itemUserOptionalList = () => {
  return request({
    url: `${API_PREFIX}/itemUserOptionalList!list.action`,
    method: METHODS.POST,
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