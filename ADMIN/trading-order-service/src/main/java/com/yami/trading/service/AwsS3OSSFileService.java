package com.yami.trading.service;

import org.springframework.web.multipart.MultipartFile;

public interface AwsS3OSSFileService {
    String uploadFile(String moduleName, MultipartFile file);

    public void createBucket(String bucketName);

    void setPolicy(String bucketName);

    String getUrl( String path);
}
