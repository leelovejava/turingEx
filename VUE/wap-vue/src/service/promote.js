
import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'



//我的推广
export const _promote = (params) => { // 续借
    return request({
        url: `${API_PREFIX}/promote!getPromote.action`,
        method: METHODS.GET,
        params
    })
};

//获取我的分享信息
export const _localuser = (params) => {
    return request({
        url: `${API_PREFIX}/user/getShare.action`,
        method: METHODS.GET,
        loading: true,
        params
    })
};

