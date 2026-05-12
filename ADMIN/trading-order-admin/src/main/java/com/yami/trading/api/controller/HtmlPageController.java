package com.yami.trading.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HtmlPageController {

    @GetMapping("/html/404")
    // @GetMapping(value = "pageUrl", produces = MediaType.TEXT_HTML_VALUE) 不使用模板引擎的方式，返回内容即网页内容
    public String timeConvert() {
        return "/html/404.html";
    }


}
