package com.green.yp.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImageFileService {
    public void deleteLogo(UUID producerId);
    public void deleteImage(UUID producerId, String imageFilename);
    public String saveLogo(UUID producerId, String logoFilename, MultipartFile logoFile);
    public String saveImage(UUID producerId, String imageFilename, MultipartFile imageFile);

}
