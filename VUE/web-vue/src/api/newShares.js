import Axios from "@/utils/http";

// 获取新股认购列表
function getNewSharesList(data) {
  return Axios.postFormData("/api/newSharesConfig/list", data);
}

// 获取新股认购已上市列表
function getNewIssueList(data) {
    return Axios.fetch("/api/newSharesConfig/newIssueList", data);
}

// 获取抽签/认缴内容
function getDrawMessage(data) {
  return Axios.postFormData("/api/newSharesConfig/getDesc", data);
}

// 抽签
function setDrawMessage(data) {
  return Axios.postFormData("/api/sNewSharesOrder/apply", data);
}

// 认缴
function setPayMessage(data) {
  return Axios.postFormData("/api/userPromise/applyPromise", data);
}


// 1 抽签记录 2 新股库存  3 现股库存
function getNewSharesOrderList(data) {
  return Axios.postFormData("/api/sNewSharesOrder/list", data);
}

// 认缴记录
function getPayMessageOrderList(data) {
  return Axios.postFormData("/api/userPromise/list", data);
}

// 获取记录顶部内容 1 抽签记录 2 新股库存  3 现股库存
function getNewSharesHeaderMessage(data) {
  return Axios.postFormData("/api/spotStock/getTopData", data);
}

// 获取认缴记录顶部内容
function getPayOrderHeaderMessage(data) {
  return Axios.postFormData("/api/userPromise/getTop", data);
}

// 卖出
function sellNowShares(data) {
  return Axios.postFormData("/api/sNewSharesOrder/list", data);
}

export default {
    getNewSharesList,
    getNewIssueList,
    getDrawMessage,
    setDrawMessage,
    getNewSharesOrderList,
    getNewSharesHeaderMessage,
    setPayMessage,
    getPayMessageOrderList,
    getPayOrderHeaderMessage,
    sellNowShares
  };