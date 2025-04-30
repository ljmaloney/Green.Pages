package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.ProducerCredentialsResponse;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.producer.data.model.ProducerUserCredentials;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerAuthUserMapper {

    @Mapping(source = "request.userName", target = "userId")
    ProducerUserCredentials toEntity(UserCredentialsRequest request);

    @Mapping(source = "credentials.userId", target = "userName")
    @Mapping(source = "credentials.id", target = "credentialsId")
    ProducerCredentialsResponse fromEntity(ProducerUserCredentials credentials);
}
