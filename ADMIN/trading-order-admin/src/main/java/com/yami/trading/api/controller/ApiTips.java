package com.yami.trading.api.controller;

import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.service.system.TipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Slf4j
public class ApiTips {

    @Autowired
    TipService tipService;

    @RequestMapping("/api/tip/getRandomByType")
    public Object getRandomByType(@RequestParam String type) {
        ResultObject resultObject = new ResultObject();
        try {
            resultObject.setData(tipService.getRandom(type));
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

    @RequestMapping("/api/tip/getRandom")
    public Object list() {
        ResultObject resultObject = new ResultObject();
        try {
            resultObject.setData(tipService.getRandom());
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

}
