import Axios from "@/utils/http";

// 理财账户订单
function getFinanceOrderList(data) {
  return Axios.fetch("/api/financeOrder!list.action", data);
}

//理财账户列表数据
function getFinanceList(data) {
  return Axios.fetch("/api/finance!list.action", data);
}

//理财账户订单汇总
function getFinanceOrderSum(data) {
  return Axios.fetch("/api/financeOrder!listSum.action", data);
}

//获取可用余额USDT
function getWallet(data) {
  return Axios.fetch("/api/wallet/getAll.action", data);
}

// 矿池锁仓
function getMinerList(data) {
  return Axios.fetch("/api/miner!list.action", data);
}

//矿池锁仓订单列表
function getMinerOrderList(data) {
  return Axios.fetch("/api/minerOrder!list.action", data);
}

//矿池锁仓订单汇总
function getMinerOrderSum(data) {
  return Axios.fetch("/api/minerOrder!listSum.action", data);
}

export default {
  getFinanceList,
  getFinanceOrderList,
  getFinanceOrderSum,
  getMinerOrderList,
  getWallet,
  getMinerList,
  getMinerOrderSum,
};
