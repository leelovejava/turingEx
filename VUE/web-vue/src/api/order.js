import Axios from "@/utils/http";
//获取合约订单
function getContractOrder(data) {
  return Axios.fetch("/api/contractOrder!list.action", data);
}
//获取现货订单,type= "forex->外汇,commodities->大宗商品，指数/ETF->indices,  A-stocks->A股,  HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
function getSpotOrder(data) {
  return Axios.fetch("/api/exchangeapplyorder!list.action", data);
}

//获取交割订单
function getDeliveryOrder(data) {
  return Axios.fetch("/api/futuresOrder!list.action", data);
}

//理财基金
function getFinanceOrder(data) {
  return Axios.fetch("/api/financeOrder!list.action", data);
}
//矿池锁仓
function getMinerOrder(data) {
  return Axios.fetch("/api/minerOrder!list.action", data);
}
//兑换列表
function getExchangeOrder(data) {
  return Axios.fetch("/api/exchangeapplyorder!queryExchangeList", data);
}

//账变记录
function getChangeRecord(data) {
  return Axios.fetch("/api/moneylog!list.action", data);
}

export {
  getContractOrder,
  getSpotOrder,
  getFinanceOrder,
  getMinerOrder,
  getExchangeOrder,
  getChangeRecord,
  getDeliveryOrder,
};
