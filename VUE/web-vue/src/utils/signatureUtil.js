// signatureUtil.js
import md5 from "js-md5";

let env_secret = import.meta.env.VITE_APP_SECRET;

export function signatureGenerate() {
  // 参数签名 密钥 + 时间戳 + header参数 + url
  // 密钥
  let secret = env_secret;
  // 时间戳
  let timestamp = Math.floor(new Date().getTime() / 1000);

  // token
  // let token = params.token
  // // post参数
  // let dataStr = dataSerialize(dataSort(params))
  // 生成签名
  // let str = dataStr + "secret=" + secret + "&timestamp=" + timestamp + "&url=" + url
  let systemRandom = `${timestamp}${Math.random().toString().slice(-6)}`;
  let str = `${secret}${timestamp}${systemRandom}`;
  const sign = md5(str);
  return {
    signature: sign.toUpperCase(),
    timestamp,
    systemRandom,
  };
}

// 参数排序
function dataSort(obj) {
  if (JSON.stringify(obj) == "{}" || obj == null) {
    return {};
  }
  let key = Object.keys(obj)?.sort();
  let newObj = {};
  for (let i = 0; i < key.length; i++) {
    newObj[key[i]] = obj[key[i]];
  }
  return newObj;
}

// 参数序列化
function dataSerialize(sortObj) {
  let strJoin = "";
  for (let key in sortObj) {
    strJoin += key + "=" + sortObj[key] + "&";
  }

  // return strJoin.substring(0, strJoin.length - 1)
  return strJoin;
}
