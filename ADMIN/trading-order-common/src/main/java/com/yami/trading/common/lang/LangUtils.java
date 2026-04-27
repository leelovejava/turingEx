package com.yami.trading.common.lang;

import com.yami.trading.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class LangUtils {
    public static String getLang() {
        String lang = "en";
        try {
            HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
            if (request == null) {
                return lang;
            }
            String langParam = request.getParameter("language");
            if (StringUtils.isNotEmpty(langParam)) {
                return langParam;
            }
        } catch (Exception e) {
            log.warn("获取当前语言失败", e);
        }
        return lang;

    }

    /**
     * 非中文的情况下，币对硬英文名
     *
     * @return
     */
    public static boolean isEnItem() {
        return !"zh-CN".equalsIgnoreCase(getLang());
    }

    public static boolean isCnItem() {
        return "zh-CN".equalsIgnoreCase(getLang());
    }

    public static boolean isTWItem() {
        return "CN".equalsIgnoreCase(getLang());
    }

    public static boolean hasLang() {
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        if (request == null) {
            return false;
        }
        String langParam = request.getParameter("language");
        return StringUtils.isNotEmpty(langParam);
    }
}
