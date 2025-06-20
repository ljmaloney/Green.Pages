package com.green.yp.image;

import java.io.File;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public interface ImageFileService {
  void deleteLogo(UUID producerId);

  void deleteImage(UUID producerId, String imageFilename);

  String saveLogo(UUID producerId, String logoFilename, MultipartFile logoFile);

  String saveImage(UUID producerId, String imageFilename, MultipartFile imageFile);

  /**
   * Specifies path separator for construction of path(s) or keys. By default this is the
   * File.separator for the OS. Override this for S3 or GCP buckets
   *
   * @return
   */
  default String pathSeparator() {
    return File.separator;
  }

  default String createFileKeyPath(
      String folder, UUID producerId, String subFolder, String fileName) {
    if (StringUtils.isEmpty(fileName)) {
      return String.join(pathSeparator(), folder, producerId.toString(), subFolder);
    }
    return String.join(pathSeparator(), folder, producerId.toString(), subFolder, fileName);
  }

  default String createUrl(String hostname, String fileKeyPath) {
    return String.format("%s/%s", hostname, fileKeyPath);
  }
}
