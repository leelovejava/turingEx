package com.yami.trading.service.syspara;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.*;
import com.alicp.jetcache.template.QuickConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.yami.trading.bean.syspara.dto.SysparasDto;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.dao.syspara.SysparaMapper;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 配置参数Service
 *
 * @author lucas
 * @version 2023-03-17
 */
@Service
@Transactional
public class SysparaService extends ServiceImpl<SysparaMapper, Syspara> {

//    // 在jetcache 2.7 版本CreateCache注解已经废弃，请改用CacheManager.getOrCreateCache(QuickConfig)
//    @CreateCache(name = "sysParaCache:", expire = 600, localExpire = 30, cacheType = CacheType.BOTH)
//    private Cache<String, Syspara> sysParaCache;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    @Lazy // 用于支持内部调用时能使用内部其他方法的 AOP 切片注解
    private SysparaService proxySysparaService;

    private Cache<String, Syspara> sysParaCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("sysParaCache:")
                .expire(Duration.ofSeconds(600))
                .cacheType(CacheType.BOTH) // two level cache
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        sysParaCache = cacheManager.getOrCreateCache(qc);
    }

    public Cache<String, Syspara> getSysParaCache() {
        return this.sysParaCache;
    }

    /**
     * 通过code 找对象，todo cache
     * 缓存框架使用，参考资料：
     *   https://blog.csdn.net/Alice_qixin/article/details/130621612
     *   https://blog.csdn.net/qq_30756815/article/details/131227854
     *   https://blog.csdn.net/llg___/article/details/133409739
     *   https://blog.51cto.com/no8g/6344263
     *   https://github.com/alibaba/jetcache/blob/master/docs/CN/Embedded.md
     *   https://github.com/alibaba/jetcache
     *
     * @param code
     * @return
     */
    @Cached(name = "sysParaCache:", key = "#code", localExpire = 60, expire = 600, timeUnit = TimeUnit.SECONDS,
            cacheType = CacheType.BOTH)
    //  当缓存访问未命中的情况下，对并发进行的加载行为进行保护。 目前只支持当 前应用内的保护，即同一个JVM中同一个key只有一个线程去加载，其它线程等待结果。
    @CachePenetrationProtect(timeout = 1)
    public Syspara find(String code) {
        log.trace("-------> SysparaService.find 真实调用...");
        LambdaQueryWrapper<Syspara> queryWrapper = new LambdaQueryWrapper<Syspara>()
                .eq(Syspara::getCode, code)
                .last("LIMIT 1");
        return super.baseMapper.selectOne(queryWrapper);
    }

    @Transactional(readOnly = false)
    //@CacheUpdate(name = "sysParaCache:", key = "#dto.id", value = "#dto")
    public void updateSysparas(SysparasDto dto) {
        if (dto == null) {
            return;
        }
        Map<String, Object> map = BeanUtil.beanToMap(dto, false, true);
        List<Syspara> updates = map.keySet().stream().map(key -> {
            Syspara syspara = new Syspara();
            syspara.setCode(key);
            syspara.setSvalue(map.get(key).toString());
            return syspara;
        }).collect(Collectors.toList());

        baseMapper.updateBatch(updates);

        sysParaCache.removeAll(map.keySet());
        for (Syspara onePara : updates) {
            // 需要切片的 exposeProxy 设置为 true 才可用，否则报错
            //((SysparaService)AopContext.currentProxy()).find(onePara.getCode());
            proxySysparaService.find(onePara.getCode());
        }
    }
}
