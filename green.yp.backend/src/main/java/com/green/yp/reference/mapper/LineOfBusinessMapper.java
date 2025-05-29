package com.green.yp.reference.mapper;

import com.green.yp.api.apitype.CreateLobServiceRequest;
import com.green.yp.reference.data.model.LOBService;
import com.green.yp.reference.data.model.LineOfBusiness;
import com.green.yp.reference.dto.LOBServiceDto;
import com.green.yp.reference.dto.LineOfBusinessDto;
import java.util.List;
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

  @Mapping(source = "lineOfBusiness.id", target = "lineOfBusinessId")
  LineOfBusinessDto toDto(LineOfBusiness lineOfBusiness);

  List<LineOfBusinessDto> toApi(List<LineOfBusinessDto> allLineOfBusiness);
}
