package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.producer.enumeration.ProducerProductType;
import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer_product", schema = "greenyp")
public class ProducerProduct extends Mutable {
    @NotNull
    @NonNull
    @Column(name = "producer_id", nullable = false, updatable = false)
    private UUID producerId;

    @NotNull
    @NonNull
    @Column(name = "producer_location_id", nullable = false, updatable = false)
    private UUID producerLocationId;

    @NotNull
    @NonNull
    @Column(name = "product_type")
    @Enumerated(EnumType.STRING)
    ProducerProductType productType;

    @Column(name = "botanical_group", length = 50)
    private String botanicalGroup;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "available_quantity")
    private BigInteger availableQuantity;

    @Column(name = "container_size")
    private String containerSize;

    @Column(name = "discontinued")
    @Convert(converter = BooleanConverter.class)
    private Boolean discontinued;

    @Column(name = "discontinue_date")
    @Temporal(TemporalType.DATE)
    private LocalDate discontinueDate;

    @Column(name = "last_order_date")
    @Temporal(TemporalType.DATE)
    private LocalDate lastOrderDate;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "attributes", columnDefinition = "json")
    private Map<String, Object> attributes;

    @Lob
    @Column(name = "product_image")
    private byte[] productImage;

    public boolean isDiscontinued() {
        return Boolean.TRUE.equals(discontinued);
    }
}
