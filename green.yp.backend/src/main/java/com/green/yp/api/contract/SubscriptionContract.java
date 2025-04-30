package com.green.yp.api.contract;

import com.green.yp.reference.dto.SubscriptionDto;
import com.green.yp.reference.service.SubscriptionService;
import jakarta.validation.constraints.NotNull;
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
}
