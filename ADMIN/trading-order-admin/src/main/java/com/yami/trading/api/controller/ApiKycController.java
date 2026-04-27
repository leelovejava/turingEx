package com.yami.trading.api.controller;

import com.yami.trading.bean.model.RealNameAuthRecord;
import com.yami.trading.common.domain.Result;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.RealNameAuthRecordService;
import com.yami.trading.service.system.TipService;
import com.yami.trading.service.user.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Data
@RestController
@CrossOrigin
public class ApiKycController {
    @Autowired
    private RealNameAuthRecordService realNameAuthRecordService;
    @Autowired
    private TipService tipService;
    @Autowired
    private UserService partyService;
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    /**
     * 获取实名认证信息
     */
    @RequestMapping("/api/kyc!get.action")
    public Result get() {
            String partyId = SecurityUtils.getCurrentUserId();
            RealNameAuthRecord kyc = realNameAuthRecordService.getByUserId(partyId);
        if (kyc == null) {
            return Result.succeed(null);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("partyId", kyc.getUserId());
        map.put("idname", kyc.getIdName());
        map.put("idnumber", kyc.getIdNumber());
        map.put("name", kyc.getName());
        map.put("idimg_1_path", awsS3OSSFileService.getUrl(kyc.getIdFrontImg()));
        map.put("idimg_2_path", awsS3OSSFileService.getUrl(kyc.getIdBackImg()));
        map.put("idimg_3_path", awsS3OSSFileService.getUrl(kyc.getHandheldPhoto()));
        map.put("idimg_1", kyc.getIdFrontImg());
        map.put("idimg_2", kyc.getIdBackImg());
        map.put("idimg_3", kyc.getHandheldPhoto());
        map.put("status", kyc.getStatus());
        map.put("msg", kyc.getMsg());
        map.put("nationality", kyc.getNationality());
        map.put("apply_time", kyc.getCreateTime());
        map.put("operation_time", kyc.getOperationTime());
        return Result.succeed(map);
    }

}
