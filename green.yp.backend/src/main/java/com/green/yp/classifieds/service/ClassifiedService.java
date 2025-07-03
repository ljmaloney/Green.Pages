package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.classified.ClassifiedRequest;
import com.green.yp.api.apitype.classified.ClassifiedResponse;
import com.green.yp.api.apitype.classified.ClassifiedUpdateRequest;
import com.green.yp.api.apitype.enumeration.ClassifiedTokenType;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.classifieds.data.model.ClassifiedToken;
import com.green.yp.classifieds.data.repository.ClassifedTokenRepository;
import com.green.yp.classifieds.data.repository.ClassifiedCustomerRepository;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedMapper;
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.util.TokenUtils;
import jakarta.validation.Valid;

import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedService {

  @Value("${green.yp.classified.token.timeout:15}")
  private Integer tokenTimeoutMinutes;

  private final ClassifiedRepository repository;
  private final ClassifedTokenRepository tokenRepository;
  private final ClassifiedCustomerRepository customerRepository;
  private final ClassifiedAdTypeService adTypeService;
  private final ClassifiedCategoryService categoryService;
  private final ClassifiedGeocodeService geocodeService;
  private final EmailService emailService;
  private final ClassifiedMapper mapper;

  public ClassifiedService(
      ClassifiedRepository repository,
      ClassifedTokenRepository tokenRepository,
      ClassifiedCustomerRepository customerRepository,
      ClassifiedAdTypeService adTypeService,
      ClassifiedCategoryService categoryService,
      ClassifiedGeocodeService geocodeService,
      EmailService emailService,
      ClassifiedMapper mapper) {
    this.repository = repository;
    this.tokenRepository = tokenRepository;
    this.customerRepository = customerRepository;
    this.adTypeService = adTypeService;
    this.categoryService = categoryService;
    this.geocodeService = geocodeService;
    this.emailService = emailService;
    this.mapper = mapper;
  }

  public ClassifiedAdCustomerResponse findClassifiedAndCustomer(UUID classifiedId) {
    return repository
        .findClassifiedAndCustomer(classifiedId)
        .map(mapper::fromProjection)
        .orElseThrow(
            () -> {
              log.warn("No classified ad and customer found for id {}", classifiedId);
              return new NotFoundException("Classified", classifiedId);
            });
  }

  public ClassifiedResponse findClassified(UUID classifiedId, String requestIP) {
    return repository
        .findById(classifiedId)
        .map(mapper::fromEntity)
        .orElseThrow(
            () -> {
              log.warn("No classified found for id {}", classifiedId);
              return new NotFoundException("Classified", classifiedId);
            });
  }

  public ClassifiedResponse createClassified(@Valid ClassifiedRequest request) {
    // upsert customer record if not already found
    var customer =
        customerRepository
            .findClassifiedCustomerByEmailAddressOrPhoneNumber(request.emailAddress(), request.phoneNumber())
            .orElseGet(
                () -> {
                  return customerRepository.saveAndFlush(mapper.customterFromClassified(request));
                });

    adTypeService.findAdType(request.adType());
    categoryService.findCategory(request.categoryId());

    var classified = mapper.toEntity(request);

    var geoLocation = geocodeService.geocodeLocation(classified, request.address());

    classified.setClassifiedCustomerId(customer.getId());
    classified.setActiveDate(null);
    classified.setLastActiveDate(null);
    classified.setRenewalCount(0);
    classified.setLatitude(geoLocation.latitude());
    classified.setLongitude(geoLocation.longitude());

    return mapper.fromEntity(repository.saveAndFlush(classified));
  }

  public ClassifiedResponse updateClassified(@Valid ClassifiedUpdateRequest classifiedRequest, String requestIP) {
    return null;
  }

  public void requestAuthCode(UUID classifiedId, String tokenDestination, ClassifiedTokenType tokenType, String requestIP) throws NoSuchAlgorithmException {
    var classified = repository.findClassifiedAndCustomer(classifiedId)
            .orElseThrow(() -> {
              log.warn("No classified found for id {}", classifiedId);
              return new NotFoundException("Classified", classifiedId);
            });

    if ( !classified.customer().getEmailAddress().equals(tokenDestination) &&
         !classified.customer().getPhoneNumber().equals(requestIP)) {
      log.error("Invalid email address or phone number attempting to generate a token auth code");
      throw new PreconditionFailedException("Invalid email address or phone number");
    }

    //create token record
    var token = TokenUtils.generateCode(8);

    var classifiedToken =
        tokenRepository.save(
            ClassifiedToken.builder()
                .classifiedId(classifiedId)
                .destination(tokenDestination)
                .destinationType(tokenType)
                .tokenValue(TokenUtils.createdHash(token))
                .tokenUsed(false)
                .tokenExpiryDate(OffsetDateTime.now().plusMinutes(tokenTimeoutMinutes))
                .build());

    List<String> toList = List.of(classified.customer().getEmailAddress());

    emailService.sendEmailAsync(EmailTemplateType.CLASSIFIED_AUTH_TOKEN,
            toList,
            EmailTemplateType.CLASSIFIED_AUTH_TOKEN.getSubjectFormat(),
            () -> Map.of("token", classifiedToken, "ipAddress", requestIP, "timestamp", OffsetDateTime.now()));

  }
}
