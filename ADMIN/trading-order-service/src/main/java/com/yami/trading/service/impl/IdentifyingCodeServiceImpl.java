package com.yami.trading.service.impl;

import com.yami.trading.bean.log.domain.CodeLog;
import com.yami.trading.service.EmailSendService;
import com.yami.trading.service.IdentifyingCodeService;
import com.yami.trading.service.IdentifyingCodeTimeWindowService;
import com.yami.trading.service.SmsSendService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.CodeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdentifyingCodeServiceImpl implements IdentifyingCodeService {

    private Logger logger = LoggerFactory.getLogger(IdentifyingCodeServiceImpl.class);

    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private EmailSendService emailSendService;

    @Autowired
    private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;

    @Autowired
    private SysparaService sysparaService;

    @Autowired
    private CodeLogService codeLogService;

    private Map<String, Integer> ipCache = new ConcurrentHashMap<String, Integer>();

    @Override
    public void send(String target, String ip, String userId) {

        String code;
        /**
         * 短信发送签名
         */
//		String smsbao_sign = sysparaService.find("smsbao_sign").getValue();
        //短信发送
        if (!target.contains("@")) {

            // 短信发送文本[TEST]code is ：{0}
            String send_code_text = this.sysparaService.find("send_code_text").getSvalue();
            if (null == send_code_text || (send_code_text = send_code_text.trim()).isEmpty()) {
                logger.error("send_code_text 未配置");
                return;
            }

            //是否每次发送的code都不一样
            boolean send_code_always_new = this.sysparaService.find("send_code_always_new").getBoolean();

            Object object = this.identifyingCodeTimeWindowService.getAuthCode(target);
            if (object == null || send_code_always_new) {
                Random random = new Random();
                code = String.valueOf(random.nextInt(999999) % 900000 + 100000);
            } else {
                code = String.valueOf(object);
            }

            /**
             * 发送的短信接口类型 tiantian---天天---smsSendService--->>>>--
             * moduyun---摩杜云---smsSingleSender
             */
            String send_code_type = this.sysparaService.find("send_code_type").getSvalue();
            if (null == send_code_type || (send_code_type = send_code_type.trim()).isEmpty()) {
                logger.error("send_code_type 未配置");
                return;
            }

            if ("tiantian".equals(send_code_type)) {
                smsSendService.send(target, MessageFormat.format(send_code_text, code));
                logger.info(MessageFormat.format("tiangtian--target:{0},code:{1},ip:{2}", target, code, ip));
            }

//			else if ("moduyun".equals(send_code_type)) {
//				// -- 摩杜云短信签名的Id--accesskey,secretkey,signId,templateId
//				String send_code_moduyun = this.sysparaService.find("send_code_moduyun").getValue();
//				String[] send_code_moduyun_parts = send_code_moduyun.split(",");
//				List<String> params = new ArrayList<String>();
//				params.add(code);
//				String strh_code = "";
//				strh_code = target.substring(0, 2);
//				if ("86".equals(strh_code)) {
//					try {
//						smsSingleSender.send(0, "86", target.substring(2, target.length()), send_code_moduyun_parts[2],
//								send_code_moduyun_parts[3], params, "", send_code_moduyun_parts[0],
//								send_code_moduyun_parts[1]);
//						log.info(MessageFormat.format("moduyun--target:{0},code:{1},ip:{2}", target, code, ip));
//					} catch (Exception e) {
//					}
//				}
//			}
            else if ("smsbao".equals(send_code_type)) {
                smsSendService.send(target, MessageFormat.format(send_code_text, code));
                logger.info(MessageFormat.format("smsbao--target:{0},code:{1},ip:{2}", target, code, ip));
            }

        } else {

            //邮件发送
            String send_code_text_content = this.sysparaService.find("send_code_text_content").getSvalue();
            if (null == send_code_text_content || (send_code_text_content = send_code_text_content.trim()).isEmpty()) {
                logger.error("send_code_text_content 未配置");
                return;
            }
            //是否每次发送的code都不一样
            boolean send_code_always_new = this.sysparaService.find("send_code_always_new").getBoolean();

            Object object = this.identifyingCodeTimeWindowService.getAuthCode(target);
            if (object == null || send_code_always_new) {
                Random random = new Random();
                code = String.valueOf(random.nextInt(999999) % 900000 + 100000);
            } else {
                code = String.valueOf(object);
            }

            String content = MessageFormat.format("code is ：{0}", code);
            String send_content_text = this.sysparaService.find("send_content_text").getSvalue();
            if (0 != (send_content_text = send_content_text.trim()).length()) {
                content = MessageFormat.format(send_content_text, code);
            }

            emailSendService.sendEmail(target, MessageFormat.format(send_code_text_content, code), content);
            logger.info(MessageFormat.format("email--target:{0},code:{1},ip:{2}", target, code, ip));
        }

        this.identifyingCodeTimeWindowService.putAuthCode(target, code);
        CodeLog codeLog = new CodeLog();
        codeLog.setTarget(target);
        codeLog.setUserId(userId);
        codeLog.setLog("发送地址：" + target + ",验证码：" + code + ",ip地址：" + ip);
        codeLog.setCreateTime(new Date());
        codeLogService.save(codeLog);
    }

}
