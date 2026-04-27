package com.yami.trading.api.controller;

import com.yami.trading.api.dto.DemoTimeConvertData;
import com.yami.trading.bean.item.domain.Item;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.bean.syspara.dto.SysparasDto;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.IpUtil;
import com.yami.trading.common.util.Json;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.service.item.ItemService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
public class TimeZoneController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysparaService sysparaService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @GetMapping("/api/demo/checkip")
    public Result checkIp() {
        String clientIp = IpUtil.getRequestIp(null);

        Map<String, Object> retData = new HashMap<>();
        retData.put("clientIp", clientIp);

        return Result.ok(retData);
    }

    @GetMapping("/api/demo/timeConvert")
    public Result<DemoTimeConvertData> timeConvert(@RequestParam long userId) {

        DemoTimeConvertData data = new DemoTimeConvertData();
        Date now = new Date();
        data.setFromTime(now);

        if (userId > 0) {
            SysUser sysUser = sysUserService.getSysUserById(userId);
            if (sysUser != null) {
                data.setUserTime(sysUser.getUpdateTime());
            }
        }

        return Result.ok(data);
    }

    @PostMapping("/api/demo/timeConvert2")
    public Result<DemoTimeConvertData> timeConvert2(@RequestBody DemoTimeConvertData info) {
        log.info("----> request param:" + Json.toJsonString(info));

        DemoTimeConvertData data = new DemoTimeConvertData();
        Date now = new Date();
        data.setFromTime(now);
        data.setKeyword(info.getKeyword());

//        SysUser demoUser = new SysUser();
//        demoUser.setUserId(System.currentTimeMillis());
//        //demoUser.setCreateTime(now);
//        //demoUser.setUpdateTime(now);
//        demoUser.setEmail(demoUser.getUserId() + "@qq.com");
//        demoUser.setGoogleAuthBind(false);
//        demoUser.setGoogleAuthSecret("");
//        demoUser.setPassword("123456");
//        demoUser.setShopId(1L);
//        demoUser.setStatus(1);
//        demoUser.setUsername("mock-" + demoUser.getUserId());
//        sysUserService.save(demoUser);
//
//        SysUser sysUser = sysUserService.getSysUserById(demoUser.getUserId());
//        if (sysUser != null) {
//            data.setUserTime(sysUser.getCreateTime());
//        }

        if (info.getUserId() != null && info.getUserId() > 0) {
            SysUser sysUser = sysUserService.getSysUserById(info.getUserId());
            if (sysUser != null) {
                sysUserService.demoUpdateTime(sysUser.getUserId());
                sysUser = sysUserService.getSysUserById(sysUser.getUserId());
                data.setUserTime(sysUser.getUpdateTime());
            }
        }

        return Result.ok(data);
    }

    @GetMapping("/api/demo/cache")
    public Result<Syspara> testParaCache(@RequestParam String code) {
        Syspara syspara = sysparaService.find(code);
        String cacheKey = "zset_demo";

        redisTemplate.opsForZSet().add(cacheKey, "k1", 1);
        redisTemplate.opsForZSet().add(cacheKey, "k2", 2);
        redisTemplate.opsForZSet().add(cacheKey, "k3", 3);
        redisTemplate.opsForZSet().add(cacheKey, "k4", 4);
        redisTemplate.opsForZSet().add(cacheKey, "k5", 5);
        redisTemplate.opsForZSet().add(cacheKey, "k6", 6);

        Set<ZSetOperations.TypedTuple<String>> klineInfoList = redisTemplate.opsForZSet().reverseRangeWithScores(cacheKey, 0, 100);
        for (ZSetOperations.TypedTuple<String> oneElement : klineInfoList) {
            log.info("------> key : {}  ,  value : {}", oneElement.getValue(), oneElement.getScore());
        }

        syspara.setNotes(syspara.getNotes() + "---  v2");
        return Result.ok(syspara);
    }

    @PostMapping("/api/demo/updateCache")
    public Result updateCache(@RequestBody SysparasDto dto) {
        sysparaService.updateSysparas(dto);

        return Result.succeed();
    }

    @GetMapping("/api/demo/jetcache")
    public Result<Syspara> getJetCache() {
        for (int i = 0; i < 100; i++) {
            List<Item> itemList = new ArrayList<>(itemService.list());
            for (Item oneItem : itemList) {
                log.info("-----> oneItem:" + oneItem);
                try {
                    Thread.sleep(500L);
                } catch (Exception e) {

                }
            }

            try {
                Thread.sleep(3000L);
            } catch (Exception e) {

            }
        }

        return Result.ok(null);
    }

    @PostMapping("/api/demo/updateJetCache")
    public Result updateJetCache(@RequestBody SysparasDto dto) {
        List<Item> allItemList = itemService.list();
//        List<String> itemIdList = new ArrayList<>();
//        for (Item oneItem : allItemList) {
//            itemIdList.add(oneItem.getUuid());
//        }
//
//        Random rdm = new Random();
//        int max = allItemList.size();

        itemService.saveOrUpdate(allItemList.get(1));

        return Result.succeed();
    }

    @GetMapping("/api/demo/cache2")
    public Result testParaCache2(@RequestParam String code) {
        Syspara syspara = sysparaService.find(code);

        String cacheKey = "mapdemo_para";
        redisTemplate.opsForHash().put(cacheKey, code, syspara);

        Object cacheValue = redisTemplate.opsForHash().get(cacheKey, code);

        return Result.ok(cacheValue);
    }

    @GetMapping("/api/demo/getUserById")
    public Result getUserById(@RequestParam(name = "userId") String userId) {
        User party = userService.getById(userId);
        log.info("---> UserServiceImpl.savePhone userId:{}, party:{}", userId, party);

        return Result.ok(party);
    }


}
