package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.ProducerServiceResponse;
import com.green.yp.api.apitype.producer.ProducerServiceRequest;
import com.green.yp.producer.data.model.ProducerService;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerServiceMapper {
    ProducerService toEntity(ProducerServiceRequest serviceRequest);

    @Mapping(source = "producerService.id", target = "producerServiceId")
    ProducerServiceResponse fromEntity(ProducerService producerService);

    List<ProducerServiceResponse> fromEntity(List<ProducerService> services);
}
