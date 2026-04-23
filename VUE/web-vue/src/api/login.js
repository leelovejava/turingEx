import Axios from "@/utils/http";
//登录
function login(data) {
  return Axios.fetch("/api/user/login", data);
}
//获取邮箱或手机号的验证码
function getVeriCode(data) {
  return Axios.fetch("api/idcode/execute", data);
}

//根据用户名和类型获取它的验证方式，用于重置密码界面，
function forgotVericode(data) {
  return Axios.fetch("api/user/getUserNameVerifTarget", data);
}
//通过邮箱或手机号，或谷歌重置密码
function fixResgister(data) {
  return Axios.postFormData("api/user/resetPsw", data);
}
//退出登录
function loginOut() {
  return Axios.fetch("api/user/logout");
}
//用账户名注册时，获取接口返回的base64图片
function imageVerifica() {
  return Axios.fetch("api/user/getImageCode");
}

//账号名注册
function accountRegister(data) {
  return Axios.fetch("api/user/registerUsername", data);
}

//手机号或邮箱注册
function resgister(data) {
  return Axios.fetch("/api/user/register", data);
}

///通过谷歌验证器重置密码
function resetpswByGoogle(data) {
  return Axios.fetch("/api/user!resetpswByGoogle.action", data);
}

//判断是否读取三方客服
function getServiceUrl(data) {
  return Axios.fetch("/api/syspara!getSyspara.action", data);
}

export default {
  login,
  getVeriCode,
  resgister,
  forgotVericode,
  fixResgister,
  loginOut,
  imageVerifica,
  accountRegister,
  resetpswByGoogle,
  getServiceUrl,
};
