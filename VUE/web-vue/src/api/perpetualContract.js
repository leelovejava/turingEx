import Axios from "@/utils/http";
// 开仓初始化参数,获取可用余额之类的
function _initOpen(data) {
  return Axios.fetch("/api/contractApplyOrder!openview.action", data);
}

// 平仓初始化参数
function _initClose(data) {
  return Axios.fetch("/api/contractApplyOrder!closeview.action", data);
}

///api/contractApplyOrder!cancelAll.action
function cancelAll() {
  return Axios.fetch("/api/contractApplyOrder!cancelAll.action");
}
// 撤单
function cancel(data) {
  return Axios.fetch("/api/contractApplyOrder!cancel.action", data);
}
// 开仓下单
function contractApplyOrderOpen(data) {
  return Axios.fetch("/api/contractApplyOrder!open.action", data);
}

// 持仓 + 历史订单 + 未实现盈亏
function contractOrderList(data) {
  return Axios.fetch("/api/contractOrder!list.action", data);
}
// 部分平仓  用于持仓区域的平仓，以及下单区的平仓
function orderPartClose(data) {
  return Axios.fetch("/api/contractApplyOrder!close.action", data);
}
// 订单全平  持仓区域的市价全平
function orderClose(data) {
  return Axios.fetch("/api/contractOrder!close.action", data);
}
// 全平  暂时没用到
function allOrderClose(data) {
  return Axios.fetch("/api/contractOrder!closeAll.action", data);
}
function orderOpenview(data) {
  return Axios.fetch("/api/contractApplyOrder!closeview.action", data);
}

// 资产
function getAssets(data) {
  return Axios.fetch("/api/wallet/getAll.action", data);
}
// 委托订单列表
function contractApplyOrderOpenList(data) {
  return Axios.fetch("/api/contractApplyOrder!list.action", data);
}

// 永续修改持仓单 修改止盈止损
function stopLossAndProfit(data) {
  return Axios.fetch("/api/contractOrder!stopLossAndProfit.action", data);
}
//
//  /api/withdraw!apply.action
// /api/contractApplyOrder!openview.action
// //api/contractApplyOrder!get.action
export default {
  _initClose,
  _initOpen,
  contractApplyOrderOpen,
  orderPartClose,
  contractApplyOrderOpenList,
  cancelAll,
  cancel,
  contractOrderList,
  orderClose,
  allOrderClose,
  orderOpenview,
  getAssets,
  stopLossAndProfit
};
