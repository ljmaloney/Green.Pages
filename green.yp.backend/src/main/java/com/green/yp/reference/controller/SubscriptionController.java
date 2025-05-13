package com.green.yp.reference.controller;

import com.green.yp.api.apitype.CreateSubscriptionRequest;
import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.UpdateSubscriptionRequest;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.reference.dto.SubscriptionDto;
import com.green.yp.reference.service.SubscriptionService;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("reference")
@Tag(name = "REST endpoint to manage supported types of subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Operation(summary = "Returns the list of active subscriptions")
    @GetMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<SubscriptionDto>> findActiveSubscriptions() {
        return new ResponseApi<>(subscriptionService.findActiveSubscription(), null);
    }

    @Operation(summary = "Returns the list of active subscriptions for a line of business")
    @GetMapping(value = "/lob/subscription/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<SubscriptionDto>> findActiveSubscriptions(
            @RequestParam("lob") UUID lineOfBusinessId) {
        return new ResponseApi<>(subscriptionService.findActiveLobSubscription(lineOfBusinessId), null);
    }

    @Operation(summary = "Creates a new subscription")
    @ApiResponse(responseCode = "200", description = "Subscription created")
    @PostMapping(
            value = "/subscription",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<SubscriptionDto> createSubscription(
            @RequestBody @Valid CreateSubscriptionRequest createRequest) {
        return new ResponseApi<>(
                subscriptionService.createSubscription(createRequest, null, RequestUtil.getRequestIP()),
                null);
    }

    @Operation(summary = "updates an existing subscription")
    @PutMapping(
            value = "/subscription",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<SubscriptionDto> updateSubscription(
            @Valid @RequestBody UpdateSubscriptionRequest updateRequest) {
        return new ResponseApi<>(
                subscriptionService.updateSubscription(updateRequest, null, RequestUtil.getRequestIP()),
                null);
    }

    @Operation(summary = "Updates one or more fields of a subscription")
    @PatchMapping(value = "/subscription/{subscriptionId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<SubscriptionDto> patchSubscription(
            @NotNull @NonNull @PathVariable("subscriptionId") UUID subscriptionId,
            @Valid @RequestBody PatchRequest patchRequest) {
        return new ResponseApi<>(subscriptionService.patchSubscription(subscriptionId, patchRequest), null);
    }

}
