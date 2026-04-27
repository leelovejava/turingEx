package com.yami.trading.admin.controller;


import com.yami.trading.bean.model.FileUploadParamsModel;
import com.yami.trading.bean.vo.FileInfoVo;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Api(tags ="文件上传")
@RequestMapping()
public class UploadFileController {

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;


    @PostMapping(value = "/api/uploadFile")
    @ApiOperation("文件上传")
    public Result<FileInfoVo> uploadFile(FileUploadParamsModel model) {
        try {
            if (model.getFile().getSize() / 1000L > 4500) {
               throw  new YamiShopBindException("图片大小不能超过4M");
            }
            if (StringUtils.isEmptyString(model.getModuleName())){
                model.setModuleName("common");
            }
            String path=  awsS3OSSFileService.uploadFile(model.getModuleName(), model.getFile());
            FileInfoVo fileInfoVo=new FileInfoVo();
             fileInfoVo.setHttpUrl(awsS3OSSFileService.getUrl(path));
             fileInfoVo.setPath(path);
            return Result.ok(fileInfoVo);
        }
         catch (Exception e) {
            throw  new YamiShopBindException(e.getMessage());
        }
    }
}
