import request from './request'
import { METHODS } from '@/config'
import { API_PREFIX } from '@/config'
// 网络请求demo 列子


export const _getCurrentProjectInfo = (params) => {
    return request({
        url: `${API_PREFIX}/projectInfoApp/getCurrentProjectInfo`,
        method: METHODS.POST,
        data: params
    })
};
//注册用户
///

export const _registerUser = (params) => {
    return request({
        url: `${API_PREFIX}/registerNoVerifcode`,
        method: METHODS.POST,
        data: params
    })
};
//登录
export const _loginUser = (params) => {
    return request({
        url: `${API_PREFIX}/login`,
        method: METHODS.POST,
        data: params
    })
};


/// 发送邮箱 手机验证码
export const _sendVerifyCode = (params) => {
    return request({
        url: `${API_PREFIX}/idcode/execute`,
        method: METHODS.POST,
        data: params
    })
};

//服务条款
export const _getCms = (params) => {
    return request({
        url: `${API_PREFIX}/cms!get.action`,
        method: METHODS.POST,
        data: params
    })
};