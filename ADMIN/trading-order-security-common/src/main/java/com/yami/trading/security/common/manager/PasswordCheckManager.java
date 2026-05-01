package com.yami.trading.security.common.manager;

import cn.hutool.core.util.StrUtil;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.security.common.enums.SysTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordCheckManager {
	
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 连续错误10次后将限制30分钟后才能再登录
     */
    private static final int TIMES_CHECK_INPUT_PASSWORD_NUM = 10;

    /**
     * 检查用户输入错误的验证码次数
     */
    private static final String CHECK_VALID_CODE_NUM_PREFIX = "checkUserInputErrorPassword_";
    
    public void checkPassword(SysTypeEnum sysTypeEnum,String userNameOrMobile, String rawPassword, String encodedPassword,String language) {
        String inputTimesExpireKey = sysTypeEnum.value() + CHECK_VALID_CODE_NUM_PREFIX + IPHelper.getIpAddr()+userNameOrMobile;

        int count = 0;
        if(RedisUtil.hasKey(inputTimesExpireKey)){
            count = RedisUtil.get(inputTimesExpireKey);
        }
        
        if(count > TIMES_CHECK_INPUT_PASSWORD_NUM){
            if(language.equals("en")){
                throw new YamiShopBindException("Password input error ten times, login restricted for 30 minutes");
            }
            throw new YamiShopBindException("密码输入错误十次,已限制登录30分钟");
        }
        
        if (StrUtil.isBlank(encodedPassword) || !passwordEncoder.matches(rawPassword,encodedPassword)){
            RedisUtil.set(inputTimesExpireKey,++count,1800);
            if(language.equals("en")){
                throw new YamiShopBindException("The password is incorrect!");
            }
            throw new YamiShopBindException("密码不正确");
        }
    }
}