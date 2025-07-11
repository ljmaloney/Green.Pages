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
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedService {

    private static final String CLASSIFIED = "Classified";

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
              return new NotFoundException(CLASSIFIED, classifiedId);
            });
  }

  public ClassifiedResponse findClassified(UUID classifiedId, String requestIP) {
    return repository
        .findById(classifiedId)
        .map(mapper::fromEntity)
        .orElseThrow(
            () -> {
              log.warn("No classified found for id {}", classifiedId);
              return new NotFoundException(CLASSIFIED, classifiedId);
            });
  }

  public ClassifiedResponse createClassified(@Valid ClassifiedRequest request, String requestIP) {
    log.info(
        "Creating new classified ad {} in {} for {} from {}",
        request.title(),
        request.categoryId(),
        request.emailAddress(),
        requestIP);
    // upsert customer record if not already found
    var customer =
        customerRepository
            .findClassifiedCustomerByEmailAddressOrPhoneNumber(
                request.emailAddress(), request.phoneNumber())
            .map(
                cust -> {
                  if (!cust.getEmailAddress().equals(request.emailAddress())) {
                    log.debug(
                        "customer {} email address has been changed, found with phone",
                        cust.getId());
                    cust.setEmailAddress(request.emailAddress());
                    cust.setEmailValidationDate(null);
                    cust.setEmailAddressValidationToken(TokenUtils.generateCode(8));
                    return customerRepository.save(cust);
                  }
                  return cust;
                })
            .orElseGet(
                () -> {
                  var newCustomer = mapper.customterFromClassified(request);
                  newCustomer.setEmailAddressValidationToken(TokenUtils.generateCode(8));
                  return customerRepository.saveAndFlush(newCustomer);
                });

    log.debug("Validate adType for {}", request.address());
    var adType = adTypeService.findAdType(request.adType());

    log.debug("Validate category for {}", request.categoryId());
    var category = categoryService.findCategory(request.categoryId());

    var classified = mapper.toEntity(request);

    log.debug("Generate geo-location for classified ad for {}", request.title());
    var geoLocation = geocodeService.geocodeLocation(classified, request.address());

    classified.setClassifiedCustomerId(customer.getId());
    classified.setActiveDate(null);
    classified.setLastActiveDate(null);
    classified.setRenewalCount(0);
    classified.setPerUnitType(request.pricePerUnitType());
    classified.setLatitude(geoLocation.latitude());
    classified.setLongitude(geoLocation.longitude());

    var classifiedResponse = mapper.fromEntity(repository.saveAndFlush(classified));

    // send confirmation email
    String subject = String.format("Greenyp - %s classified ad confirmation", adType.adTypeName());
    emailService.sendEmailAsync(
        EmailTemplateType.CLASSIFIED_EMAIL_VALIDATION,
        Collections.singletonList(classified.getEmailAddress()),
        subject,
        () -> {
          Map<String, Object> templateData = new HashMap<>();
          templateData.put("lastName", request.lastName());
          templateData.put("firstName", request.firstName());
          templateData.put("classifiedTitle", request.title());
          templateData.put("categoryName", category.name());
          templateData.put("emailValidationToken", customer.getEmailAddressValidationToken());
          templateData.put("adTypeName", adType.adTypeName());
          templateData.put("paymentAmount", adType.monthlyPrice());
          templateData.put("ipAddress", requestIP);
          templateData.put("timestamp", classified.getCreateDate());
          return templateData;
        });

    return classifiedResponse;
  }

  public ClassifiedResponse updateClassified(
      @Valid ClassifiedUpdateRequest classifiedRequest, String requestIP) {
    return null;
  }

  public void requestAuthCode(
      UUID classifiedId, String tokenDestination, ClassifiedTokenType tokenType, String requestIP)
      throws NoSuchAlgorithmException {
    var classified =
        repository
            .findClassifiedAndCustomer(classifiedId)
            .orElseThrow(
                () -> {
                  log.warn("No classified found for id {}", classifiedId);
                  return new NotFoundException("Classified", classifiedId);
                });

    if (!classified.customer().getEmailAddress().equals(tokenDestination)
        && !classified.customer().getPhoneNumber().equals(requestIP)) {
      log.error("Invalid email address or phone number attempting to generate a token auth code");
      throw new PreconditionFailedException("Invalid email address or phone number");
    }

    // create token record
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

    emailService.sendEmailAsync(
        EmailTemplateType.CLASSIFIED_AUTH_TOKEN,
        toList,
        EmailTemplateType.CLASSIFIED_AUTH_TOKEN.getSubjectFormat(),
        () ->
            Map.of(
                "token",
                classifiedToken,
                "ipAddress",
                requestIP,
                "timestamp",
                OffsetDateTime.now()));
  }
}
