package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.CreateProducerRequest;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.api.apitype.producer.ProducerSubscriptionResponse;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerLineOfBusiness;
import com.green.yp.producer.data.record.ProducerSubscriptionRecord;
import com.green.yp.reference.dto.LineOfBusinessDto;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerMapper {
    @Mapping(source = "newProducer.businessName", target = "name")
    Producer toEntity(CreateProducerRequest newProducer);

    @Mapping(source = "savedProducer.id", target = "producerId")
    @Mapping(source = "savedProducer.name", target = "businessName")
    @Mapping(source = "savedProducer.createDate", target = "createDate")
    @Mapping(source = "savedProducer.lastUpdateDate", target = "lastUpdateDate")
    @Mapping(source = "lineOfBusinessDto.lineOfBusinessId", target = "lineOfBusinessId")
    ProducerResponse fromEntity(Producer savedProducer, LineOfBusinessDto lineOfBusinessDto);

    @Mapping(source = "savedProducer.id", target = "producerId")
    @Mapping(source = "savedProducer.name", target = "businessName")
    @Mapping(source = "savedProducer.createDate", target = "createDate")
    @Mapping(source = "savedProducer.lastUpdateDate", target = "lastUpdateDate")
    @Mapping(source = "primaryLob.lineOfBusinessId", target = "lineOfBusinessId")
    @Mapping(source = "subscriptions", target = "subscriptions")
    ProducerResponse fromEntity(
            Producer savedProducer,
            ProducerLineOfBusiness primaryLob,
            List<ProducerSubscriptionResponse> subscriptions);

    List<ProducerSubscriptionResponse> fromRecord(List<ProducerSubscriptionRecord> subscriptions);

    @Mapping(source = "producerSubscription.subscriptionId", target = "subscriptionId")
    @Mapping(source = "producerSubscription.id", target = "producerSubscriptionId")
    @Mapping(source = "subscription.displayName", target = "displayName")
    @Mapping(source = "subscription.shortDescription", target = "shortDescription")
    @Mapping(source = "producerSubscription.invoiceCycleType", target = "invoiceCycleType")
    @Mapping(source = "subscription.subscriptionType", target = "subscriptionType")
    @Mapping(source = "producerSubscription.nextInvoiceDate", target = "nextInvoiceDate")
    @Mapping(source = "producerSubscription.startDate", target = "startDate")
    @Mapping(source = "producerSubscription.endDate", target = "endDate")
    @Mapping(target = "subscriptionAmount", source = "subscriptionRecord", qualifiedByName = "toPaymentAmount")
    ProducerSubscriptionResponse fromRecord(ProducerSubscriptionRecord subscriptionRecord);

    @Named("toPaymentAmount")
    default BigDecimal toPaymentAmount(ProducerSubscriptionRecord subscriptionRecord) {
        return switch (subscriptionRecord.producerSubscription().getInvoiceCycleType()) {
            case MONTHLY -> subscriptionRecord.subscription().getMonthlyAutopayAmount();
            case QUARTERLY -> subscriptionRecord.subscription().getQuarterlyAutopayAmount();
            case ANNUAL, NONRECURRING_ANNUAL -> subscriptionRecord.subscription().getAnnualBillAmount();
        };
    }

    @Mapping(source = "producer.id", target = "producerId")
    @Mapping(source = "producer.name", target = "businessName")
    @Mapping(source = "producer.createDate", target = "createDate")
    @Mapping(source = "producer.lastUpdateDate", target = "lastUpdateDate")
    ProducerResponse fromEntity(Producer producer);

    List<ProducerResponse> fromEntity(List<Producer> producers);
}
