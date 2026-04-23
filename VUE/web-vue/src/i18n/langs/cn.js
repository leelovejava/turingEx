import cnLocale from "element-plus/lib/locale/lang/zh-cn";
import login from "../resource/China_cn/login";
import home from "../resource/China_cn/home";
import user from "../resource/China_cn/user";
import hangqing from "../resource/China_cn/hangqing";
import jiaoyi from "../resource/China_cn/jiaoyi";
import c2c from "../resource/China_cn/c2c";
import compositeHome from "../resource/China_cn/compositeHome";
const cn = {
  ...compositeHome,
  ...login,
  message: {
    home,
    user,
    hangqing,
    jiaoyi,
    c2c,
  },
  ...cnLocale,
};

export default cn;
