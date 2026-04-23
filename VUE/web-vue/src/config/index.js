
let env_url = ''
if (import.meta.env.MODE === 'development') {
   env_url = "penkn.com" 
} else {
  env_url = window.location.host;
}
let ENV_DEV = "https://" + env_url + "/api/"; // dev

let HOST_URL = "";
let BASE_URL = "";
let WS_URL = "";

HOST_URL = "https://" + env_url;
BASE_URL = ENV_DEV;
WS_URL = `wss://${env_url}/api/api/websocket`;

export default {
  HOST_URL,
  BASE_URL,
  WS_URL,
};
