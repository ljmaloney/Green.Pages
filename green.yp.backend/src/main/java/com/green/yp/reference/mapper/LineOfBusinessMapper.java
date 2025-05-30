package com.green.yp.reference.mapper;

import com.green.yp.api.apitype.CreateLobServiceRequest;
import com.green.yp.reference.data.model.LOBService;
import com.green.yp.reference.data.model.LineOfBusiness;
import com.green.yp.reference.dto.LOBServiceDto;
import com.green.yp.reference.dto.LineOfBusinessDto;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LineOfBusinessMapper {
  @Mapping(source = "id", target = "lobServiceId")
  LOBServiceDto fromEntity(LOBService service);

  List<LOBServiceDto> fromEntity(List<LOBService> services);

  LOBService toEntity(CreateLobServiceRequest serviceRequest);

  List<LineOfBusinessDto> toDto(List<LineOfBusiness> lobList);

  LineOfBusiness fromDto(LineOfBusinessDto dto);

  @Mapping(source = "id", target = "lineOfBusinessId")
  LineOfBusinessDto toDto(LineOfBusiness lineOfBusiness);

  List<LineOfBusinessDto> toApi(List<LineOfBusinessDto> allLineOfBusiness);

  default LineOfBusiness fromDtoToEntity(@Valid @NotNull LineOfBusinessDto lineOfBusinessDto, LineOfBusiness lineOfBusiness){
    lineOfBusiness.setId(lineOfBusinessDto.lineOfBusinessId());
    lineOfBusiness.setLineOfBusinessName(lineOfBusinessDto.lineOfBusinessName());
    lineOfBusiness.setCreateType(lineOfBusinessDto.createType());
    lineOfBusiness.setCreateByReference(lineOfBusinessDto.createByReference());
    lineOfBusiness.setShortDescription(lineOfBusinessDto.shortDescription());
    lineOfBusiness.setDescription(lineOfBusinessDto.description());
    lineOfBusiness.setEnableDistanceRadius(lineOfBusinessDto.enableDistanceRadius());
    lineOfBusiness.setIconName(lineOfBusinessDto.iconName());
    lineOfBusiness.setIconFileName(lineOfBusinessDto.iconFileName());
    return lineOfBusiness;
  }
}
