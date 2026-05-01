import request from "./request";
import { API_PREFIX, METHODS } from "@/config";

export const _workerOrderCreate = (params) => {
  return request({
    url: `${API_PREFIX}/workerOrder!create.action`,
    method: METHODS.POST,
    params,
    loading: true,
  });
};

export const _workerOrderList = (params) => {
  return request({
    url: `${API_PREFIX}/workerOrder!list.action`,
    method: METHODS.GET,
    params,
  });
};

export const _workerOrderDetail = (params) => {
  return request({
    url: `${API_PREFIX}/workerOrder!detail.action`,
    method: METHODS.GET,
    params,
  });
};

export const _workerOrderReply = (params) => {
  return request({
    url: `${API_PREFIX}/workerOrder!reply.action`,
    method: METHODS.POST,
    params,
    loading: true,
  });
};

