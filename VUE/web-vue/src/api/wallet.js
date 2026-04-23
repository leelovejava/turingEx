import Axios from "@/utils/http";

function listPaymentmethod(data) {
  return Axios.fetch("/api/paymentmethod!list.action", data);
}

function deletePaymentmethod(data) {
  return Axios.fetch("/api/paymentmethod!delete.action", data);
}

function addPaymentmethod(data) {
  return Axios.fetch("/api/paymentmethod!add.action", data);
}

//获得所有资产
//"forex->外汇,commodities->大宗商品，指数/ETF->indices,  A-stocks->A股,  HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币

function getAllAssets(data) {
  return Axios.fetch("/api/assets!getAll.action", data);
}

// 资产的聚合接口
function getAggregationAssets(data) {
  return Axios.fetch("/api/assets!getAllAggregation.action", data);
}

//钱包历史记录
function getWalletHistory(data) {
  return Axios.fetch("/api/wallet/records.action", data);
}

//获取永续合约持有仓位列表，没有分页，前端分页
function getOrderList(data) {
  return Axios.fetch("/api/contractOrder!list.action", data);
}
//获取永续合约委托持有仓位列表，没有分页，前端分页
function getOrderApplyList(data) {
  return Axios.fetch("/api/contractApplyOrder!list.action", data);
}

//交割合约持有仓位列表
function getFutureList(data) {
  return Axios.fetch("/api/futuresOrder!list.action", data);
}

// 现货订单
function getSpotList(data) {
  return Axios.fetch("/api/exchangeapplyorder!list.action", data);
}

//钱包列表
function getWalletList(data) {
  return Axios.fetch("/api/wallet/list.action", data);
}
// 获取兑换后币种的数量和手续费
function getFee(data) {
  return Axios.fetch("/api/exchangeapplyorder!buy_and_sell_fee.action", data);
}

// 获取兑换的sessiontoken
function getExchangeapplyorderView(data) {
  return Axios.fetch("/api/exchangeapplyorder!view.action", data);
}

// 兑换接口
function getTrans(data) {
  return Axios.fetch("/api/exchangeapplyorder!buy_and_sell.action", data);
}

//矿池详情
function goMineDetail(data) {
  return Axios.fetch("/api/minerOrder!get.action", data);
}

//理财详情

function goDetail(data) {
  return Axios.fetch("/api/financeOrder!get.action", data);
}

//理财账户列表数据
function listFinanceOrder(data) {
  return Axios.fetch("/api/financeOrder!list.action", data);
}

//矿池锁仓列表
function listMinerOrder(data) {
  return Axios.fetch("/api/minerOrder!list.action", data);
}

// 理财赎回
function financeOrderOfClose(data) {
  return Axios.fetch("/api/financeOrder!closOrder.action", data);
}

// 矿池解锁
function minerOrderOfClose(data) {
  return Axios.fetch("/api/minerOrder!closOrder.action", data);
}

function url(url, data) {
  return Axios.fetch(url, data);
}

// 链名称
function getChainName(data) {
  return Axios.fetch("api/channelBlockchain!getBlockchainName.action", data);
}

// 获取充值说明
function getRechargeTips(data) {
  return Axios.fetch("api/cms!get.action", data);
}

// 充值的币种列表
function getRechargeCurrencyList(data) {
  return Axios.fetch("api/c2cOrder/currency", data);
}

//获取USD充值session_token
function getUSDRechargeToken(data) {
  return Axios.fetch("api/rechargeBlockchain/rechargeOpen", data);
}

//获取银行卡充值session_token post
function getBankRechargeToken(data) {
  return Axios.post("api/c2cOrder/orderOpen", data);
}

//提交充值申请 post
function rechargeApply(data) {
  return Axios.fetch("api/rechargeBlockchain/recharge", data);
}

//数字货币充值记录   0 初始状态，未知或处理中 1 成功 2 失败
function getCrypotRechargeList(data) {
  return Axios.fetch("api/rechargeBlockchain/list", data);
}

//获取c2c订单列表    0未付款/1已付款/2申诉中/3已完成/4已取消/5已超时
function getC2cList(data) {
  return Axios.fetch("api/c2cOrder/list", data);
}

//银行卡订单详情  ??????
function getBankOrderDetail(data) {
  return Axios.fetch("api/c2cOrder/get", data);
}

//银行卡取消订单 post
function cancelBankOrder(data) {
  return Axios.fetch("api/c2cOrder/orderCancel", data);
}

//充值订单详情
function getRechargeOrderDetail(data) {
  return Axios.fetch("api/rechargeBlockchain/get", data);
}

//提现订单详情
function getWithdrawOrderDetail(data) {
  return Axios.fetch("api/withdraw/get", data);
}

//获取提现session_token
function getWithdrawToken(data) {
  return Axios.fetch("api/withdraw/withdrawOpen", data);
}

//提现申请 post
function withdrawApply(data) {
  return Axios.postFormData("api/withdraw/apply", data);
}

//提现手续费计算
function getWithdrawFee(data) {
  return Axios.fetch("api/withdraw/fee", data);
}
//数字货币提现记录  0 初始状态，未知或处理中 1 成功 2 失败
function getWithdrawList(data) {
  return Axios.fetch("api/withdraw/list", data);
}

//获取我的支付方式
function getMyPaymentMethodList(data) {
  return Axios.fetch("api/paymentMethod/myList", data);
}

//充值提现订单提交
function applyOrder(data) {
  return Axios.postFormData("api/c2cOrder/apply", data);
}

// 获取余额
function getBalanceUsdt(data) {
  return Axios.fetch("api/wallet/getUsdt", data);
}

export default {
  listPaymentmethod,
  deletePaymentmethod,
  addPaymentmethod,
  getAllAssets,
  getWalletHistory,
  getOrderList,
  getOrderApplyList,
  getFutureList,
  getWalletList,
  getFee,
  getExchangeapplyorderView,
  getTrans,
  goMineDetail,
  goDetail,
  listFinanceOrder,
  listMinerOrder,
  financeOrderOfClose,
  minerOrderOfClose,
  url,
  getSpotList,
  getAggregationAssets,
  getChainName,

  // 充值
  getRechargeTips,
  getRechargeCurrencyList,
  getUSDRechargeToken,
  rechargeApply,
  getBankRechargeToken,
  getCrypotRechargeList,
  getC2cList,
  getBankOrderDetail,
  cancelBankOrder,
  getRechargeOrderDetail,

  // 提现
  getWithdrawToken,
  withdrawApply,
  getWithdrawFee,
  getWithdrawList,
  getWithdrawOrderDetail,

  getMyPaymentMethodList,

  applyOrder,
  getBalanceUsdt,
};
