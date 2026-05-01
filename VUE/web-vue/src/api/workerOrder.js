import Axios from "@/utils/http";

function workerOrderList(data) {
  return Axios.fetch("/api/workerOrder!list.action", data);
}

function workerOrderCreate(data) {
  return Axios.fetch("/api/workerOrder!create.action", data);
}

function workerOrderDetail(data) {
  return Axios.fetch("/api/workerOrder!detail.action", data);
}

function workerOrderReply(data) {
  return Axios.fetch("/api/workerOrder!reply.action", data);
}

export default {
  workerOrderList,
  workerOrderCreate,
  workerOrderDetail,
  workerOrderReply,
};

