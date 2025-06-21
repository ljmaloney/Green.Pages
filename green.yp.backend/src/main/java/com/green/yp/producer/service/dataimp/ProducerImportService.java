package com.green.yp.producer.service.dataimp;

import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.*;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.producer.service.ProducerContactOrchestrationService;
import com.green.yp.producer.service.ProducerLocationService;
import com.green.yp.producer.service.ProducerOrchestrationService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProducerImportService {

  private static final String IMPORT_SUB_ID = "e0315cb5-a2e2-40e1-abb2-4ce646439730";
  private final ProducerOrchestrationService producerOrchestrationService;
  private final ProducerLocationService producerLocationService;
  private final ProducerContactOrchestrationService contactOrchestrationService;
  private final GeocodingService geocodingService;

  @Transactional
  public UUID importProducer(UUID lineOfBusinessId, ProducerCsvRecord importRecord) {
    var location =
        geocodingService.getCoordinates(
            importRecord.address, importRecord.city, importRecord.state, importRecord.zip);

    // Use contact as company name if company is empty
    String businessName = StringUtils.getIfBlank(importRecord.company, () -> importRecord.contact);
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

    ProducerLocationResponse locationResponse =
        producerLocationService.createLocation(
            new LocationRequest(
                null,
                businessName,
                ProducerLocationType.HOME_OFFICE_PRIMARY,
                LocationDisplayType.CITY_STATE_ZIP,
                true,
                importRecord.address,
                null,
                null,
                importRecord.city,
                importRecord.state,
                importRecord.zip,
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
            StringUtils.getIfBlank(importRecord.contact, () -> "Primary"),
            null,
            null,
            null,
            importRecord.phone,
            null,
            null),
        Optional.empty(),
        producerResponse.producerId(),
        locationResponse.locationId(),
        null);
    return producerResponse.producerId();
  }

  public record ProducerCsvRecord(
      long recordNumber,
      String type,
      String licenseNumber,
      String contact,
      String company,
      String address,
      String city,
      String state,
      String zip,
      String county,
      String phone) {}
}
