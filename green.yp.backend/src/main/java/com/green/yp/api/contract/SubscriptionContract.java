package com.green.yp.api.contract;

import com.green.yp.reference.dto.SubscriptionDto;
import com.green.yp.reference.service.SubscriptionService;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

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
