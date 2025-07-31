package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.enumeration.ProducerSubProcessType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@Entity
@Builder
@Table(name = "producer_subscription_process", schema = "greenyp")
@NoArgsConstructor
@AllArgsConstructor
public class ProducerSubscriptionProcess extends Mutable {

  @NotNull
  @Column(name = "producer_id", nullable = false, length = 16)
  private UUID producerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "process_step", length = 50)
  private ProducerSubProcessType processStep;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProducerSubscriptionProcess that = (ProducerSubscriptionProcess) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(producerId, that.producerId).append(processStep, that.processStep).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(producerId).append(processStep).toHashCode();
    }
}
