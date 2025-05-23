package com.green.yp.reference.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.reference.data.enumeration.LineOfBusinessCreateType;
import jakarta.persistence.*;
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
    private String lineOfBusinessName;

    @Column(name = "created_by_type")
    @Enumerated(EnumType.STRING)
    private LineOfBusinessCreateType createType;

    @Column(name = "create_by_reference")
    private String createByReference;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "enable_distance_radius")
    @Convert(converter = BooleanConverter.class)
    private Boolean enableDistanceRadius;

    @Column(name="icon_name")
    private String iconName;

    @Column(name="icon_file_name")
    private String iconFileName;

    @Column(name="short_description", length = 200)
    private String shortDescription;
}
