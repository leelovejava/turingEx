package com.yami.trading.admin.task.cms;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yami.trading.admin.facade.MachineTranslationService;
import com.yami.trading.bean.item.domain.ItemSummary;
import com.yami.trading.common.config.ThreadPool;
import com.yami.trading.service.item.ItemSummaryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemSummaryTranslate {

    @Autowired
    private ItemSummaryService itemSummaryService;
    
    @Autowired
    private MachineTranslationService translationService;
    
    private ThreadPoolTaskExecutor taskExecutor=ThreadPool.getApplicationThreadPool();
    
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public ItemSummary translateChineseFields(ItemSummary item) throws IllegalAccessException {
        if (item == null) {
            throw new IllegalArgumentException("ItemSummary cannot be null");
        }

        ItemSummary enItemSummary = itemSummaryService.getBaseMapper().selectOne(new LambdaQueryWrapper<ItemSummary>().eq(ItemSummary::getSymbol, item.getSymbol()).eq(ItemSummary::getLang, "en"));
        if (enItemSummary == null) {
            enItemSummary = new ItemSummary();
        }
        BeanUtil.copyProperties(item, enItemSummary, "lang", "uuid");
        // 遍历类中的所有字段
        for (Field field : ItemSummary.class.getDeclaredFields()) {
            field.setAccessible(true);  // 使得private字段可以访问
            Object fieldValue = field.get(item);  // 获取字段的值
            if (fieldValue == null) {
                continue;
            }
            // 只处理字符串类型的字段
            if (fieldValue instanceof String) {
                String value = (String) fieldValue;
                // 如果字段不为空并且含有中文字符，则进行翻译
                if (CHINESE_PATTERN.matcher(value).find()) {
                    String translated = translationService.translate(value);  // 调用TranslateAPI进行翻译
                    field.set(enItemSummary, translated);  // 将翻译后的结果设置回字段
                }
            }
        }
        return enItemSummary;

    }

    @Scheduled(cron = "0 */5 * ? * *")
    public void translate() {
        QueryWrapper<ItemSummary> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("translate");
        queryWrapper.eq("lang", "zh-CN");
        List<ItemSummary> list = itemSummaryService.list(queryWrapper);
        for (ItemSummary itemSummary : list) {
        	taskExecutor.execute(() -> {
                try {
                    ItemSummary itemSummaryEn = translateChineseFields(itemSummary);
                    itemSummaryEn.setSymbol(itemSummary.getSymbol());
//                    itemSummaryEn.setUuid(null);
                    itemSummaryEn.setLang("en");
                    itemSummary.setTranslate("1");
                    itemSummaryService.saveOrUpdate(itemSummaryEn);
                    itemSummaryService.updateById(itemSummary);
                } catch (Exception e) {
                    log.error("翻译简况失败: {}", itemSummary.getSymbol(), e);
                }
            });
        }
    }
}
