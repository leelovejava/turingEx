import Axios from "@/utils/http";
function getPairsWallet(data) {
  return Axios.fetch("/api/wallet/getAll.action", data);
}
function getExchangerateuserconfig() {
  return Axios.fetch("/api/exchangerate!list.action");
}
function getExchangerateuserconfigInfo() {
  return Axios.fetch("/api/exchangerateuserconfig!get.action");
}
export default {
  getPairsWallet,
  getExchangerateuserconfig,
  getExchangerateuserconfigInfo,
};
