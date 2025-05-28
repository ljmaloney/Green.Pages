package com.green.yp.reference.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.CreateSubscriptionRequest;
import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.UpdateSubscriptionRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.common.ServiceUtils;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import com.green.yp.reference.data.model.Subscription;
import com.green.yp.reference.data.repository.SubscriptionRepository;
import com.green.yp.reference.dto.SubscriptionDto;
import com.green.yp.reference.mapper.SubscriptionMapper;
import com.green.yp.util.DateUtils;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SubscriptionService {

    final
    SubscriptionMapper subscriptionMapper;

    final
    SubscriptionRepository subscriptionRepository;

    final
    LineOfBusinessService lobService;

    public SubscriptionService(SubscriptionMapper subscriptionMapper,
                               SubscriptionRepository subscriptionRepository,
                               LineOfBusinessService lobService) {
        this.subscriptionMapper = subscriptionMapper;
        this.subscriptionRepository = subscriptionRepository;
        this.lobService = lobService;
    }

    @Cacheable("activeSubscriptions")
    public List<SubscriptionDto> findActiveSubscription() {
        List<Subscription> subscriptionList =
                subscriptionRepository.findAllActive(
                        new Date(), SubscriptionType.TOP_LEVEL, SubscriptionType.ADD_ON);
        if (CollectionUtils.isEmpty(subscriptionList)) {
            throw new NotFoundException("No active subscriptions found");
        }

        return subscriptionMapper.mapToDto(subscriptionList);
    }

    @Cacheable("subscription")
    public SubscriptionDto findActiveSubscription(@NotNull @NonNull UUID subscriptionId) {

        Subscription subscription =
                subscriptionRepository
                        .findById(subscriptionId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                String.format(
                                                        "No currently active subscription found for: %s", subscriptionId)));

        return subscriptionMapper.mapToDto(subscription);
    }

    @Cacheable("lobSubscription")
    public List<SubscriptionDto> findActiveLobSubscription(@NotNull @NonNull UUID lineOfBusinessId) {
        lobService.findLineOfBusiness(lineOfBusinessId);

        List<Subscription> subscriptionList =
                subscriptionRepository.findAllActive(
                        lineOfBusinessId,
                        new Date(),
                        SubscriptionType.LINE_OF_BUSINESS,
                        SubscriptionType.LINE_OF_BUSINESS_ADD_ON);
        if (CollectionUtils.isEmpty(subscriptionList)) {
            throw new NotFoundException("No active subscriptions found");
        }

        return subscriptionMapper.mapToDto(subscriptionList);
    }

    @AuditRequest(
            requestParameter = "createRequest",
            objectType = AuditObjectType.SUBSCRIPTION,
            actionType = AuditActionType.CREATE)
    @CacheEvict(value = {"activeSubscriptions"}, allEntries = true)
    public SubscriptionDto createSubscription(
            CreateSubscriptionRequest createRequest, String userId, String ipAddress) {
        if (createRequest.getLineOfBusinessId() != null) {
            lobService.findLineOfBusiness(createRequest.getLineOfBusinessId());
        }

        Subscription subscription =
                subscriptionRepository.saveAndFlush(subscriptionMapper.mapToEntity(createRequest));
        log.info("Created new subscription - {}", subscription);
        return subscriptionMapper.mapToDto(subscription);
    }

    @AuditRequest(
            requestParameter = "updateRequest",
            objectType = AuditObjectType.SUBSCRIPTION,
            actionType = AuditActionType.UPDATE)
    @CacheEvict(value = {"activeSubscriptions", "subscriptions"}, allEntries = true)
    public SubscriptionDto updateSubscription(
            UpdateSubscriptionRequest updateRequest, String userId, String ipAddress) {
        if (updateRequest.getLineOfBusinessId() != null) {
            lobService.findLineOfBusiness(updateRequest.getLineOfBusinessId());
        }

        Subscription subscription =
                subscriptionRepository
                        .findById(updateRequest.getSubscriptionId())
                        .orElseThrow(
                                () -> new NotFoundException("SubscriptionId", updateRequest.getSubscriptionId()));

        try {
            PropertyUtils.copyProperties(subscription, updateRequest);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(
                    "Unexpected error when attempting to update subscription id: {}",
                    updateRequest.getSubscriptionId());
            throw new PreconditionFailedException("Attempt to update using invalid payload");
        }

        subscription = subscriptionRepository.saveAndFlush(subscription);

        log.info("Updated subscription - {}", subscription);
        return subscriptionMapper.mapToDto(subscription);
    }

    @AuditRequest(
            requestParameter = "patchRequest",
            objectType = AuditObjectType.SUBSCRIPTION,
            actionType = AuditActionType.PATCH)
    @CacheEvict(value = {"activeSubscriptions", "subscriptions"}, allEntries = true)
    public SubscriptionDto patchSubscription(UUID subscriptionId, PatchRequest patchRequest) {
        log.debug("Patching subscription {} ", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription", subscriptionId));

    ServiceUtils.patchEntity(
        patchRequest,
        subscription,
        (name, value) -> {
          return switch (name) {
                case "monthlyAutopayAmount", "quarterlyAutopayAmount", "annualBillAmount" -> BigDecimal.valueOf((double)value);
                case "startDate", "endDate" -> DateUtils.parseDate(value.toString(), Date.class);
                default -> value;
          };
        });
        return subscriptionMapper.mapToDto(subscriptionRepository.saveAndFlush(subscription));
    }
}
