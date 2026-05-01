/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.trading.security.common.manager;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yami.trading.common.exception.YamiShopBindException;

import cn.hutool.crypto.symmetric.AES;

/**
 * @author 菠萝凤梨
 * @description AES编解码管理
 */
@Component
public class PasswordManager {
	/**
     * AES对称加密秘钥
     */
    @Value("${auth.password.signKey:-mall4j-password}")
    private String passwordSignKey;
    
    /**
     * 日志组件
     */
    private static final Logger logger = LoggerFactory.getLogger(PasswordManager.class);

    /**
     * 解密BASE64编码为明文密码
     * @param data BASE64编码
     * @return 明文密码
     */
    public String decryptPassword(String data) {
        AES aes = new AES(passwordSignKey.getBytes(StandardCharsets.UTF_8));
        try {
            return aes.decryptStr(data).substring(13);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw new YamiShopBindException("AES解密错误", e);
        }
    }
    
    /**
     * 加密明文密码为BASE64密文密码
     * @param data 明文密码
     * @return 密文密码
     */
    public String encryptPassword(String data) {
    	 AES aes = new AES(passwordSignKey.getBytes(StandardCharsets.UTF_8));
         try {
        	 return aes.encryptBase64(System.currentTimeMillis()+data);
         } catch (Exception e) {
             logger.error("Exception:", e);
             throw new YamiShopBindException("AES加密错误", e);
         }
    }
}
