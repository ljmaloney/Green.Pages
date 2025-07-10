package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedPaymentRequest;
import com.green.yp.api.apitype.classified.ClassifiedPaymentResponse;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.contract.PaymentContract;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedPaymentMapper;
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.NotFoundException;
import com.green.yp.util.TokenUtils;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedPaymentService {
  private final PaymentContract paymentContract;
  private final ClassifiedPaymentMapper paymentMapper;
  private final ClassifiedRepository classifedRepository;
  private final ClassifiedAdTypeService adTypeService;
  private final ClassifiedCategoryService classifiedCategoryService;
  private final EmailService emailService;

  private String paymentNoteFormat = """
                       Classified Ad Package: %s
                       Classified Category : %s
                       Classified Title : %s
                       Classified Description : %s
                       Term : 1 month
                       Cost : $%s
  """;

  @Value("${greenyp.classified.baseUrl}")
  private String classifiedUrl;

  public ClassifiedPaymentService(
      PaymentContract paymentContract,
      ClassifiedRepository classifiedRepository,
      ClassifiedAdTypeService adTypeService,
      ClassifiedCategoryService classifiedCategoryService,
      EmailService emailService,
      ClassifiedPaymentMapper paymentMapper) {
    this.paymentContract = paymentContract;
    this.paymentMapper = paymentMapper;
    this.adTypeService = adTypeService;
    this.classifiedCategoryService = classifiedCategoryService;
    this.classifedRepository = classifiedRepository;
    this.emailService = emailService;
  }

  public ClassifiedPaymentResponse processPayment(
      @Valid ClassifiedPaymentRequest paymentRequest, String requestIP) {

    var classified =
        classifedRepository
            .findClassifiedAndCustomer(paymentRequest.classifiedId())
            .orElseThrow(
                () -> {
                  log.error(
                      String.format("Classified not found for %s", paymentRequest.classifiedId()));
                  return new NotFoundException("Classified", paymentRequest.classifiedId());
                });

    var adType = adTypeService.findAdType(classified.classified().getAdTypeId());

    var category = classifiedCategoryService.findCategory(classified.classified().getCategoryId());

    var note = StringUtils.trim(String.format(paymentNoteFormat, adType.adTypeName(),
            category.name(),
            classified.classified().getTitle(),
            StringUtils.truncate(classified.classified().getDescription(), 100),
            adType.monthlyPrice()));

    var paymentResponse =
        paymentContract.applyPayment(
            paymentMapper.toPaymentRequest(paymentRequest, "GrnPgs-Classifieds", adType.monthlyPrice(), adType.monthlyPrice(), note, requestIP),
            Optional.empty());

    if ( !"COMPLETED".equals(paymentResponse.status()) ) {
      log.warn("Attempted to process payment of classified ad {} failed", classified.classified().getId());
      return new ClassifiedPaymentResponse(classified.classified().getId(),
              classified.classified().getTitle(),
              paymentResponse.status(),null,null, null);
    }

    var token = TokenUtils.generateCode(10);
    classified.classified().setIdToken(token);

    var directLink = String.format("{}classifieds/%s?secret=%s", classifiedUrl, classified.classified().getId(), token);

    // create invoice record for classified ad

    // send confirmation email
    String subject = String.format("Greenyp - %s classified ad confirmation", adType.adTypeName());
    emailService.sendEmailAsync(EmailTemplateType.CLASSIFIED_CONFIRMATION,
            Collections.singletonList(classified.classified().getEmailAddress()),
            subject,
            () -> Map.of("customer", classified.customer(),
                    "categoryName", category.name(),
                    "classifiedTitle", classified.classified().getTitle(),
                    "link", directLink,
                    "adTypeName", adType.adTypeName(),
                    "paymentAmount", adType.monthlyPrice(),
                    "ipAddress", requestIP,
                    "classifiedId", classified.classified().getId().toString(),
                    "transactionRef", paymentResponse.paymentRef(),
                    "timestamp", classified.classified().getCreateDate()));

    classified.classified().setActiveDate(LocalDate.now());
    classified.classified().setLastActiveDate(LocalDate.now().plusMonths(1));
    classifedRepository.save(classified.classified());

    return new ClassifiedPaymentResponse(classified.classified().getId(),
            classified.classified().getTitle(),
            paymentResponse.status(),
            paymentResponse.paymentRef(),
            paymentResponse.orderRef(), paymentResponse.receiptNumber());
  }
}
