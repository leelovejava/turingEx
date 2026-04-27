package com.yami.trading.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 手动获取bean的工具类
 */
@Component
// 关闭延迟加载
@Lazy(value = false)
public class SpringBeanUtil implements BeanFactoryPostProcessor, ApplicationContextAware, DisposableBean {

    private final static Logger logger = LoggerFactory.getLogger(SpringBeanUtil.class);

    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext = null;

    private static Environment environment;
    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * BeanFactoryPostProcessor, 注入Context到静态变量中.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        SpringBeanUtil.beanFactory = factory;
    }

    /**
     * 实现ApplicationContextAware接口, 注入Context到静态变量中.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanUtil.applicationContext = applicationContext;
        environment = applicationContext.getEnvironment();
    }

    public static ListableBeanFactory getBeanFactory() {
        return null == beanFactory ? applicationContext : beanFactory;
    }

    /**
     * 从静态变量applicationContext中取得Bean, Map<Bean名称，实现类></>
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     * 如果对应的 bean 不存在，此处会抛出异常
     */
    public static <T> T getBean(Class<T> requiredType) {
        if (applicationContext == null) {
            logger.error("applicationContext is null");
            throw new RuntimeException("SpringContextHolder，你可以在引用Bean内Autowire该Bean");
        }

        return (T) (getBeanFactory().getBean(requiredType));
    }

    public static void setEnv(Environment env) {
        if (env != null) {
            environment = env;
        }
    }

    public static Environment getEnv() {
        return environment;
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext为Null.
     */
    public static void clearHolder() {
        if (logger.isDebugEnabled()) {
            logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        }
        applicationContext = null;
    }

    /**
     * 发布事件
     * @param event
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        applicationContext.publishEvent(event);
    }

    /**
     *  动态注入bean
     *  AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(User.class).getBeanDefinition();
     *         registry.registerBeanDefinition("user", beanDefinition);
     * @param requiredType 注入类
     * @param beanName bean名称
     */
    public static Object registerBean(Class<?> requiredType, String beanName) {
        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

        //获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();

        //创建bean信息.
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(requiredType);

        //动态注册bean.
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());

        //获取动态注册的bean.
        return configurableApplicationContext.getBean(requiredType);
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() {
        SpringBeanUtil.clearHolder();
    }

}