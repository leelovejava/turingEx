import Axios from "@/utils/http";

//交割合约买入开仓页面参数
function getFutures(data) {
  return Axios.fetch("/api/futuresOrder!openview.action", data);
}

//交割合约开仓
function futuresOpen(data) {
  return Axios.fetch("/api/futuresOrder!open.action", data);
}

//交割订单列表
function futuresList(data) {
  return Axios.fetch("/api/futuresOrder!list.action", data);
}

//交割订单详情
function futuresDetail(data) {
  return Axios.fetch("/api/futuresOrder!get.action", data);
}

export default {
  getFutures,
  futuresOpen,
  futuresList,
  futuresDetail,
};
