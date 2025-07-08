package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedPaymentRequest;
import com.green.yp.api.apitype.classified.ClassifiedPaymentResponse;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.contract.PaymentContract;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedPaymentMapper;
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.NotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    var note = String.format(paymentNoteFormat, adType.adTypeName(),
            category.name(),
            classified.classified().getTitle(),
            StringUtils.truncate(classified.classified().getDescription(), 100),
            adType.monthlyPrice());

    var paymentResponse =
        paymentContract.applyPayment(
            paymentMapper.toPaymentRequest(paymentRequest, adType.monthlyPrice(), adType.monthlyPrice(), note, requestIP),
            Optional.empty());

    var directLink = "";

    // create invoice record for classified ad

    // send confirmation email
    String subject = String.format("Greenyp - %s classified ad confirmation", adType.adTypeName());
    emailService.sendEmailAsync(EmailTemplateType.CLASSIFIED_CONFIRMATION,
            Collections.singletonList(classified.classified().getEmailAddress()),
            subject,
            () -> Map.of("customer", classified.customer(),
                    "category", category,
                    "classifiedTitle", classified.classified().getTitle(),
                    "directLink", directLink,
                    "adTypeName", adType.adTypeName(),
                    "ipAddress", requestIP,
                    "timestamp", classified.classified().getCreateDate()));

    classified.classified().setActiveDate(LocalDate.now());
    classified.classified().setLastActiveDate(LocalDate.now().plusMonths(1));
    classifedRepository.save(classified.classified());

    return new ClassifiedPaymentResponse();
  }
}
