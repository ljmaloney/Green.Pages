package com.green.yp.api.contract;

import com.green.yp.reference.data.enumeration.SubscriptionType;
import com.green.yp.reference.dto.SubscriptionDto;
import com.green.yp.reference.service.SubscriptionService;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionContract {

  final SubscriptionService subscriptionService;

  public SubscriptionContract(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  public SubscriptionDto findSubscription(@NotNull @NonNull UUID subscriptionId) {
    return subscriptionService.findActiveSubscription(subscriptionId);
  }

  public List<SubscriptionDto> getAllSubscriptions(
      SubscriptionType subscriptionType, boolean active) {
    var today = new Date();
    return subscriptionService.findActiveSubscription().stream()
        .filter(sub -> sub.subscriptionType() == subscriptionType)
        .filter(
            sub ->
                !active || (active && sub.startDate().before(today) && sub.endDate().after(today)))
        .toList();
  }
}
