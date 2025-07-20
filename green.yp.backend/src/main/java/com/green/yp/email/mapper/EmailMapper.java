package com.green.yp.email.mapper;

import com.green.yp.api.apitype.email.EmailValidationResponse;
import com.green.yp.email.data.model.EmailValidation;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmailMapper {

  @Mapping(target = "validationId", source = "id")
  @Mapping(target = "token", source = "emailToken")
  EmailValidationResponse fromEntity(@NotNull EmailValidation emailValidation);
}
