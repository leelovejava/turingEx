package com.yami.trading.api.controller;

import com.yami.trading.api.dto.DemoTimeConvertData;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.bean.syspara.dto.SysparasDto;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.Json;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
@Slf4j
public class HtmlPageController {

    @GetMapping("/html/404")
    // @GetMapping(value = "pageUrl", produces = MediaType.TEXT_HTML_VALUE) 不使用模板引擎的方式，返回内容即网页内容
    public String timeConvert() {
        return "/html/404.html";
    }


}
