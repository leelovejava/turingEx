import deLocale from "element-plus/lib/locale/lang/de";
import login from "../resource/German/login";
import home from "../resource/German/home";
import user from "../resource/German/user";
import hangqing from "../resource/German/hangqing";
import jiaoyi from "../resource/German/jiaoyi";
import c2c from "../resource/German/c2c";
import compositeHome from "../resource/German/compositeHome"; //综合盘的
const de = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...deLocale,
};

export default de;
