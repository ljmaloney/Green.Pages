package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.LocationHoursResponse;
import com.green.yp.producer.data.model.ProducerLocationHours;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerLocationHoursMapper {
  List<LocationHoursResponse> fromEntity(List<ProducerLocationHours> allByProducerLocationId);

  @Mapping(source = "entity.id", target = "locationHoursId")
  LocationHoursResponse fromEntity(ProducerLocationHours entity);
}
