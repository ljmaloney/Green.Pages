package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.LocationHoursResponse;
import com.green.yp.api.apitype.producer.LocationRequest;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.model.ProducerLocationHours;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerLocationMapper {
  @Mapping(source = "producerId", target = "producerId")
  ProducerLocation toEntity(LocationRequest createLocationRequest, @NotNull UUID producerId);

  ProducerLocation toEntity(LocationRequest createLocationRequest);

  @Mapping(source = "location.id", target = "locationId")
  ProducerLocationResponse fromEntity(ProducerLocation location);

  @Mapping(source = "entity.id", target = "locationHoursId")
  LocationHoursResponse fromEntity(ProducerLocationHours entity);

  List<ProducerLocationResponse> fromEntity(List<ProducerLocation> producerLocations);
}
