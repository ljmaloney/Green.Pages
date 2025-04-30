package com.green.yp.common.data.embedded;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import lombok.*;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.annotation.ReadOnlyProperty;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@OptimisticLocking
@MappedSuperclass
public class Versioned {

  // Null when new so that SimpleJpaRepository::save does an insert (EntityManager::persist) without
  // a select statement (EntityManager::merge) when saving new entities
  @Version @ReadOnlyProperty private Long version;

  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime createDate =
      OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS); // To match column definition datetime(6)

  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime lastUpdateDate = createDate;

  @PrePersist
  public void onPrePersist() {
    if (createDate == null) createDate = OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS);
    setLastUpdateDate(getCreateDate());
  }

  @PreUpdate
  public void onPreUpdate() {
    setLastUpdateDate(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));
  }

  public void resetCreated() {
    setCreateDate(OffsetDateTime.now().truncatedTo(ChronoUnit.MICROS));
    setLastUpdateDate(getCreateDate());
  }
}
