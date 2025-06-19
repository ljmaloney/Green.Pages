package com.green.yp.image.service;

import com.green.yp.image.ImageFileService;
import org.apache.xmlgraphics.image.loader.Image;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "greenyp.image.service.impl", havingValue = "local", matchIfMissing = true)
public class LocalImageFileServiceImpl implements ImageFileService {
    @Override
    public void deleteLogo(UUID producerId) {

    }

    @Override
    public void deleteImage(UUID producerId, String imageFilename) {

    }

    @Override
    public String saveLogo(UUID producerId, String logoFilename, MultipartFile logoFile) {
        return "";
    }

    @Override
    public String saveImage(UUID producerId, String imageFilename, MultipartFile imageFile) {
        return "";
    }
}
