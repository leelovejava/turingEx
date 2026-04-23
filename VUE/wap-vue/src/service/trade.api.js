import request from './request';
import { METHODS } from '@/config';
import { API_PREFIX } from '@/config';

/**
 * 获取 支付币种（法币） 列表
 * @returns {*}
 * @private
 */
export const _getCurrencyList = () => {
  return request({
    url: `${API_PREFIX}/c2cOrder/currency`,
    method: METHODS.GET
  });
};

/**
 * 获取法币列表
 * @returns {*}
 * @private
 */
export const _getLegalTenderList = () => {
  return request({
    url: `${API_PREFIX}/c2cAdvert!currency.action`,
    method: METHODS.GET
  });
};

/**
 * 获取 支付方式 列表
 */

export const _getPaymentMethod = (params) => {
  return request({
    url: `${API_PREFIX}/paymentMethod/myList`,
    method: METHODS.POST,
    data: params
  });
};

/**
 * 添加 支付方式
 */

export const _getAddPaymentMethod = (params) => {
  return request({
    url: `${API_PREFIX}/paymentMethod/add`,
    method: METHODS.POST,
    data: params
  });
};

/**
 * 添加 支付方式
 */

export const _getUpdatePaymentMethod = (params) => {
  return request({
    url: `${API_PREFIX}/paymentMethod/update`,
    method: METHODS.POST,
    data: params
  });
};
/** =============================  永续合约委托 =======================*/

// 开仓初始化参数
export const _initOpen = (params) => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!openview.action`,
    method: METHODS.POST,
    data: params
  });
};

// 永续合约开仓
export const _orderOpen = (params) => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!open.action`,
    method: METHODS.POST,
    loading: true,
    data: params
  });
};

// 平仓初始化参数
export const _initClose = (params) => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!closeview.action`,
    method: METHODS.POST,
    data: params
  });
};

// 永续合约平仓
export const _orderClose = (params) => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!close.action`,
    method: 'GET',
    loading: true,
    params: {
      symbol: params.symbol,
      session_token: params.session_token,
      direction: params.direction || 'buy', // buy, sell
      amount: params.amount,  // 数量
      price_type: params.price_type || 'opponent', // limit - 限价，opponent - 市价
      price: params.price, // limit price
      lever_rate: params.lever_rate || 1 // 杠杆
    }
  })
}

// 获取订单列表
export const contractOrder = (params) => {
  return request({
    url: `${API_PREFIX}/contractOrder!list.action`,
    method: METHODS.POST,
    data: params
  });
};


// 获取委托订单列表2
export const _contractApplyOrderList = (params) => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!list.action`,
    method: METHODS.POST,
    data: params
  });
};

// 永续合约撤单
export const _contractApplyOrderCancel = (params) => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!cancel.action`,
    method: METHODS.POST,
    data: params
  });
};

// 永续合约一件撤单
export const _recallOrderBatch = () => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!cancelAll.action`,
    method: 'GET',
    loading: true
  })
}
// 平仓
export const _contractOrderClose = (params) => {
  return request({
    url: `${API_PREFIX}/contractOrder!close.action`,
    method: METHODS.POST,
    data: params
  });
};

// 永续合约持仓订单详情
export const _orderHoldDetail = (order_no) => {
  return request({
    url: `${API_PREFIX}/contractOrder!get.action`,
    loading: false,
    method: 'GET',
    params: {
      order_no
    }
  });
};

// 永续订单详情
export const _orderDetail = order_no => {
  return request({
    url: `${API_PREFIX}/contractApplyOrder!get.action`,
    method: 'GET',
    loading: false,
    params: {
      order_no
    }
  })
}

// 永续合约一键平仓
export const _orderSellBatch = () => {
  return request({
    url: `${API_PREFIX}/contractOrder!closeAll.action`,
    method: 'GET',
    loading: true
  })
}


export const _exchangeratelist = (params) => {
  return request({
    url: `${API_PREFIX}/exchangerate!list.action`,
    method: "get",
    // isLoading: true
  }, params)
};

export const _exchangerateuserconfig = (params) => {
  return request({
    url: `${API_PREFIX}/exchangerateuserconfig!userSetRate.action`,
    method: "get",
    params
  })
};

// 获取可用余额
export const _getBalance = (data) => {
  return request({
    url: `${API_PREFIX}/wallet/getUsdt`,
    method: "GET",
    params: {
      ...data
    }
    // isLoading: true
  })
};


//现货交易-买入
export const _tradeBuy = (params) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!open.action`,
    method: "get",
    params
  })
};

//现货交易-卖出
export const _tradeSell = (params) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!close.action`,
    method: "get",
    params
  })
};

export const _openView = (token) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!openview.action`,
    method: "get",
    params: {
      token
    }
  })
};

export const _closeView = (params) => {
  return request({
    url: `${API_PREFIX}/exchangeapplyorder!closeview.action`,
    method: "get",
    params
  })
};
// 开仓
export const _futrueOrder = (params) => {
  return request({
    url: `${API_PREFIX}/futuresOrder!open.action`,
    method: 'GET',
    loading: true,
    params: {
      symbol: params.symbol,
      session_token: params.session_token,
      direction: params.direction || 'buy',
      amount: params.amount, // 金额
      para_id: params.para_id // 交割周期id
    }
  })
}
// 交割订单详情
export const _futrueOrderDetail = (order_no) => {
  return request({
    url: `${API_PREFIX}/futuresOrder!get.action`,
    method: 'GET',
    loading: false,
    params: {
      order_no
    }
  })
}
/** =============================  交割合约 =======================*/
// 开仓初始化参数
export const _futrueOrderInit = (symbol) => {
  return request({
    url: `${API_PREFIX}/futuresOrder!openview.action`,
    method: 'GET',
    params: {
      symbol
    }
  })
}

// 订单列表
export const _futrueOrderList = (symbol, type = 'orders', page_no = 1, symbolType = '') => { // type: orders, hisorders
  return request({
    url: `${API_PREFIX}/futuresOrder!list.action`,
    method: 'GET',
    params: {
      symbol,
      type,
      page_no,
      symbolType
    }
  })
}

/** ================= k line =================== */

// 获取k线
export const _getKline = (symbol, line) => { // 1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week, 1分钟,5分钟,1天,1月
  return request({
    url: `${API_PREFIX}/hobi!getKline.action`,
    method: 'GET',
    params: {
      symbol,
      line
    }
  })
}

// 分时图
export const _getTrend = (symbol) => {
  return request({
    url: `${API_PREFIX}/hobi!getTrend.action`,
    method: 'GET',
    params: {
      symbol
    }
  })
}

// 近期交易记录
export const _getTrade = (symbol) => {
  return request({
    url: `${API_PREFIX}/hobi!getTrade.action`,
    method: 'GET',
    params: {
      symbol
    }
  })
}

// 获取深度深度数据
export const _getDeepData = (symbol) => {
  return request({
    url: `${API_PREFIX}/hobi!getDepth.action`,
    method: 'GET',
    params: {
      symbol
    }
  })
}

