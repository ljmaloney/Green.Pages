package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.*;
import com.green.yp.exception.PreconditionFailedException;
import com.squareup.square.core.SquareApiException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentOrchestrationService {

    private final PaymentTransactionService transactionService;
    private final PaymentService paymentService;
    private final PaymentMethodService methodService;

    public PaymentOrchestrationService(PaymentTransactionService transactionService,
                                       PaymentService paymentService, PaymentMethodService methodService) {
        this.transactionService =  transactionService;
        this.paymentService = paymentService;
        this.methodService = methodService;
    }

    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest methodRequest) {
        log.info("Creating new payment method for subscriber");
        try{
            UUID paymentMethodId = UUID.randomUUID();

            var newCustomer = paymentService.createCustomer(methodRequest, paymentMethodId);

            var savedPayment = paymentService.createCardOnFile(methodRequest, newCustomer.externCustRef(), newCustomer.idempotencyId());

               return methodService.createPaymentMethod(methodRequest, newCustomer, savedPayment);
        } catch (SquareApiException e){
            log.warn("Error creating new customer / saving card {}", e.getMessage(), e);
            throw new PreconditionFailedException("There was an error when attempting to save the card for the subscription");
        }
    }

    @Transactional
    public PaymentMethodResponse replaceCardOnFile(PaymentMethodRequest methodRequest){
        log.info("Replacing existing payment method for subscriber {}", methodRequest.referenceId());
        try{
            var activeCard = methodService.findActiveMethod(methodRequest.referenceId());

            if ( customerChanged(methodRequest, activeCard)) {
                paymentService.updateCustomer(methodRequest, activeCard.externCustRef(), UUID.randomUUID());
            }
            //deactivate card
            paymentService.deactivateExistingCard(activeCard.cardRef());
            methodService.deactivateExistingCard(activeCard.paymentMethodId());

            var savedPayment = paymentService.createCardOnFile(methodRequest, activeCard.externCustRef(), UUID.randomUUID());

            return methodService.createPaymentMethod(methodRequest, activeCard.externCustRef(), savedPayment);
        } catch (SquareApiException e){
            log.warn("Error updating customer / saving card {}", e.getMessage(), e);
            throw new PreconditionFailedException("There was an error when attempting to save the card for the subscription");
        }

    }

    @Transactional
    public void cancelCardOnFile(String referenceId) {
        log.info("Cancelling existing payment method for subscriber {}", referenceId);
        try{
            var activeCard = methodService.findActiveMethod(referenceId);
            //deactivate card
            paymentService.deactivateExistingCard(activeCard.cardRef());
            methodService.deactivateExistingCard(activeCard.paymentMethodId());

        } catch (SquareApiException e){
            log.warn("Error updating customer / saving card {}", e.getMessage(), e);
            throw new PreconditionFailedException("There was an error when attempting to save the card for the subscription");
        }
    }

    private boolean customerChanged(PaymentMethodRequest methodRequest, PaymentMethodResponse activeCard) {
        return !methodRequest.firstName().equals(activeCard.givenName()) ||
               !methodRequest.lastName().equals(activeCard.familyName()) ||
               !methodRequest.companyName().equals(activeCard.companyName()) ||
               !methodRequest.payorAddress1().equals(activeCard.payorAddress1()) ||
               !methodRequest.payorAddress2().equals(activeCard.payorAddress2()) ||
               !methodRequest.payorCity().equals(activeCard.payorCity()) ||
               !methodRequest.payorState().equals(activeCard.payorState()) ||
               !methodRequest.payorPostalCode().equals(activeCard.payorPostalCode()) ||
               !methodRequest.phoneNumber().equals(activeCard.phoneNumber()) ||
               !methodRequest.emailAddress().equals(activeCard.emailAddress());
    }

    public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef) {
        //first create payment record
        var paymentResponse = transactionService.createPaymentRecord(paymentRequest);
        try{
            //call payment partner API
            var cardResponse = paymentService.processPayment(paymentRequest, paymentResponse.getId(), customerRef);
            //update payment record
            return transactionService.updatePayment(paymentResponse.getId(), cardResponse);
        } catch (SquareApiException e){
            log.warn(e.getMessage(), e);
            return transactionService.updatePaymentError(paymentResponse.getId(), e.getMessage(), e.statusCode(), e.body().toString());
        }
    }
}
