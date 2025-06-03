package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer", schema = "greenyp")
public class Producer extends Mutable {
  @Column(name = "name", length = 60, nullable = false)
  private String name;

  @Column(name = "subscription_type")
  @Enumerated(EnumType.STRING)
  private ProducerSubscriptionType subscriptionType;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime cancelDate;

  @Column(name = "last_bill_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime lastBillDate;

  @Column(name = "last_bill_paid_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime lastBillPaidDate;

  @Column(name = "website_url", length = 150)
  private String websiteUrl;

  @Column(name = "icon_link_url")
  private String iconLink;

  @Column(name = "narrative", length = 512)
  private String narrative;

  @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
  private List<ProducerLineOfBusiness> linesOfBusiness;

  @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
  private List<ProducerSubscription> subscriptionList;

  public void addLineOfBusiness(ProducerLineOfBusiness lob) {
    if (linesOfBusiness == null) {
      linesOfBusiness = new ArrayList<>();
    }
    linesOfBusiness.add(lob);
  }

  public void addSubscription(ProducerSubscription subscription) {
    if (subscriptionList == null) {
      subscriptionList = new ArrayList<>();
    }
    subscriptionList.add(subscription);
  }
}
