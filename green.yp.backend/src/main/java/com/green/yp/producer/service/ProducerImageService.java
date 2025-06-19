package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerImageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProducerImageService {

    public List<ProducerImageResponse> getGalleryImages(UUID producerId) {
        return Collections.emptyList();
    }

    public void uploadLogoImage(UUID producerId, String logoFileName, MultipartFile file) {
    }

    public void uploadGalleryImage(UUID producerId, String imageDescription, MultipartFile file) {
    }

    public ProducerImageResponse getProducerLogo(UUID producerId) {
        return null;
    }

    public void deleteLogo(UUID producerId, String requestIP) {
    }

    public void deleteGallaryImage(UUID producerId, String imageFilename, String requestIP) {
    }
}
