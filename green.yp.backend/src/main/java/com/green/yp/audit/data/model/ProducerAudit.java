package com.green.yp.audit.data.model;

import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.common.data.embedded.Immutable;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "producer_audit", schema = "greenyp")
public class ProducerAudit extends Immutable {

    @Column(name = "producer_id")
    private UUID producerId;

    @Enumerated(EnumType.STRING)
    private AuditActionType actionType;

    @Enumerated(EnumType.STRING)
    private AuditObjectType objectType;

    @Column(name = "class_name")
    private String className;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "action_ref_id")
    private String actionRefId;

    @Column(name = "action_email_address")
    private String actionEmailAddress;

    @Column(name = "object_data")
    private String objectData;

    @Column(name = "object_data_encrypted")
    //    @Convert(converter = CryptoConverter.class)
    private String objectDataEncrypted;
}
