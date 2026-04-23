import request from './request'
import {
    METHODS
} from '@/config'
import {
    API_PREFIX
} from '@/config'

//获取 支付方式配置 列表
const ctcPaymentMethodConfig = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethodConfig!list.action`,
        method: "get",
        isLoading: false,
        params
    })
};
//CTC支付方式

//获取 支付方式类型 列表
export const ctcPaymentMethodType = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!method_type.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 支付方式 列表
const ctcPaymentMethodList = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!list.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 支付方式 详情
const ctcPaymentMethodDetail = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!get.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 承兑商广告 支付方式列表
const ctcPaymentMethodPayList = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!getAdPayments.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 用户支付方式列表（需匹配承兑商广告支付方式）
const ctcPaymentMethodUserPay = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!getUserPaymentsByAd.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//新增 支付方式
const ctcPaymentMethodAddPay = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!add.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//修改 支付方式
const ctcPaymentMethodUpdate = (params) => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethod!update.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//CTC承兑商

//获取 承兑商 详情
const ctcUserGet = (params) => {
    return request({
        url: `${API_PREFIX}/c2cUser!get.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 CTC用户中心
const ctcUserGetUserCenter = (params) => {
    return request({
        url: `${API_PREFIX}/c2cUser!getUserCenter.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//设置 承兑商 信息
const ctcUserSet = (params) => {
    return request({
        url: `${API_PREFIX}/c2cUser!set.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//CTC 广告


//发布广告
const ctcAdvertAdd = (params) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!add.action`,
        method: "get",
        isLoading: false,
        params
    })
};


//已关闭广告重新上架
const ctcAdvertAddClosed = (params) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!add_closed.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//关闭广告
const ctcAdvertClose = (params) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!close.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 上架币种 列表
const ctcAdvertList = (params) => {
    params.symbol = params.symbol.toLocaleLowerCase() // 转换成小写
    return request({
        url: `${API_PREFIX}/c2cAdvert!list.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 承兑商广告 列表
const ctcAdvertCtcUserList = (params) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!list_ctc_user.action`,
        method: "get",
        isLoading: false,
        params
    })
};



//获取 广告 详情
const ctcAdvertGetDetail = (params) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!get.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//CTC订单

//获取 用户订单 列表
export const ctcOrderList = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!list.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 承兑商订单 列表
const ctcOrderListCtcUser = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!list_ctc_user.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//获取 订单 详情
const ctcOrderGetDetail = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!get.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//自选区下单：购买、出售
const ctcOrderOpen = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!open.action`,
        method: "get",
        isLoading: false,
        params
    })
};

//快捷区下单：购买、出售
const ctcOrderOpenQuick = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!open_quick_apply.action`,
        method: "get",
        isLoading: false,
        params
    })
};


//取消订单
const ctcOrderCancel = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!order_cancel.action`,
        method: "get",
        isLoading: false,
        params
    })
};


// 获取新增或者修改广告页面需要的session_token
export const getCtcAdvertToken = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!advert_open.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 发布广告
export const placeAd = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!add.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 已关闭广告重新上架
export const rePlaceAd = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!add_closed.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 关闭广告
export const closeAd = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!close.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 获取 支付币种（法币）列表
export const c2cGetPayCurrencyList = () => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!currency.action`,
        method: "get",
        loading: false,
    });
};


// 获取上架币种列表
export const c2cGetCurrencyList = () => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!symbol.action`,
        method: "POST",
        loading: false,
    });
};

// 获取承兑商（我的）广告列表
export const getMyadvertList = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!list_c2c_user.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 获取承兑商（我的）广告历史列表
export const getMyadvertHistoryList = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!list_history.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 获取广告详情
export const getAdDetail = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!get.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};


// 获取承兑商订单列表
export const getMerchantOrdersList = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!list_c2c_user.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 计算广告参数
export const countAdParams = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!getComputeValue.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 获取广告支付时效列表
export const getTimeList = () => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!expire_time.action`,
        method: "POST",
        loading: false,
    });
};


// 修改广告
export const editAd = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAdvert!update.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    });
};

export const getSessionToken = params => {
    return request({
        url: `${API_PREFIX}/c2cOrder/orderOpen`,
        method: "post",
        params
    })
}

// 获取 C2C帮助中心 列表
export const getC2cHelpCenter = params => {
    return request({
        url: `${API_PREFIX}/cms!list.action`,
        method: "get",
        params
    })
}

// 获取 C2C帮助中心 详情
export const getC2cHelpCenterDetail = params => {
    return request({
        url: `${API_PREFIX}/cms!get.action`,
        method: "get",
        params
    })
}

// 发送消息
export const otcOnlinechat = params => {
    return request({
        url: `${API_PREFIX}/otcOnlinechat!send.action`,
        method: "get",
        loading: true,
        params
    })
}

// 获取聊天历史
export const otcOnlinechatList = params => {
    return request({
        url: `${API_PREFIX}/otcOnlinechat!list.action`,
        method: "get",
        params
    })
}

const paymentMethodConfigDetail = params => {
    return request({
        url: `${API_PREFIX}/c2cPaymentMethodConfig!get.action`,
        method: "get",
        params
    })
}

// 获取用户实名
const getUserName = params => {
    return request({
        url: `${API_PREFIX}/kyc!get.action`,
        method: "get",
        params
    })
}


// c2c申诉
const c2cAppeal = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cAppeal!apply.action`,
        method: "POST",
        loading: false,
        params: {
            ...obj
        }
    }
    );
};

// 快捷区下单：获取最优价格的广告
const c2cgetBestPrice = (obj) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!get_best_price_advert.action`,
        method: "POST",
        loading: true,
        params: {
            ...obj
        }
    }
    );
};

const getorder_open = (params) => {
    return request({
        url: `${API_PREFIX}/c2cOrder!order_open.action`,
        method: "post",
        params

    })
};
const otcApi = {
    ctcPaymentMethodConfig,
    ctcPaymentMethodType, ctcPaymentMethodList, ctcPaymentMethodDetail, ctcPaymentMethodPayList, ctcPaymentMethodUserPay, ctcPaymentMethodAddPay, ctcPaymentMethodUpdate,
    ctcUserGet, ctcUserGetUserCenter, ctcUserSet,
    ctcAdvertAdd, ctcAdvertAddClosed, ctcAdvertClose, ctcAdvertList, ctcAdvertCtcUserList, ctcAdvertGetDetail,
    ctcOrderList, ctcOrderListCtcUser, ctcOrderGetDetail, ctcOrderOpen, ctcOrderOpenQuick, ctcOrderCancel, getSessionToken, getC2cHelpCenter,
    getC2cHelpCenterDetail,
    otcOnlinechat, otcOnlinechatList,
    paymentMethodConfigDetail, getUserName,
    c2cGetPayCurrencyList,
    c2cAppeal,
    c2cgetBestPrice,
    getorder_open
}

export default otcApi
