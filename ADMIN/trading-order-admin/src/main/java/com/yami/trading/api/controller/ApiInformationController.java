package com.yami.trading.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.hankcs.hanlp.HanLP;
import com.yami.trading.bean.cms.Infomation;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.cms.InfomationService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.extra.pinyin.PinyinUtil.isChinese;
import static java.util.regex.Pattern.*;

@RestController
@CrossOrigin
@Slf4j
public class ApiInformationController {
    @Autowired
    private InfomationService infomationService;

    @ApiOperation("查看实时咨询数据")
    @GetMapping("/api/information!list.action")
    public Result<List<Infomation>> list(@RequestParam(required = false) Integer limit,
                                         @RequestParam(required = false) String language,
                                         @RequestParam(required = false) String maxTime,
                                         @RequestParam(required = false) String lang,
                                         @RequestParam(required = false) String type) {
        Boolean translate = Boolean.FALSE;
        if (StringUtils.isEmptyString(language)) {
            language = lang;
        }
        if (StringUtils.isEmptyString(language)) {
            language = "en";
        }
        if ("CN".equalsIgnoreCase(language)) {
            language = "zh-CN";
            translate = Boolean.TRUE;
        } else if (!"zh-CN".equalsIgnoreCase(language)) {
            language = "en";
        }

        if (limit == null) {
            limit = 50;
        }
        if (limit >= 200) {
            limit = 200;
        }
        if (StringUtils.isEmptyString(maxTime)) {
            maxTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        }
        //最多查询一周之内的
        LocalDateTime maxDate = LocalDateTime.parse(maxTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusDays(7);
        if (threshold.compareTo(maxDate) > 0) {
            return Result.ok(new ArrayList<>());
        }

        QueryWrapper<Infomation> infomationQueryWrapper = new QueryWrapper<>();
        infomationQueryWrapper.eq("lang", language);
        infomationQueryWrapper.eq(StringUtils.isNotEmpty(type), "type", type);
        infomationQueryWrapper.lt(StringUtils.isNotEmpty(maxTime), "created_at", maxTime);
        infomationQueryWrapper.orderByDesc("created_At");
        infomationQueryWrapper.last("LIMIT " + limit);
        List<Infomation> list = infomationService.list(infomationQueryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            if (translate) {
                for (Infomation infomation : list) {
                    infomation.setDescription(HanLP.convertToTraditionalChinese(infomation.getDescription()));
                }
            }
        }
        if (!"zh-CN".equals(language)) {
            if (CollectionUtils.isNotEmpty(list)) {
                for (Infomation Infomation : list) {
                    Infomation.setDescription(filterChinese(Infomation.getDescription()));
                }
            }
        }

        return Result.ok(list);
    }

    private static String filterChinese(String str) {
        // 用于返回结果
        String result = str;
        boolean flag = isContainChinese(str);
        if (flag) {// 包含中文
            // 用于拼接过滤中文后的字符
            StringBuffer sb = new StringBuffer();
            // 用于校验是否为中文
            boolean flag2 = false;
            // 用于临时存储单字符
            char chinese = 0;
            // 5.去除掉文件名中的中文
            // 将字符串转换成char[]
            char[] charArray = str.toCharArray();
            // 过滤到中文及中文字符
            for (int i = 0; i < charArray.length; i++) {
                chinese = charArray[i];
                flag2 = isChinese(chinese);
                if (!flag2) {// 不是中日韩文字及标点符号
                    sb.append(chinese);
                }
            }
            result = sb.toString();
        }
        return result;
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    private static boolean isContainChinese(String str) {
        Pattern pattern = compile("[\u4e00-\u9fa5]");
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

}
