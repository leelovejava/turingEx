package com.yami.trading.api.controller;

import com.yami.trading.bean.model.FileUploadParamsModel;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.service.AwsS3OSSFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Api(tags ="文件上传")
@RequestMapping("api")
public class ApiUploadFileController {

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    @PostMapping(value = "/api/uploadFile")
    @ApiOperation("文件上传")
    public Result uploadFile(FileUploadParamsModel model) {
        try {
            if (model.getFile().getSize() / 1000L > 4500) {
               throw  new YamiShopBindException("图片大小不能超过4M");
            }
            String path =  awsS3OSSFileService.uploadFile(model.getModuleName(), model.getFile());
            return Result.succeed(path);
        }
         catch (Exception e) {
            throw  new YamiShopBindException(e.getMessage());
        }
    }
}
