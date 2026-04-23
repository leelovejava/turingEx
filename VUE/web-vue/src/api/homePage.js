import Axios from "@/utils/http";

//关于我们
function getAboutUs(data) {
  return Axios.fetch("/api/cms!get.action", data);
}

export default {
  getAboutUs,
};
