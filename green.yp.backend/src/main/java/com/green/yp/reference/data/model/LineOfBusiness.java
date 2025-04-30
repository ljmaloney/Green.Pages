package com.green.yp.reference.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.reference.data.enumeration.LineOfBusinessCreateType;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "line_of_business", schema = "greenyp")
public class LineOfBusiness extends Mutable {

    @Column(name = "line_of_business", unique = true, nullable = false, length = 50)
    private String lineOfBusiness;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_by_type")
    @Enumerated(EnumType.STRING)
    private LineOfBusinessCreateType createType;

    @Column(name = "create_by_reference")
    private String createByReference;

    @Column(name = "enable_distance_radius")
    @Convert(converter = BooleanConverter.class)
    private Boolean enableDistanceRadius;

    public LineOfBusiness(UUID id,
                          OffsetDateTime createDate,
                          OffsetDateTime lastUpdateDate,
                          String lineOfBusiness,
                          String description,
                          LineOfBusinessCreateType createType,
                          String createByReference,
                          Boolean enableDistanceRadius) {
        super.setId(id);
        super.setCreateDate(createDate);
        super.setLastUpdateDate(lastUpdateDate);
        this.lineOfBusiness = lineOfBusiness;
        this.description = description;
        this.createType = createType;
        this.createByReference = createByReference;
        this.enableDistanceRadius = enableDistanceRadius;
    }
}
