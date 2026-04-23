import Axios from "@/utils/http";
function getShare() {
  return Axios.fetch("/api/user/getShare.action");
}
function getPromote(data) {
  return Axios.fetch("/api/promote!getPromote.action",data);
}
function getPromoteData(data) {
  return Axios.fetch("/api/promote!getPromoteData.action",data);
}
export default {
  getShare,
  getPromote,
  getPromoteData,
};
