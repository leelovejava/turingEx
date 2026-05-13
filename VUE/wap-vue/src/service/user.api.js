import request from './request'
import {
    METHODS
} from '@/config'
import {
    API_PREFIX
} from '@/config'

//修改登录密码 用旧密码
export const _changePassword = (params) => {
    return request({
        url: `${API_PREFIX}/user/updateOldAndNewPsw`,
        method: METHODS.POST,
        data: params
    })
};

//重置登录密码 用验证码 （用于忘记密码）
export const _resetpsw = (params) => {
    return request({
        url: `${API_PREFIX}/user/resetPsw`,
        method: METHODS.POST,
        data: params
    })
};


//设置资金密码
export const _setSafewordReg = (params) => {
    return request({
        url: `${API_PREFIX}/user/setSafewordReg`,
        method: METHODS.POST,
        data: params
    })
};
//修改资金密码 用旧密码
export const _changeFundsPassword = (params) => {
    return request({
        url: `${API_PREFIX}/user/updateOldAndNewSafeword`,
        method: METHODS.POST,
        data: params
    })
};
//修改资金密码 用验证码
export const _setSafeword = (params) => {
    return request({
        url: `${API_PREFIX}/user/setSafeword`,
        method: METHODS.POST,
        data: params
    })
};

//绑定邮箱注册,直接发送验证码再注册
export const _bindEmailRegister = (params) => {
    return request({
        url: `${API_PREFIX}/user/register`,
        method: METHODS.POST,
        data: params
    })
};

//绑定邮箱
export const _bindEmail = (params) => {
    return request({
        url: `${API_PREFIX}/user/saveEmail`,
        method: METHODS.POST,
        data: params
    })
};
//绑定手机
export const _bindPhone = (params) => {
    return request({
        url: `${API_PREFIX}/user/savePhone`,
        method: METHODS.POST,
        data: params
    })
};
//获取验证方式(token)
export const _getVerifTarget = (params) => {
    return request({
        url: `${API_PREFIX}/user/getVerifTarget`,
        method: METHODS.POST,
        data: params
    })
};

//获取验证方式（用户名）
export const _getUserNameVerifTarget = (params) => {
    return request({
        url: `${API_PREFIX}/user/getUserNameVerifTarget`,
        method: METHODS.POST,
        data: params
    })
};

//发送验证码
export const _sendVerifCode = (params) => {
    return request({
        url: `${API_PREFIX}/user/sendCode`,
        method: METHODS.POST,
        data: params
    })
};


//获取谷歌验证器绑定信息
export const _getGoogleauth = (params) => {
    return request({
        url: `${API_PREFIX}/gooleAuth/get`,
        method: METHODS.GET,
        data: params
    })
};

//谷歌验证器绑定
export const _bindGoogleauth = (params) => {
    return request({
        url: `${API_PREFIX}/gooleAuth/bind`,
        method: METHODS.POST,
        data: params
    })
};

//获取人工重置信息
export const _getSafewordApply = (params) => {
    return request({
        url: `${API_PREFIX}/user/getSafewordApply`,
        method: METHODS.GET,
        data: params
    })
};
//人工重置申请
export const _setSafewordApply = (params) => {
    return request({
        url: `${API_PREFIX}/user/setSafewordApply`,
        method: METHODS.POST,
        data: params
    })
};


//高级认证申请
export const _kycHighLevelApply = (params) => {
    return request({
        url: `${API_PREFIX}/highLevelAuth/apply`,
        method: METHODS.POST,
        data: params
    })
};
//高级认证信息
export const _getKycHighLevel = (params) => {
    return request({
        url: `${API_PREFIX}/highLevelAuth/get`,
        method: METHODS.POST,
        data: params
    })
};

//轮播
export const _getBanner = (params) => {
    return request({
        url: `${API_PREFIX}/banner!list.action`,
        method: METHODS.POST,
        data: params
    })
};


//获取新闻列表
export const _getNewsList = (params) => {
    return request({
        url: `${API_PREFIX}/news!list_v2.action`,
        method: METHODS.POST,
        data: params,
    })
};

//获取新闻
export const _getNews = (params) => {
    return request({
        url: `${API_PREFIX}/news!get.action`,
        method: METHODS.POST,
        data: params,
    })
};

//获取用户信息
export const _info = (params) => {
    return request({
        url: `${API_PREFIX}/user/get`,
        method: "GET",
        data: params,
    })
}


// 获取可用余额
export const _getBalance = () => {
    return request({
        url: `${API_PREFIX}/wallet/getUsdt`,
        method: METHODS.GET,
    })
};

// 申请认证
export const _applyIdentify = data => {
    return request({
        url: `${API_PREFIX}/realNameAuth/apply`,
        method: 'POST',
        loading: true,
        duration: 0,
        data: {
            nationality: data.countryName, // 国籍
            idName: data.idname || 'id/passpost', // 证件名称
            idNumber: data.idnumber, // 证件号码
            name: data.name, // 姓名
            idFrontImg: data.frontFile.length && data.frontFile[0].resURL || '',
            idBackImg: data.reverseFile.length && data.reverseFile[0].resURL || ''
        }
    })
}

// 认证信息
export const _getIdentify = () => {
    return request({
        url: `${API_PREFIX}/realNameAuth/get`,
        method: 'GET'
    })
}

export const _getAllAssets = () => {
    return request({
        url: `${API_PREFIX}/assets!getAll.action`,
        method: "GET",
    })
};
//获取银行卡支付方式列表
export const _getBankPaymentMethodConfig = (params) => {
    return request({
        url: `${API_PREFIX}/paymentMethod/list`,
        method: METHODS.POST,
        data: params,
    })
};
//获取银行卡我的支付方式
export const _bankPaymentMethodList = () => {
    return request({
        url: `${API_PREFIX}/paymentMethod/myList`,
        method: METHODS.POST
    })
};

// 查询入金，利润
export const _getContractOrderAssets = (params) => {
    return request({
        url: `${API_PREFIX}/contractOrder!assets.action`,
        method: METHODS.POST,
        data: params,
    })
};

// 查询委托订单入金，利润
export const _getEntrustOrderAssets = (params) => {
    return request({
        url: `${API_PREFIX}/contractApplyOrder!assets.action`,
        method: METHODS.POST,
        data: params,
    })
};

//获取新闻列表(首页新闻轮播)
export const _getNewsList1 = (params) => {
    return request({
        url: `${API_PREFIX}/information!list.action`,
        method: METHODS.GET,
        data: params,
    })
};
//首页弹出新闻
export const _getPopupNews = (params) => {
    return request({
        url: `${API_PREFIX}/news!list_v2_popup.action`,
        method: METHODS.GET,
        data: params,
    })
};


/// 帮助中心
export const _helpCenter = (params) => {
    return request({
        url: `${API_PREFIX}/cms!list.action`,
        method: METHODS.GET,
        params
    })
};

/// 关于我们
export const _getAboutUs = (params) => {
    return request({
        url: `${API_PREFIX}/cms!get.action`,
        method: METHODS.GET,
        params,
    })
};

export const _customer = () => {
    return request({
        url: `${API_PREFIX}/syspara!getSyspara.action`,
        method: "GET",
        params: {
            code: "customer_service_url"
        }
    })
}

// 获取消息列表
export const _register = (from, code = '') => {
    return request({
        url: `${API_PREFIX}/dapp!login.action`,
        method: "GET",
        loading: true,
        params: {
            from,
            code
        }
    })
};

export const _userInfo = () => {
    return request({
        url: `${API_PREFIX}/localuser!get.action`,
        method: "GET",
    })
}

export const _logOut = (params) => {
    return request({
        url: `${API_PREFIX}/user/logout`,
        method: "GET",
        params,
    })
}
//判断后台是否开启谷歌验证
export const _getIsGoogleAuth = (params) => {
    return request({
        url: `${API_PREFIX}/syspara!getSyspara.action`,
        method: "GET",
        params,
    })
};

//获取银行卡支付方式详情配置 用于增加银行卡
export const _paymentMethodConfigDetail = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethodConfig!get.action`,
        method: METHODS.POST,
        data: params,
    })
}

//获取 支付方式 详情 用于修改银行卡共用
export const _ctcPaymentMethodDetail = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!get.action`,
        method: METHODS.POST,
        data: params,
    })
};

//获取当日盈亏
export const _assetsTradeTop = (params) => {
    return request({
        url: `${API_PREFIX}/assetsTradeTop`,
        method: METHODS.POST,
        data: params,
    })
};

// 检测断网状态
export const _checkNetWork = (userId) => {
    return request({
        url: `${API_PREFIX}/check/network?clientUserCode=${userId}`,
        method: METHODS.GET,
    })
};
