
let base = import.meta.env.VITE_APP_API_BASE_URL;

let HOST_URL = base;
let BASE_URL = base;
let WS_URL = import.meta.env.VITE_APP_WEB_SOCKET_URL;

export default {
  HOST_URL,
  BASE_URL,
  WS_URL,
};
