import axios from "axios";
// import  URL  from "../config/index";
import { Message } from "element-ui";
import allTits from "../assets/lan/tits.js";
import { signatureGenerate } from '@/utils/crypto'
// import router from "../router.js";
axios.defaults.timeout = 15000;
axios.defaults.baseURL = URL.BASE_URL;
//http request 拦截器
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("spToken");
    // config.data = JSON.stringify(config.data);
    // config.headers = {
    //   'Content-Type':'application/x-www-form-urlencoded'
    // }
    if (token) {
      if (config.method == "get") {
        config.params.token = token;
      } else if (config.data == "post") {
        config.params.token = token;
      }
    }
    const signature = signatureGenerate();
    config.headers["Sign"] = signature.signature;
    config.headers["Systemrandom"] = signature.systemRandom;
    config.headers["Tissuepaper"] = signature.timestamp;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

//http response 拦截器
axios.interceptors.response.use(
  (response) => {
    if (response.data.code != 0) {
      var localLan = "",
        lanTits = "";
        //判断语言
      if (localStorage.getItem("localLan")) {
        localLan = localStorage.getItem("localLan");
        if (localLan == "en") {
          lanTits = "tits_en";
        } else if (localLan == "zh-CN") {
          lanTits = "tits_zh-CN";
        } else if (localLan == "cht") {
          lanTits = "tits_CN";
        } else if (localLan == "ko") {
          lanTits = "tits_Korean";
        } else if (localLan == "fr") {
          lanTits = "tits_fr";
        } else if (localLan == "de") {
          lanTits = "tits_de";
        } else if (localLan == "it") {
          lanTits = "tits_it";
        }
      }
      //403
      if (response.data.code == "403") {
        console.log('routerrouterrouterrouterrouterrouterrouterrouterrouterrouterrouter',router)
        let lang = localStorage.getItem('localLan')
        localStorage.clear()
        localStorage.removeItem("username");
        localStorage.removeItem("spToken");
        localStorage.removeItem("vuex");
        localStorage.setItem('localLan', lang)
      //   router.beforeEach((to, from, next) => {
      //     //会在任意路由跳转前执行，next一定要记着执行，不然路由不能跳转了
      //   console.log('beforeEach')
      //   console.log(to,from)
      //   //
      //   next()
      // })
      //  router.push("/login");
       location.reload()
      }
      if (allTits[lanTits][response.data.msg]) {
        Message(allTits[lanTits][response.data.msg]);
      } else {
        Message(response.data.msg);
      }
    }
    return response;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * 封装get方法
 * @param url
 * @param data
 * @returns {Promise}
 */
export function fetch(url, params = {}) {
  return new Promise((resolve, reject) => {
    axios
      .get(url, {
        params: params,
      })
      .then((response) => {
        resolve(response.data);
      })
      .catch((err) => {
        reject(err);
      });
  });
}

/**
 * 封装post请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export function post(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.post(url, data).then(
      (response) => {
        resolve(response.data);
      },
      (err) => {
        reject(err);
      }
    );
  });
}

/**
 * 封装patch请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export function patch(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.patch(url, data).then(
      (response) => {
        resolve(response.data);
      },
      (err) => {
        reject(err);
      }
    );
  });
}

/**
 * 封装put请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export function put(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.put(url, data).then(
      (response) => {
        resolve(response.data);
      },
      (err) => {
        reject(err);
      }
    );
  });
}
export default { fetch, patch, put, post };
