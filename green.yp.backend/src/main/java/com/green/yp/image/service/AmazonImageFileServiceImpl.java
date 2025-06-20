package com.green.yp.image.service;

import com.green.yp.image.AbstractImageFileService;
import com.green.yp.image.ImageFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "greenyp.image.service.impl", havingValue = "aws")
public class AmazonImageFileServiceImpl extends AbstractImageFileService implements ImageFileService {
    private static final String S3_PATH_SEPARATOR = "/";

    @Value("${greenyp.image.service.bucket}")
    private String s3BucketName;

    @Value("${greenyp.image.service.image.host:}")
    private String imageHostname;

    public String pathSeparator(){
        return S3_PATH_SEPARATOR;
    }

    @Override
    protected String saveImageFile(MultipartFile multipartFile, String pathString, String fileName) {
        return "";
    }

    @Override
    protected void deleteFile(String pathKey, String fileName) {
    }
}
