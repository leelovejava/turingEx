import Axios from "@/utils/http";

// 获取用户实名认证信息
function getIdentify(data) {
  return Axios.fetch("/api/realNameAuth/get", data);
}
// 申请实名认证
function apply(data) {
  return Axios.postFormData("/api/realNameAuth/apply", data);
}

// 获取高级认证信息，如家庭住址等
function getHighIdentify(data) {
  return Axios.post("/api/highLevelAuth/get", data);
}

// 申请高级认证
function applyHigh(data) {
  return Axios.postFormData("/api/highLevelAuth/apply", data);
}

//兑换币种
function getExchangerateuserconfig() {
  return Axios.fetch("/api/rate/exchangeRate/list");
}

//帮助中心
function getCms(data) {
  return Axios.fetch("/api/cms!list.action", data);
}

//公告
function news(data) {
  return Axios.fetch("/api/news!list.action", data);
}

//绑定邮件
function saveEmail(data) {
  return Axios.postFormData("/api/user/saveEmail", data);
}

//绑定手机
function savePhone(data) {
  return Axios.fetch("/api/user/savePhone", data);
}

//重置安全密码，获取人工审核的结果
function getSafewordApply(data) {
  return Axios.fetch("/api/user/getSafewordApply", data);
}

//
function setSafewordApply(data) {
  return Axios.postFormData("/api/user/setSafewordApply", data);
}

//修改资金米啊
function setSafeword(data) {
  return Axios.postFormData("/api/user/setSafeword", data);
}

//获取验证目标
function getVerifTarget(data) {
  return Axios.post("/api/user/getVerifTarget", data);
}

//获得谷歌绑定的信息
function getGoogleAuth(data) {
  return Axios.fetch("/api/gooleAuth/get", data);
}

//绑定谷歌绑定
function bindGoogleAuth(data) {
  return Axios.postFormData("/api/gooleAuth/bind", data);
}

function updatepsw(data) {
  return Axios.fetch("api/user/updatePsw", data);
}

//用户信息
function getUserInfo(data) {
  return Axios.fetch("/api/user/get", data);
}

export default {
  getIdentify,
  apply,
  getHighIdentify,
  applyHigh,
  getExchangerateuserconfig,
  getCms,
  news,
  saveEmail,
  savePhone,
  setSafewordApply,
  getSafewordApply,
  setSafeword,
  getVerifTarget,
  getGoogleAuth,
  bindGoogleAuth,
  updatepsw,
  getUserInfo,
};
