package com.green.yp.geolocation.service;

import com.green.yp.geolocation.data.model.PostalCodeGeocode;
import com.green.yp.geolocation.data.repository.PostalCodeGeocodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;

/**
 * Service for handling geocode data uploads from CSV files.
 * Processes postal code information including coordinates and location details.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GeocodeUploadService {
    private final PostalCodeGeocodeRepository repository;

    /**
     * Imports ZIP code geocode data from a CSV file.
     * Expected CSV format: postal_code,latitude,longitude,place_name,state
     *
     * @param file MultipartFile containing the CSV data
     * @throws RuntimeException if file processing fails
     */
    @Transactional
    public void importZipGeocodeFromCsv(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader("postal_code", "latitude", "longitude", "place_name", "state")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);

            for (CSVRecord record : csvParser) {

                PostalCodeGeocode geocode = new PostalCodeGeocode();
                geocode.setPostalCode(record.get(0));
                geocode.setLatitude(new BigDecimal(record.get(1)));
                geocode.setLongitude(new BigDecimal(record.get(2)));
                geocode.setPlaceName(record.get(3));
                geocode.setState(record.get(4));

                repository.save(geocode);
            }
        } catch (Exception e) {
            log.error("Error processing CSV file", e);
            throw new RuntimeException("Failed to process ZIP geocode CSV file: " + e.getMessage());
        }
    }
}
