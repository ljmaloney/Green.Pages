package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedRequest;
import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import com.green.yp.classifieds.data.repository.ClassifiedCustomerRepository;
import com.green.yp.classifieds.mapper.ClassifiedMapper;
import com.green.yp.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedCustomerService {

    private final ClassifiedCustomerRepository repository;
    private final ClassifiedMapper mapper;

    public ClassifiedCustomerService(ClassifiedCustomerRepository repository,
                                     ClassifiedMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    /*

     */
    public ClassifiedCustomer upsertCustomer(ClassifiedRequest request){
        return repository
                .findClassifiedCustomerByEmailAddress(
                        request.emailAddress())
                .map(
                        cust -> {
                            if (!cust.getEmailAddress().equals(request.emailAddress())
                                || cust.getEmailValidationDate() == null) {
                                log.debug(
                                        "customer {} email address has been changed, found with phone",
                                        cust.getId());
                                cust.setEmailAddress(request.emailAddress());
                                cust.setEmailValidationDate(null);
                                cust.setEmailAddressValidationToken(TokenUtils.generateCode(8));
                                return repository.save(cust);
                            }
                            return cust;
                        })
                .or( () -> {
                    return repository.findClassifiedCustomerByPhoneNumber(request.phoneNumber());
                })
                .orElseGet(
                        () -> {
                            var newCustomer = mapper.customterFromClassified(request);
                            newCustomer.setEmailAddressValidationToken(TokenUtils.generateCode(8));
                            return repository.saveAndFlush(newCustomer);
                        });
    }
}
