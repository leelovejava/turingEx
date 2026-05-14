import request from './request'
import { API_PREFIX, METHODS } from '@/config'

export const _noticeList = (params) =>
  request({ url: `${API_PREFIX}/userNotice/list`, method: METHODS.GET, params })

export const _noticeUnreadCount = () =>
  request({ url: `${API_PREFIX}/userNotice/unreadCount`, method: METHODS.GET })

export const _noticeReadAll = () =>
  request({ url: `${API_PREFIX}/userNotice/readAll`, method: METHODS.POST })

export const _noticeRead = (id) =>
  request({ url: `${API_PREFIX}/userNotice/read/${id}`, method: METHODS.POST })
