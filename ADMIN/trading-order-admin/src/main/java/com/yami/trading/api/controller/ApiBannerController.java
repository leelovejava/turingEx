package com.yami.trading.api.controller;

import com.yami.trading.bean.cms.Banner;
import com.yami.trading.common.domain.Result;
import com.yami.trading.service.cms.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 横幅管理
 */
@RestController
@CrossOrigin
public class ApiBannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取 横幅
     * language 语言
     * content_code 业务代码，同种内容不同语言下的code相同
     */
    @RequestMapping("/api/banner!get.action")
    public Object get(HttpServletRequest request) {
        String language = request.getParameter("language");
        String content_code = request.getParameter("content_code");
        Banner banner = this.bannerService.getByCodeAndLanguage(content_code, language);
        return Result.succeed(bannerService.bindOne(banner));
    }

    /**
     * 获取 横幅 列表
     */
    @RequestMapping("/api/banner!list.action")
    public Object list(HttpServletRequest request) {
        String model = request.getParameter("model");
        String language = request.getParameter("language");
        List<Map<String, Object>> result = new ArrayList<>();
        List<Banner> cacheListByModel = this.bannerService.listByModelAndLanguage(model, language);
        for (Banner banner : cacheListByModel) {
            if (banner.getOnShow()==1){
                result.add(this.bannerService.bindOne(banner));
            }
        }
        return Result.succeed(result);
    }
}
