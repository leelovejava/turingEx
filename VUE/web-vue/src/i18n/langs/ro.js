// 罗马尼亚
import roLocale from "element-plus/lib/locale/lang/ro";
import login from "../resource/Romanian/login.js";
import home from "../resource/Romanian/home";
import user from "../resource/Romanian/user";
import hangqing from "../resource/Romanian/hangqing";
import jiaoyi from "../resource/Romanian/jiaoyi";
import c2c from "../resource/Romanian/c2c";
import compositeHome from "../resource/Romanian/compositeHome"; //综合盘的
const ro = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...roLocale,
};

export default ro;
