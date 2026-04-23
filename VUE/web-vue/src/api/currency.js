import Axios from "@/utils/http";
//获取所有币种信息,根据不同type区分
//"forex->外汇,commodities->大宗商品，指数/ETF->indices,
// A-stocks->A股,  HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币,TW-stocks->台股
function getAllSymbolDetails(data) {
  return Axios.fetch("/api/item!list.action", data);
}

// 获取币种的实时数据，比如价格和涨跌幅
// https://sjiepc.com/api/doc.html#/%E5%A4%96%E6%B1%87%E7%AE%A1%E7%90%86%E5%B9%B3%E5%8F%B0/%E5%AE%9E%E6%97%B6%E8%A1%8C%E6%83%85%E6%95%B0%E6%8D%AE/getRealtimeUsingGET
function getRealtime(data,controller=null) {
  return Axios.fetch("/api/hobi!getRealtime.action", data,controller);
}

// 以上两个合并成这个 处理数据量大的情况
function publicRealtimeByType(data,controller=null) {
  return Axios.fetch("/api/publicRealtimeByType", data,controller);
}

// 置顶币种信息
function publicRealtimeTop(data,controller=null) {
  return Axios.fetch("/api/publicRealtimeTop", data,controller);
}

//币币交易买入开仓页面参数
function BuyOpenView(data) {
  return Axios.fetch("/api/exchangeapplyorder!openview.action", data);
}

//币币交易卖出开仓页面参数
function SellOpenView(data) {
  return Axios.fetch("/api/exchangeapplyorder!closeview.action", data);
}

//获取可得数量
function getbuySellFee(data) {
  return Axios.fetch("/api/exchangeapplyorder!buy_and_sell_fee.action", data);
}

//币币买入
function currencyBuy(data) {
  return Axios.fetch("/api/exchangeapplyorder!open.action", data);
}

//币币卖出
function currencySell(data) {
  return Axios.fetch("/api/exchangeapplyorder!close.action", data);
}

//币币交易记录，type= "forex->外汇,commodities->大宗商品，指数/ETF->indices,  A-stocks->A股,  HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
function currencyTradeRecord(data) {
  return Axios.fetch("/api/exchangeapplyorder!list.action", data);
}

//钱包账户资产
function currencyPaypal(data) {
  return Axios.fetch("/api/assets!getAll.action", data);
}

//撤销当前委托单
function cancelCurrencyOrder(data) {
  return Axios.fetch("/api/exchangeapplyorder!cancel.action", data);
}
//加入自选币种
function addItemUserOptiona(data) {
  return Axios.fetch("/api/itemUserOptional!add.action", data);
}
//查询自选
function getItemOptionalStatus() {
  return Axios.fetch("/api/itemUserOptional!list.action");
}
//删除自选
function deleteItemUserOptiona(data) {
  return Axios.fetch("/api/itemUserOptional!delete.action", data);
}

//查询自选2
function getItemOptionalStatus2(data) {
  return Axios.fetch("/api/itemUserOptional!list.action", data);
}

export default {
  getAllSymbolDetails,
  getRealtime,
  publicRealtimeByType,
  publicRealtimeTop,
  BuyOpenView,
  SellOpenView,
  getbuySellFee,
  currencyBuy,
  currencySell,
  currencyTradeRecord,
  cancelCurrencyOrder,
  currencyPaypal,
  addItemUserOptiona,
  getItemOptionalStatus,
  deleteItemUserOptiona,
  getItemOptionalStatus2,
};
