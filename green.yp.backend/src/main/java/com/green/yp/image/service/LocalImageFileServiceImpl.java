package com.green.yp.image.service;

import com.green.yp.exception.SystemException;
import com.green.yp.image.AbstractImageFileService;
import com.green.yp.image.ImageFileService;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "greenyp.image.service.impl",
    havingValue = "local",
    matchIfMissing = true)
public class LocalImageFileServiceImpl extends AbstractImageFileService
    implements ImageFileService {

  @Value("${greenyp.image.service.filepath}")
  private String fileSystemPath;

  @Value("${greenyp.image.service.host:}")
  private String imageHostname;

  protected String saveImageFile(MultipartFile multipartFile, String pathString, String fileName) {
    Path path = FileSystems.getDefault().getPath(pathString, fileName);
    try {
      Files.createDirectories(path.getParent());
      Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
      return createUrl(imageHostname, pathString);
    } catch (IOException e) {
      log.error("Unexpected error saving producer/subscriber logo", e);
      throw new SystemException("Unexpected error saving producer/subscriber logo", e);
    }
  }

  protected void deleteFile(String pathKey, String fileName) {
    Path path = FileSystems.getDefault().getPath(fileSystemPath, pathKey, fileName);
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      log.error("Unexpected error deleting / removing producer image", e);
      throw new SystemException("Unexpected error deleting / removing producer image", e);
    }
  }
}
