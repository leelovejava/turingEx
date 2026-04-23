import CryptoJS from 'crypto-js'
import md5 from "js-md5";


// 加密
const keyStr = '-mall4j-password' // 解密用的key
export function encrypt (word) {
  const time = Date.now()

  const key = CryptoJS.enc.Utf8.parse(keyStr)
  const srcs = CryptoJS.enc.Utf8.parse(time + word) // 加密方式: 时间戳 + 密文
  const encrypted = CryptoJS.AES.encrypt(srcs, key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7})
  return encrypted.toString()
}

// let env_secret = 'd78585e683ed11eaa13f0242ac110003';
let env_secret = process.env.VUE_APP_KEY;
// console.log(env_secret);
export function signatureGenerate() {
// console.log(env_secret);
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
