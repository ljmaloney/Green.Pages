package com.green.yp.geolocation.controller;

import com.green.yp.geolocation.service.GeocodeUploadService;
import com.green.yp.security.IsAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/producer/postalcode/geocode")
@RequiredArgsConstructor
public class GeocodeUploadController {

  private final GeocodeUploadService geocodeUploadService;

  @IsAdmin
  @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadGeocodeData(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }

    geocodeUploadService.importZipGeocodeFromCsv(file);
    return ResponseEntity.ok().build();
  }
}
