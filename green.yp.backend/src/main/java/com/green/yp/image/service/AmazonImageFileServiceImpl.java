package com.green.yp.image.service;

import com.green.yp.exception.SystemException;
import com.green.yp.image.AbstractImageFileService;
import com.green.yp.image.ImageFileService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@ConditionalOnProperty(name = "greenyp.image.service.impl", havingValue = "aws")
public class AmazonImageFileServiceImpl extends AbstractImageFileService
    implements ImageFileService {
  private static final String S3_PATH_SEPARATOR = "/";

  @Value("${greenyp.image.service.bucket}")
  private String s3BucketName;

  @Value("${greenyp.image.service.host:}")
  private String imageHostname;

  private final S3Client s3Client;

  public AmazonImageFileServiceImpl(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  public String pathSeparator() {
    return S3_PATH_SEPARATOR;
  }

  @Override
  protected String saveImageFile(MultipartFile multipartFile, String pathString, String fileName) {
    String key = createKey(pathString, fileName);
    try {
      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(s3BucketName)
              .key(key)
              .acl("public-read") // Required if using S3 public objects
              .contentType(multipartFile.getContentType())
              .build(),
          RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
      return createUrl(imageHostname, key);
    } catch (IOException | S3Exception e) {
      log.error("Failed to upload image to S3", e);
      throw new SystemException("Unexpected error uploading image to S3", e);
    }
  }

  @Override
  protected void deleteFile(String pathKey, String fileName) {
    String key = (fileName == null) ? pathKey : createKey(pathKey, fileName);
    try {
      s3Client.deleteObject(DeleteObjectRequest.builder().bucket(s3BucketName).key(key).build());
    } catch (S3Exception e) {
      log.warn("Failed to delete object from S3: {}", key, e);
      throw new SystemException("Unexpected error deleting image from S3", e);
    }
  }

  private String createKey(String path, String fileName) {
    return path.endsWith(S3_PATH_SEPARATOR) ? path + fileName : path + S3_PATH_SEPARATOR + fileName;
  }
}
