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

/**
 * 雪球资讯获取与翻译定时任务
 * 新闻采集的是英文，不需要翻译了
 * 本类负责以下功能：
 * 1. 从雪球网采集财经资讯新闻
 * 2. 将中文新闻翻译成英文
 *
 * 定时任务：
 * - translate(): 每5分钟执行，从数据库查询未翻译的中文新闻，
 *                调用翻译服务将 description 和 source 字段翻译成英文，
 *                并保存为新的英文记录
 *
 * 采集流程：
 * - crawl(): 从雪球API获取最新资讯（已注释掉，实际采集逻辑在 XueQiuDataServiceImpl）
 *
 * 翻译流程：
 * 1. 查询条件：lang="en" 且 translate IS NULL
 * 2. 遍历每条待翻译的新闻
 * 3. 使用 MachineTranslationService 翻译 description 和 source 字段
 * 4. 创建新的英文记录，lang 设为 "en"
 * 5. 标记原记录为已翻译（translate="1"）
 *
 * 注意：
 * - 翻译服务不可用时会中断执行（return）
 * - 每次循环遇到异常会记录日志但继续处理下一条
 */
@Slf4j
@Component
public class XueQiuInfomationGet {

    /**
     * 新闻服务，用于操作 Infomation 表
     * Infomation 表存储从雪球采集的新闻资讯
     */
    @Autowired
    private InfomationService infomationService;

    /**
     * 机器翻译服务，用于中译英
     * 调用外部翻译API将中文新闻内容翻译成英文
     */
    @Autowired
    private MachineTranslationService translationService;

    /**
     * 线程池，用于执行异步任务
     * 取自 ThreadPool 配置类
     */
    private ThreadPoolTaskExecutor taskExecutor = ThreadPool.getApplicationThreadPool();

    /**
     * 采集雪球资讯（已注释）
     *
     * 此方法原本用于从雪球API采集新闻资讯，每30分钟执行一次
     * 目前采集逻辑已移至 XueQiuDataServiceImpl.getInformation() 方法
     * 通过 InfomationService.getInformation() 调用
     *
     * 如需重新启用采集功能，取消注释即可
     */
//    @Scheduled(cron = "0 0/30 * ? * *")
//    public void crawl(){
//        infomationService.getInformation();
//    }

    /**
     * 翻译未翻译的新闻资讯
     *
     * 执行周期：每5分钟执行一次
     *
     * 功能说明：
     * - 从数据库查询所有 lang="en" 且 translate IS NULL 的新闻记录
     * - 对每条记录进行中译英处理
     * - 翻译完成后保存英文版本记录，并标记原记录为已翻译
     *
     * 翻译处理流程：
     * 1. 使用 BeanUtil.copyProperties 复制原新闻对象到新对象
     * 2. 新对象 uuid 设为 null（让数据库自动生成）
     * 3. 调用翻译服务翻译 description（新闻内容）
     * 4. 调用翻译服务翻译 source（新闻来源）
     * 5. 设置新记录 lang="en"（英文）
     * 6. 保存新记录到数据库
     * 7. 设置原记录 translate="1" 并更新
     *
     * 错误处理：
     * - 如果翻译服务返回 null，立即中断本次执行
     * - 如果发生异常，记录日志但继续处理下一条记录
     *
     * 数据表结构（Infomation）：
     * - uuid: 主键
     * - dataId: 雪球新闻ID
     * - description: 新闻内容/描述
     * - source: 新闻来源（如 "来源1"）
     * - createdAt: 创建时间
     * - type: 类型（1=新闻）
     * - originUrl: 原文链接
     * - lang: 语言（zh-CN=中文，en=英文）
     * - translate: 翻译标记（1=已翻译，null=未翻译）
     */
    ///@Scheduled(cron = "0 */5 * ? * *")
    public void translate() {
        // 构建查询条件：查询英文且未翻译的新闻
        QueryWrapper<Infomation> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("translate");
        queryWrapper.eq("lang", "en");
        List<Infomation> list = infomationService.list(queryWrapper);

        // 遍历每条待翻译的新闻
        for (Infomation infomation : list) {
            try {
                // 获取原新闻的内容和来源
                String description = infomation.getDescription();
                String source = infomation.getSource();

                // 复制原对象创建英文版本
                Infomation infomationEn = BeanUtil.copyProperties(infomation, Infomation.class);
                infomationEn.setUuid(null);

                // 翻译新闻内容（description）
                if (StringUtils.isNotEmpty(description)) {
                    String translate = translationService.translate(description);
                    // 翻译服务不可用时中断执行
                    if (translate == null) {
                        return;
                    }
                    infomationEn.setDescription(translate);
                }

                // 翻译新闻来源（source）
                if (StringUtils.isNotEmpty(source)) {
                    String translate = translationService.translate(source);
                    // 翻译服务不可用时中断执行
                    if (translate == null) {
                        return;
                    }
                    infomationEn.setSource(translate);
                }

                // 设置语言为英文并保存
                infomationEn.setLang("en");
                infomationService.save(infomationEn);

                // 标记原记录为已翻译
                infomation.setTranslate("1");
                infomationService.updateById(infomation);
            } catch (Exception e) {
                // 翻译出错记录日志，继续处理下一条
                log.info("翻译报错", e);
            }
        }

    }
}