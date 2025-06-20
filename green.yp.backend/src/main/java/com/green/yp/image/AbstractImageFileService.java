package com.green.yp.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public abstract class AbstractImageFileService implements ImageFileService{
    @Value("${greenyp.image.service.url.base:subscriber}")
    private String urlBasePath;

    @Value("${greenyp.image.service.logo.path:logo}")
    private String logoPath;

    @Value("${greenyp.image.service.imageGallery.path:image}")
    private String imageGalleryPath;

    @Override
    public void deleteLogo(UUID producerId) {
        String pathString = createFileKeyPath(urlBasePath, producerId, logoPath, null);
        deleteFile(pathString, null);
    }

    @Override
    public void deleteImage(UUID producerId, String imageFilename) {
        String pathString = createFileKeyPath(urlBasePath, producerId, imageGalleryPath, null);
        deleteFile(pathString, imageFilename);
    }

    @Override
    public String saveLogo(UUID producerId, String logoFilename, MultipartFile logoFile) {
        String pathString = createFileKeyPath(urlBasePath, producerId, logoPath, null);
        String fileName = logoFilename.replace(' ','-');
        return saveImageFile(logoFile, pathString, fileName);
    }
    @Override
    public String saveImage(UUID producerId, String imageFilename, MultipartFile imageFile) {
        String pathString = createFileKeyPath(urlBasePath, producerId, imageGalleryPath, null);
        String fileName = imageFilename.replace(' ','-');
        return saveImageFile(imageFile, pathString, fileName);
    }

    protected abstract String saveImageFile( MultipartFile multipartFile, String pathString, String fileName);
    protected abstract void deleteFile(String pathKey, String fileName);
}
