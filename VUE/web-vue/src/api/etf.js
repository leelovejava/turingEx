import Axios from "@/utils/http";

//简况
function getSummary(data) {
  return Axios.fetch("/api/item/itemSummary/get", data);
}
//获取成分股币对名称
function getStock(data) {
  return Axios.fetch("/api/projectBreed/getConstituentStockList", data);
}

export default {
  getStock,
  getSummary,
};
