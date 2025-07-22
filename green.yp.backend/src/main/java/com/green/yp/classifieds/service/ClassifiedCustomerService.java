package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedRequest;
import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import com.green.yp.classifieds.data.repository.ClassifiedCustomerRepository;
import com.green.yp.classifieds.mapper.ClassifiedMapper;
import com.green.yp.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .findClassifiedCustomerByEmailAddressAndPhoneNumber(request.emailAddress(), request.phoneNumber())
                .map(cust -> {
                    upsertCustomerData(request, cust);
                    return repository.save(cust);
                 })
//                .or( () -> repository.findClassifiedCustomerByPhoneNumber(request.phoneNumber())
//                        .map(cust -> {
//                            log.debug(
//                                    "customer {} email address has been changed, found with phone",
//                                    cust.getId());
//                            cust.setEmailAddress(request.emailAddress());
//                            cust.setEmailValidationDate(null);
//                            cust.setEmailAddressValidationToken(TokenUtils.generateCode(8));
//                            upsertCustomerData(request, cust);
//                            return repository.save(cust);
//                        })).or(Optional::empty)
                .orElseGet(
                                () -> {
                                    var newCustomer = mapper.customterFromClassified(request);
                                    newCustomer.setEmailAddressValidationToken(TokenUtils.generateCode(8));
                                    return repository.saveAndFlush(newCustomer);
                });
    }

    private static void upsertCustomerData(ClassifiedRequest request, ClassifiedCustomer cust) {
        cust.setFirstName(request.firstName());
        cust.setLastName(request.lastName());
        cust.setAddress(request.address());
        cust.setCity(request.city());
        cust.setState(request.state());
        cust.setPostalCode(request.postalCode());
    }
}
