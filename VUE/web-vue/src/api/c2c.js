import Axios from "@/utils/http";
//币种获取
function c2cSymbolList() {
  return Axios.fetch("/api/c2cAdvert!symbol.action");
}
//购买界面，承兑商列表
function c2cAdvertList(data) {
  return Axios.fetch("/api/c2cAdvert!list.action", data);
}
//C2C 自由买卖的支付类型
function c2cPaymentMethodList(data) {
  return Axios.fetch("/api/c2cPaymentMethod!method_type.action", data);
}
//c2c购买初始化
function c2cOrderOpen(data) {
  return Axios.fetch("/api/c2cOrder!order_open.action", data);
}
//获取广告详情
function c2cAdvertGetDetail(data) {
  return Axios.fetch("/api/c2cAdvert!get.action", data);
}
//自由买币-购买/出售  生成订单
function c2cOrderOpenOrder(data) {
  return Axios.fetch("/api/c2cOrder!open.action", data);
}
//获取 承兑商广告 支付方式列表
function ctcPaymentMethodPayList(data) {
  return Axios.fetch("/api/c2cPaymentMethod!getAdPayments.action", data);
}
//获取订单详情
function c2cOrderGetDetail(data) {
  return Axios.fetch("/api/c2cOrder!get.action", data);
}
//我已付款
function pay_finish(data) {
  return Axios.fetch("/api/c2cOrder!pay_finish.action", data);
}
//取消订单
function order_cancel(data) {
  return Axios.fetch("/api/c2cOrder!order_cancel.action", data);
}
//用户订单列表
function userc2cOrderList(data) {
  return Axios.fetch("/api/c2cOrder!list.action", data);
}

// 资产
function getAssets(data) {
  return Axios.fetch("/api/wallet/getAll.action", data);
}
// 获取价格最优的广告商
function get_best_price_advert(data) {
  return Axios.fetch("/api/c2cOrder!get_best_price_advert.action", data);
}
//获取用户详情
function getUserCenterC2cUser(data) {
  return Axios.fetch("/api/c2cUser!getUserCenter.action", data);
}
//获取支付方式详情
function getC2cPaymentMethod(data) {
  return Axios.fetch("/api/c2cPaymentMethod!get.action", data);
}
//用户快捷支付或者购买
function openQuickApply(data) {
  return Axios.fetch("/api/c2cOrder!open_quick_apply.action", data);
}
//获取承兑商详情
function getC2cUser(data) {
  return Axios.fetch("/api/c2cUser!get.action", data);
}
//获取个人信息
function getUser() {
  return Axios.fetch("/api/localuser!get.action");
}
//获取聊天记录
function getOtcOnlinechat(data) {
  return Axios.fetch("/api/otcOnlinechat!list.action", data);
}
//发送消息
function sendOtcOnlinechat(data) {
  return Axios.fetch("/api/otcOnlinechat!send.action", data);
}
//申诉提交
function c2cAppealApply(data) {
  return Axios.fetch("/api/c2cAppeal!apply.action", data);
}
//订单放行
function c2cOrderOrderPass(data) {
  return Axios.fetch("/api/c2cOrder!order_pass.action", data);
}

// 获取支持的收款方式
function payMethodList(data) {
  return Axios.fetch("/api/c2cPaymentMethodConfig!list.action", data);
}

//获取该用户支持的收款方式
function userC2cPaymentMethodList(data) {
  return Axios.fetch("/api/c2cPaymentMethod!list.action", data);
}

// 删除payconfig
function delPayConfig(data) {
  return Axios.fetch("/api/c2cPaymentMethod!delete.action", data);
}

// 新增payconfig
function addPayConfig(data) {
  return Axios.fetch("/api/c2cPaymentMethod!add.action", data);
}

// 更新payconfig
function updatePayConfig(data) {
  return Axios.fetch("/api/c2cPaymentMethod!update.action", data);
}

// 获取支付设置
function getPayConfig(data) {
  return Axios.fetch("/api/c2cPaymentMethodConfig!get.action", data);
}

export default {
  c2cSymbolList,
  c2cAdvertList,
  c2cPaymentMethodList,
  c2cOrderOpen,
  c2cAdvertGetDetail,
  c2cOrderOpenOrder,
  ctcPaymentMethodPayList,
  c2cOrderGetDetail,
  payMethodList,
  delPayConfig,
  pay_finish,
  order_cancel,
  userc2cOrderList,
  userC2cPaymentMethodList,
  getAssets,
  get_best_price_advert,
  getUserCenterC2cUser,
  getC2cPaymentMethod,
  openQuickApply,
  getC2cUser,
  getUser,
  getOtcOnlinechat,
  sendOtcOnlinechat,
  c2cAppealApply,
  c2cOrderOrderPass,
  getPayConfig,
  addPayConfig,
  updatePayConfig,
};
