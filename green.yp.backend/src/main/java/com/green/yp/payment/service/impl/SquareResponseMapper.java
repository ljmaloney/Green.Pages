package com.green.yp.payment.service.impl;

import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.payment.data.json.CardDetails;
import com.green.yp.payment.data.json.CardError;
import com.squareup.square.types.*;
import com.squareup.square.types.Error;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SquareResponseMapper {

  @Mapping(
      target = "paymentTransactionId",
      source = "referenceId",
      qualifiedByName = "unwrapOptionalUUID")
  @Mapping(target = "paymentRef", source = "id", qualifiedByName = "unwrapOptionalString")
  @Mapping(target = "locationRef", source = "locationId", qualifiedByName = "unwrapOptionalString")
  @Mapping(target = "orderRef", source = "orderId", qualifiedByName = "unwrapOptionalString")
  @Mapping(target = "customerRef", source = "customerId", qualifiedByName = "unwrapOptionalString")
  @Mapping(
      target = "descriptionId",
      source = "statementDescriptionIdentifier",
      qualifiedByName = "unwrapOptionalString")
  @Mapping(target = "cardDetails", source = "cardDetails", qualifiedByName = "unwrapCardDetails")
  PaymentResponse toPaymentResponse(Payment payment);

  com.green.yp.payment.data.json.Card toCard(@NotNull Card card);

  CardDetails toCardDetails(@NotNull CardPaymentDetails cardPaymentDetails);

  @Named("unwrapOptionalUUID")
  default UUID unwrapOptionalUUID(Optional<String> optionalString) {
    return optionalString
        .map(UUID::fromString)
        .orElse(null); // Or optionalString.orElse(""); for an empty string default
  }

  default String unwrapString(Optional<String> optionalString) {
    return optionalString.orElse(null); // Or optionalString.orElse(""); for an empty string default
  }

  default List<CardError> unwrapErrors(Optional<List<Error>> value) {
    return value.map(this::unwrapCardErrors).orElse(null);
  }

  List<CardError> unwrapCardErrors(@NotNull List<Error> errors);

  @Named("unwrapOptionalString")
  default String unwrapOptionalString(Optional<String> optionalString) {
    return optionalString.orElse(null); // Or optionalString.orElse(""); for an empty string default
  }

  default com.green.yp.payment.data.json.Card mapCard(Optional<Card> value) {
    return value.map(this::toCard).orElse(null);
  }

  default com.green.yp.payment.data.json.CardPaymentTimeline unwrapTimeline(
      Optional<CardPaymentTimeline> value) {
    return value.map(this::mapTimeline).orElse(null);
  }

  com.green.yp.payment.data.json.CardPaymentTimeline mapTimeline(
      @NotNull CardPaymentTimeline cardPaymentTimeline);

  default String mapCardTyoe(Optional<CardType> value) {
    return value.map(CardType::toString).orElse(null);
  }

  default String mapPrepaid(Optional<CardPrepaidType> value) {
    return value.map(CardPrepaidType::toString).orElse(null);
  }

  default Long mapLong(Optional<Long> value) {
    return value.orElse(null);
  }

  default Boolean unwrapBoolean(Optional<Boolean> value) {
    return value.orElse(null);
  }

  default String mapCardBrand(Optional<CardBrand> value) {
    return value.map(CardBrand::toString).orElse(null);
  }

  @Named("unwrapCardDetails")
  default CardDetails unwrapOptionalCardDetails(Optional<CardPaymentDetails> cardPaymentDetails) {
    return cardPaymentDetails.map(this::toCardDetails).orElse(null);
  }
}
