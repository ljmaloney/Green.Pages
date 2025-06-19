package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerImageResponse;
import com.green.yp.producer.data.repository.ImageGalleryRepository;
import com.green.yp.producer.data.repository.ProducerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProducerImageService {

    private final ProducerRepository producerRepository;
    private final ImageGalleryRepository imageGalleryRepository;

    public ProducerImageService(ProducerRepository producerRepository,
                                ImageGalleryRepository imageGalleryRepository){
        this.producerRepository = producerRepository;
        this.imageGalleryRepository=imageGalleryRepository;
    }

    public List<ProducerImageResponse> getGalleryImages(UUID producerId) {
        return Collections.emptyList();
    }

    public void uploadLogoImage(UUID producerId, String logoFileName, MultipartFile file) {
    }

    public void uploadGalleryImage(UUID producerId, String imageDescription, String description, MultipartFile file) {
    }

    public ProducerImageResponse getProducerLogo(UUID producerId) {
        return null;
    }

    public void deleteLogo(UUID producerId, String requestIP) {
    }

    public void deleteGallaryImage(UUID producerId, String imageFilename, String requestIP) {
    }
}
