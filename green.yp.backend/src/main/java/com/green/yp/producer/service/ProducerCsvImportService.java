package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.*;
import com.green.yp.geolocation.data.repository.PostalCodeGeocodeRepository;
import com.green.yp.geolocation.service.GeocodingService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerCsvImportService {
  private final String IMPORT_SUB_ID = "e0315cb5-a2e2-40e1-abb2-4ce646439730";
  private final ProducerOrchestrationService producerOrchestrationService;
  private final ProducerLocationService producerLocationService;
  private final ProducerContactOrchestrationService contactOrchestrationService;
  private final PostalCodeGeocodeRepository postalCodeGeocodeRepository;
  private final GeocodingService geocodingService;

  @Transactional
  public List<UUID> importProducersFromCsv(MultipartFile file, UUID lineOfBusinessId) {

    List<UUID> createdProducerIds = new ArrayList<>();

    try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      CSVParser csvParser =
          CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).build().parse(reader);

      List<CSVRecord> records = csvParser.getRecords();
      records.parallelStream()
          .filter(record -> record.getRecordNumber() != 1)
          .forEach(
              record -> {
                String type = "";
                String licenseNumber = "";
                String contact = "";
                String company = "";
                String address = "";
                String city = "";
                String state = "";
                String zip = "";
                String county = "";
                String phone = "";
                try {
                  type = record.get(0);
                  licenseNumber = record.get(1);
                  contact = record.get(2);
                  company = record.get(3);
                  address = record.get(4);
                  city = record.get(5);
                  state = record.get(6);
                  zip = record.get(7);
                  county = record.get(8);
                  phone = StringUtils.trim(record.get(9));
                  if (phone.length() > 12) {
                    phone = phone.substring(0, 12);
                  }
                  var location = geocodingService.getCoordinates(zip);
                  // Use contact as company name if company is empty
                  String businessName = company.isEmpty() ? contact : company;

                  ProducerResponse producerResponse =
                      producerOrchestrationService.createProducer(
                          new CreateProducerRequest(
                              businessName,
                              lineOfBusinessId,
                              UUID.fromString(IMPORT_SUB_ID),
                              ProducerSubscriptionType.LIVE_UNPAID,
                              null,
                              null,
                              null),
                          null);

                  synchronized (createdProducerIds) {
                    createdProducerIds.add(producerResponse.producerId());
                  }

                  ProducerLocationResponse locationResponse =
                      producerLocationService.createLocation(
                          new LocationRequest(
                              null,
                              businessName,
                              ProducerLocationType.HOME_OFFICE_PRIMARY,
                              LocationDisplayType.CITY_STATE_ZIP,
                              true,
                              address,
                              null,
                              null,
                              city,
                              state,
                              zip,
                              location.latitude(),
                              location.longitude(),
                              null),
                          producerResponse.producerId(),
                          null);

                  contactOrchestrationService.createContact(
                      new ProducerContactRequest(
                          null,
                          locationResponse.locationId(),
                          ProducerContactType.PRIMARY,
                          ProducerDisplayContactType.GENERIC_NAME_PHONE_EMAIL,
                          StringUtils.isBlank(contact) ? "Primaru" : contact,
                          null,
                          null,
                          phone,
                          null,
                          null),
                      Optional.empty(),
                      producerResponse.producerId(),
                      locationResponse.locationId(),
                      null);
                } catch (Exception e) {
                  log.error(
                      "Error processing {}. {}. {}, {}",
                      record.get(1),
                      record.get(2),
                      record.get(3),
                      record.get(4));
                  throw new RuntimeException(
                      "Failed to process CSV record: " + record.getRecordNumber(), e);
                }
              });
    } catch (Exception e) {
      log.error("Error processing CSV file", e);
      throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
    }
    return createdProducerIds;
  }
}
