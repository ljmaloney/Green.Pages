package com.green.yp.image;

import java.io.File;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public interface ImageFileService {
  void deleteLogo(UUID producerId);
  default void deleteImage(UUID producerId, String imageFilename) {
    deleteImage(producerId, null, imageFilename);
  }
  void deleteImage(UUID producerId, String overrideBase, String imageFilename);

  String saveLogo(UUID producerId, String logoFilename, MultipartFile logoFile);

  default String saveImage(UUID producerId, String imageFilename, MultipartFile imageFile){
    return saveImage(producerId, null, imageFilename, imageFile);
  }

  String saveImage(UUID producerId, String overrideBase, String imageFilename, MultipartFile imageFile);

  default String getBasePath(String basePath, String override){
    return StringUtils.isBlank(override) ? basePath : override;
  }

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

  default String createUrl(String hostname, String fileKeyPath, String filename) {
    return String.format("%s/%s/%s", hostname, fileKeyPath, filename);
  }
}
