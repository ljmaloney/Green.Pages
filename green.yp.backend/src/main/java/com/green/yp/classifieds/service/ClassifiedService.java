package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.classified.ClassifiedRequest;
import com.green.yp.api.apitype.classified.ClassifiedResponse;
import com.green.yp.api.apitype.classified.ClassifiedUpdateRequest;
import com.green.yp.api.apitype.email.EmailValidationStatusType;
import com.green.yp.api.apitype.enumeration.ClassifiedTokenType;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.contract.EmailContract;
import com.green.yp.classifieds.data.model.Classified;
import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import com.green.yp.classifieds.data.model.ClassifiedToken;
import com.green.yp.classifieds.data.repository.ClassifedTokenRepository;
import com.green.yp.classifieds.data.repository.ClassifiedCustomerRepository;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedMapper;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.mapper.ProducerLocationMapper;
import com.green.yp.util.TokenUtils;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedService {
  private static final String CLASSIFIED = "Classified";

  @Value("${green.yp.classified.token.timeout:15}")
  private Integer tokenTimeoutMinutes;

    @Value("${green.yp.classified.unpaid.timeout:30}")
    private Integer unpaidTimeoutMinutes;

  private final ClassifiedRepository repository;
  private final ClassifedTokenRepository tokenRepository;
  private final ClassifiedCustomerRepository customerRepository;
  private final ClassifiedAdTypeService adTypeService;
  private final ClassifiedCategoryService categoryService;
  private final ClassifiedGeocodeService geocodeService;
  private final EmailContract emailContract;
  private final ClassifiedImageService imageService;
  private final ClassifiedCustomerService customerService;
  private final ClassifiedMapper mapper;

  public ClassifiedService(
          ClassifiedRepository repository,
          ClassifedTokenRepository tokenRepository,
          ClassifiedCustomerRepository customerRepository,
          ClassifiedAdTypeService adTypeService,
          ClassifiedCategoryService categoryService,
          ClassifiedGeocodeService geocodeService,
          EmailContract emailContract, ClassifiedImageService imageService,
          ClassifiedMapper mapper,
          ClassifiedCustomerService customerService) {
    this.repository = repository;
    this.tokenRepository = tokenRepository;
    this.customerRepository = customerRepository;
    this.adTypeService = adTypeService;
    this.categoryService = categoryService;
    this.geocodeService = geocodeService;
    this.emailContract = emailContract;
      this.imageService = imageService;
      this.mapper = mapper;
      this.customerService = customerService;
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
              log.warn("No classified found for id {} from {}", classifiedId, requestIP);
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
    var customer = customerService.upsertCustomer(request);

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

    var emailValidation = emailContract.validateEmail(classified.getId().toString(), customer.getEmailAddress());

    // send confirmation email
    if(emailValidation.validationStatus() == EmailValidationStatusType.NOT_VALIDATED
       || emailValidation.validationStatus() == EmailValidationStatusType.VALIDATED ){
        emailContract.sendEmail(
                  EmailTemplateType.CLASSIFIED_EMAIL_VALIDATION,
                  Collections.singletonList(classified.getEmailAddress()),
                  EmailTemplateType.CLASSIFIED_EMAIL_VALIDATION.getSubjectFormat(),
                  () -> {
                      Map<String, Object> templateData = new HashMap<>();
                      templateData.put("lastName", request.lastName());
                      templateData.put("firstName", request.firstName());
                      templateData.put("classifiedTitle", request.title());
                      templateData.put("categoryName", category.name());
                      templateData.put("emailValidationToken", emailValidation.token());
                      templateData.put("adTypeName", adType.adTypeName());
                      templateData.put("paymentAmount", adType.monthlyPrice());
                      templateData.put("ipAddress", requestIP);
                      templateData.put("timestamp", classified.getCreateDate());
                      return templateData;
                  });
      }

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
                  return new NotFoundException(CLASSIFIED, classifiedId);
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

    emailContract.sendEmail(
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

    public void cleanUnpaid() {
      var deleteDate = OffsetDateTime.now().minusMinutes(unpaidTimeoutMinutes.longValue());
      List<Classified> classifieds = repository.findUnpaidAds(deleteDate);
      classifieds.forEach(classified -> {
          imageService.deleteGalleryImages(classified.getId());
          repository.delete(classified);
          log.info("Deleted unpaid classified: {} - {}", classified.getId(), classified.getTitle());
      });
    }

    public void validateEmail(UUID classifiedId, String emailAddress, String emailToken, String requestIP) {
      log.info("Validating email address {} for classifiedId: {} from {}", emailAddress, classifiedId, requestIP);
      repository.findClassifiedAndCustomer(classifiedId).ifPresentOrElse( projection -> {
                var customer = projection.customer();
                if ( customer.getEmailAddress().equals(emailAddress) && customer.isValidToken(emailToken)){
                    customer.setEmailValidationDate(OffsetDateTime.now());
                    customerRepository.save(customer);
                }else {
                    log.warn("Invalid email or email token for classifiedId {}", classifiedId);
                    throw new PreconditionFailedException("Invalid email or email token");
                }
              },
              () -> {
                  log.warn("No classified found for {}", classifiedId);
                  throw new NotFoundException("classified", classifiedId);
              }
      );
    }
}
