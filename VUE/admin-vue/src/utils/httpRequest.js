import Vue from "vue";
import axios from "axios";
import router from "@/router";
import qs from "qs";
import merge from "lodash/merge";
import MyDialog from "@/components/ipPop/ip-update.vue"; // 导入外部组件
import { clearLoginInfo } from "@/utils";
import { Message } from "element-ui";
import { signatureGenerate } from '@/utils/crypto'
// import store from "@/store";
let dialogInstance = null;
const http = axios.create({
  timeout: 1000 * 30,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json; charset=utf-8",
  },
});

/**
 * 请求拦截
 */
http.interceptors.request.use(
  (config) => {
    config.headers["Authorization"] = Vue.cookie.get("Authorization"); // 请求头带上token

    const signature = signatureGenerate();
    config.headers["Sign"] = signature.signature;
    config.headers["Systemrandom"] = signature.systemRandom;
    config.headers["Tissuepaper"] = signature.timestamp;
  
    // console.log("config => " + JSON.stringify(config));

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * 响应拦截
 */
http.interceptors.response.use(
  (response) => {
    if (
      response.data.code == "403" ||
      response.data.code == "401" 
    ) {
      Message({
        message: response.data.msg,
        type: "error",
        duration: 1500,
        customClass: "element-error-message-zindex",
      });
     
      clearLoginInfo();
      router.push({ name: "login" });
      return Promise.reject(response);
    }
    console.log(response.data);
    if (response.data.code == "1001") {
      // 在这里触发弹窗组件
      if (dialogInstance) {
        return false;
      }
      //console.log(3);
      //console.log(store.user);
      // 获取当前路由信息
      const currentRoute = router.currentRoute;
      //console.log(currentRoute);
      if (currentRoute.path == "/") {
        //console.log("qqq");
        document.body.removeChild(dialogInstance.$el);
        return false;
      }
      const DialogConstructor = Vue.extend(MyDialog);
      dialogInstance = new DialogConstructor();
      dialogInstance.$mount();
      // 监听弹窗关闭事件
      // dialogInstance.$on("close", () => {
      //   console.log("qqq");
      //   document.body.removeChild(dialogInstance.$el);
      // });

      // 将弹窗组件实例挂载到页面中
      document.body.appendChild(dialogInstance.$el);
      return Promise.reject(error);
    }
    return response;
  },
  (error) => {
    if (!error.response) {
      console.log("error = " + JSON.stringify(error));
    }
    switch (error.response.status) {
      case 400:
        Message({
          message: error.response.data,
          type: "error",
          duration: 1500,
          customClass: "element-error-message-zindex",
        });
        break;
      case 401:
        clearLoginInfo();
        router.push({ name: "login" });
        console.log(1);
        if (dialogInstance) {
          console.log(2);
          document.body.removeChild(dialogInstance.$el);
        }
        break;
      case 405:
        Message({
          message: "http请求方式有误",
          type: "error",
          duration: 1500,
          customClass: "element-error-message-zindex",
        });
        break;
      case 500:
        Message({
          message: "服务器出了点小差，请稍后再试",
          type: "error",
          duration: 1500,
          customClass: "element-error-message-zindex",
        });
        break;
      case 501:
        Message({
          message: "服务器不支持当前请求所需要的某个功能",
          type: "error",
          duration: 1500,
          customClass: "element-error-message-zindex",
        });
        break;
    }
    return Promise.reject(error);
  }
);

/**
 * 请求地址处理
 * @param {*} actionName action方法名称
 */
http.adornUrl = (actionName) => {
  // 非生产环境 && 开启代理, 接口前缀统一使用[/proxyApi/]前缀做代理拦截!
  // return (
  //   (process.env.NODE_ENV !== "production" && process.env.OPEN_PROXY
  //     ? "/proxyApi"
  //     : process.env.VUE_APP_BASE_API) + actionName
  // );

  return (
    (process.env.NODE_ENV !== "production"
      ? process.env.VUE_APP_BASE_API
      : process.env.VUE_APP_API_BASE_URL) + actionName
  );
};

/**
 * get请求参数处理
 * @param {*} params 参数对象
 * @param {*} openDefultParams 是否开启默认参数?
 */
http.adornParams = (params = {}, openDefultParams = true) => {
  var defaults = {
    t: new Date().getTime(),
  };
  return openDefultParams ? merge(defaults, params) : params;
};

/**
 * post请求数据处理
 * @param {*} data 数据对象
 * @param {*} openDefultdata 是否开启默认数据?
 * @param {*} contentType 数据格式
 *  json: 'application/json; charset=utf-8'
 *  form: 'application/x-www-form-urlencoded; charset=utf-8'
 */
http.adornData = (data = {}, openDefultdata = true, contentType = "json") => {
  var defaults = {
    t: new Date().getTime(),
  };
  data = openDefultdata ? merge(defaults, data) : data;
  return contentType === "json" ? JSON.stringify(data) : qs.stringify(data);
};

export default http;
