package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerImageResponse;
import com.green.yp.exception.NotFoundException;
import com.green.yp.image.ImageFileService;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.repository.ImageGalleryRepository;
import com.green.yp.producer.data.repository.ProducerRepository;
import com.green.yp.producer.mapper.ImageGalleryMapper;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
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
    private final ImageGalleryMapper imageGalleryMapper;
    private final ImageFileService imageFileService;

    public ProducerImageService(ProducerRepository producerRepository,
                                ImageGalleryRepository imageGalleryRepository,
                                ImageGalleryMapper imageGalleryMapper,
                                ImageFileService imageFileService){
        this.producerRepository = producerRepository;
        this.imageGalleryRepository=imageGalleryRepository;
        this.imageGalleryMapper = imageGalleryMapper;
        this.imageFileService = imageFileService;
    }

    public List<ProducerImageResponse> getGalleryImages(@NonNull @NotNull UUID producerId) {

        var images = imageGalleryRepository.findImageGalleriesByProducerId(producerId);
        log.debug("Found {} images in the image gallery for {}", images.size(), producerId);
        return imageGalleryMapper.mapToResponse(images);
    }

    public void uploadLogoImage(UUID producerId, String logoFileName, MultipartFile file) {
        Producer producer = producerRepository.findById(producerId)
                .orElseThrow(() -> new NotFoundException("producer", producerId));
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
