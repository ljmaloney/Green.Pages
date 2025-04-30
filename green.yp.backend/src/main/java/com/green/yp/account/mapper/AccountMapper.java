package com.green.yp.account.mapper;

import com.green.yp.api.apitype.account.CreateAccountRequest;
import com.green.yp.api.apitype.account.CreateAccountResponse;
import com.green.yp.api.apitype.account.UpdateAccountRequest;
import com.green.yp.api.apitype.producer.*;
import java.util.UUID;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AccountMapper {
  CreateProducerRequest toProducer(CreateAccountRequest account);

  ProducerRequest toProducer(UpdateAccountRequest accountRequest);

  CreateAccountResponse fromProducer(ProducerResponse producerResponse);

  @Mapping(source = "locationId", target = "locationId")
  LocationRequest copyRequest(LocationRequest request, UUID locationId);

  @Mapping(source = "locationId", target = "producerLocationId")
  @Mapping(source = "contactId", target = "contactId")
  ProducerContactRequest copyRequest(
      ProducerContactRequest request, UUID contactId, UUID locationId);
}
