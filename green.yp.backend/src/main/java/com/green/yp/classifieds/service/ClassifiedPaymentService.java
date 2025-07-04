package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedPaymentRequest;
import com.green.yp.api.apitype.classified.ClassifiedPaymentResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedPaymentService {
    public ClassifiedPaymentResponse processPayment(@Valid ClassifiedPaymentRequest paymentRequest, String requestIP) {
        return null;
    }
}
