import Axios from "@/utils/http";
function getKline(data) {
  return Axios.fetch("/api/hobi!getKline.action", data);
}
export default {
  getKline,
};
