import itLocale from "element-plus/lib/locale/lang/it";
import login from "../resource/Italia/login";
import home from "../resource/Italia/home";
import user from "../resource/Italia/user";
import hangqing from "../resource/Italia/hangqing";
import jiaoyi from "../resource/Italia/jiaoyi";
import c2c from "../resource/Italia/c2c";
import compositeHome from "../resource/Italia/compositeHome"; //综合盘的
const it = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...itLocale,
};

export default it;
