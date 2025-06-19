package com.green.yp.image.service;

import com.green.yp.exception.SystemException;
import com.green.yp.image.ImageFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlgraphics.image.loader.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "greenyp.image.service.impl", havingValue = "local", matchIfMissing = true)
public class LocalImageFileServiceImpl implements ImageFileService {

  @Value("${greenyp.image.service.filepath}")
  private String fileSystemPath;

    @Value("${greenyp.image.service.url.base:subscriber}")
    private String urlBasePath;

    @Value("${greenyp.image.service.logo.path:logo}")
    private String logoPath;

    @Value("${greenyp.image.service.imageGallery.path:image}")
    private String imageGalleryPath;

    @Override
    public void deleteLogo(UUID producerId) {
        String pathString = createPath(fileSystemPath, urlBasePath, logoPath, producerId);
        Path path = FileSystems.getDefault().getPath(pathString);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Unexpected error deleting / removing producer logo", e);
            throw new SystemException("Unexpected error deleting / removing producer logo", e);
        }
    }

    @Override
    public void deleteImage(UUID producerId, String imageFilename) {
        String pathString = createPath(fileSystemPath, urlBasePath, imageGalleryPath, producerId);
        Path path = FileSystems.getDefault().getPath(pathString, imageFilename);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Unexpected error deleting / removing producer image", e);
            throw new SystemException("Unexpected error deleting / removing producer image", e);
        }
    }

    @Override
    public String saveLogo(UUID producerId, String logoFilename, MultipartFile logoFile) {
        String pathString = createPath(fileSystemPath, urlBasePath, logoPath, producerId);
        Path path = FileSystems.getDefault().getPath(pathString, logoFilename);
        try {
            Files.copy(logoFile.getInputStream(), path);
            return createUrlPath(urlBasePath, producerId, logoPath, logoFilename);
        } catch (IOException e) {
            log.error("Unexpected error saving producer/subscriber logo", e);
            throw new SystemException("Unexpected error saving producer/subscriber logo", e);
        }
    }

    @Override
    public String saveImage(UUID producerId, String imageFilename, MultipartFile imageFile) {
        String pathString = createPath(fileSystemPath, urlBasePath, logoPath, producerId);
        Path path = FileSystems.getDefault().getPath(pathString, imageFilename);
        try {
            Files.copy(imageFile.getInputStream(), path);
            return createUrlPath(urlBasePath, producerId, logoPath, imageFilename);
        } catch (IOException e) {
            log.error("Unexpected error saving producer/subscriber logo", e);
            throw new SystemException("Unexpected error saving producer/subscriber logo", e);
        }
    }

    private String createUrlPath(String urlBasePath, UUID producerId, String subfolder, String logoFilename) {
        return urlBasePath +
               "/" +
               producerId.toString() +
               "/" +
               subfolder +
               "/" +
               logoFilename;
    }

    private String createPath(String fileSystemPath, String urlBasePath, String subfolder, UUID producerId) {
        return new StringBuilder(fileSystemPath)
                .append(File.pathSeparator)
                .append(urlBasePath)
                .append(File.pathSeparator)
                .append(producerId.toString())
                .append(File.pathSeparator)
                .append(subfolder)
                .toString();
    }
}
