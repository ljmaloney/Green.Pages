package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.enumeration.CancelReasonType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer", schema = "greenyp")
public class Producer extends Mutable {
  @Column(name = "name", length = 150, nullable = false)
  private String name;

  @Column(name = "subscription_type")
  @Enumerated(EnumType.STRING)
  private ProducerSubscriptionType subscriptionType;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime cancelDate;

  @Column(name = "cancel_type")
  @Enumerated(EnumType.STRING)
  private CancelReasonType cancelReasonType;

  @Column(name = "cancel_reason")
  private String cancelReason;

  @Column(name = "last_bill_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime lastBillDate;

  @Column(name = "last_bill_paid_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime lastBillPaidDate;

  @Column(name = "has_images_uploaded")
  @Convert(converter = BooleanConverter.class)
  Boolean hasImagesUploaded;

  @Column(name = "website_url", length = 150)
  private String websiteUrl;

  @Column(name = "icon_link_url")
  private String iconLink;

  @Column(name = "keywords")
  private String keywords;

  @Column(name = "narrative", length = 1024)
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Producer producer = (Producer) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(name, producer.name)
        .append(subscriptionType, producer.subscriptionType)
        .append(cancelDate, producer.cancelDate)
        .append(lastBillDate, producer.lastBillDate)
        .append(lastBillPaidDate, producer.lastBillPaidDate)
        .append(hasImagesUploaded, producer.hasImagesUploaded)
        .append(websiteUrl, producer.websiteUrl)
        .append(iconLink, producer.iconLink)
        .append(narrative, producer.narrative)
        .append(linesOfBusiness, producer.linesOfBusiness)
        .append(subscriptionList, producer.subscriptionList)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(name)
        .append(subscriptionType)
        .append(cancelDate)
        .append(lastBillDate)
        .append(lastBillPaidDate)
        .append(hasImagesUploaded)
        .append(websiteUrl)
        .append(iconLink)
        .append(narrative)
        .append(linesOfBusiness)
        .append(subscriptionList)
        .toHashCode();
  }

    public ProducerLineOfBusiness getPrimaryLineOfBusiness() {
      return linesOfBusiness.stream().filter(ProducerLineOfBusiness::getPrimaryLob)
              .findFirst().orElse(null);
    }
}
