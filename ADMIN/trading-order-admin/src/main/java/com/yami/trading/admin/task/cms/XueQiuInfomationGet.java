package com.yami.trading.admin.task.cms;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yami.trading.admin.facade.MachineTranslationService;
import com.yami.trading.bean.cms.Infomation;
import com.yami.trading.common.config.ThreadPool;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.cms.InfomationService;

import cn.hutool.core.bean.BeanUtil;

@Slf4j
@Component
public class XueQiuInfomationGet {
    
    @Autowired
    private InfomationService infomationService;

    @Autowired
    private MachineTranslationService translationService;
    
    private ThreadPoolTaskExecutor taskExecutor=ThreadPool.getApplicationThreadPool();

//    @Scheduled(cron = "0 0/30 * ? * *")
//    public void crawl(){
//        infomationService.getInformation();
//    }

    @Scheduled(cron = "0 */5 * ? * *")
    public void translate(){
        QueryWrapper<Infomation> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("translate");
        queryWrapper.eq("lang", "zh-CN");
        List<Infomation> list = infomationService.list(queryWrapper);
        for(Infomation infomation : list){
                try{
                    String description = infomation.getDescription();
                    String source = infomation.getSource();
                    Infomation infomationEn = BeanUtil.copyProperties(infomation, Infomation.class);
                    infomationEn.setUuid(null);
                    if(StringUtils.isNotEmpty(description)){
                        String translate = translationService.translate(description);
                        if(translate == null){
                            return;
                        }
                        infomationEn.setDescription(translate);
                    }
                    if(StringUtils.isNotEmpty(source)){
                        String translate = translationService.translate(source);
                        if(translate == null){
                            return;
                        }
                        infomationEn.setSource(translate);
                    }
                    infomationEn.setLang("en");
                    infomationService.save(infomationEn);
                    infomation.setTranslate("1");
                    infomationService.updateById(infomation);
                }catch (Exception e){
                    log.info("翻译报错", e);
                }
        }

    }
}
