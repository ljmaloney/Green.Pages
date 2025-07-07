package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedPaymentRequest;
import com.green.yp.api.apitype.classified.ClassifiedPaymentResponse;
import com.green.yp.api.contract.PaymentContract;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedPaymentMapper;
import com.green.yp.exception.NotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedPaymentService {
  private final PaymentContract paymentContract;
  private final ClassifiedPaymentMapper paymentMapper;
  private final ClassifiedRepository classifedRepository;
  private final ClassifiedAdTypeService adTypeService;
  private final ClassifiedCategoryService classifiedCategoryService;

  public ClassifiedPaymentService(
      PaymentContract paymentContract,
      ClassifiedRepository classifiedRepository,
      ClassifiedAdTypeService adTypeService,
      ClassifiedCategoryService classifiedCategoryService,
      ClassifiedPaymentMapper paymentMapper) {
    this.paymentContract = paymentContract;
    this.paymentMapper = paymentMapper;
    this.adTypeService = adTypeService;
    this.classifiedCategoryService = classifiedCategoryService;
    this.classifedRepository = classifiedRepository;
  }

  public ClassifiedPaymentResponse processPayment(
      @Valid ClassifiedPaymentRequest paymentRequest, String requestIP) {

    var classified =
        classifedRepository
            .findById(paymentRequest.classifiedId())
            .orElseThrow(
                () -> {
                  log.error(
                      String.format("Classified not found for %s", paymentRequest.classifiedId()));
                  return new NotFoundException("Classified", paymentRequest.classifiedId());
                });
    var adType = adTypeService.findAdType(classified.getAdTypeId());
    var category = classifiedCategoryService.findCategory(classified.getCategoryId());

      String str = "Classified Ad Package: " +
                   adType.adTypeName() +
                   "\r\n" +
                   "Classified Category : " +
                   category.name() +
                   "\r\n" +
                   "Classified Title : " +
                   classified.getTitle() +
                   "\r\n" +
                   "Classified Description : " +
                   classified.getDescription() +
                   "\r\n" +
                   "Term : 1 month" +
                   "\r\n" +
                   "Cost : $" +
                   adType.monthlyPrice() +
                   "\r\n";

    var paymentResponse =
        paymentContract.applyPayment(
            paymentMapper.toPaymentRequest(paymentRequest, str, requestIP),
            Optional.empty());

    // create invoice record for classified ad

    // send confirmation email

    classified.setActiveDate(LocalDate.now());
    classified.setLastActiveDate(LocalDate.now().plusMonths(1));
    classifedRepository.save(classified);

    return new ClassifiedPaymentResponse();
  }
}
