package com.yami.trading.api.controller;

import com.yami.trading.bean.cms.Cms;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateTimeTools;
import com.yami.trading.service.cms.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户端内容管理
 */
@RestController
@CrossOrigin
public class ApiCmsController {
    @Autowired
    private CmsService cmsService;

    /**
     * 获取 用户端内容管理
     */
    @RequestMapping("/api/cms!get.action")
    public Object get(HttpServletRequest request) {
        // 业务代码， 同种内容 不同语言下的code相同
        String content_code = request.getParameter("content_code");
        String language = request.getParameter("language");
        Cms cms = cmsService.getContentCodeAndLanguage(content_code, language);
        return Result.succeed(cms);
    }

    /**
     * 获取 用户端内容管理 列表
     */
    @RequestMapping("/api/cms!list.action")
    public Object list(HttpServletRequest request) {
        String model = request.getParameter("model");
        String language = request.getParameter("language");
        List<Cms> cacheListByModel = this.cmsService.getModelAndLanguage(model, language);
        for (Cms cms : cacheListByModel) {
            cms.setCreateTimeStr(cms.getCreateTime());
        }

        return Result.succeed(cacheListByModel);
    }
}
