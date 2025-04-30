package com.green.yp.common.data.embedded;

import static java.time.temporal.ChronoUnit.MICROS;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
// @NoArgsConstructor // required for Hibernate but shouldn't be used otherwise
@MappedSuperclass
public class Immutable {

  @Id private UUID id = UUID.randomUUID();

  // Null when new so that SimpleJpaRepository::save does an insert (EntityManager::persist) without
  // a select statement (EntityManager::merge) when saving new entities
  //    @Column(nullable = false, updatable = false)
  //    @Version
  //    @ReadOnlyProperty
  //    private Long version;

  @Column(nullable = false, updatable = false)
  @CreatedDate
  @JsonIgnore
  private OffsetDateTime createDate =
      OffsetDateTime.now().truncatedTo(MICROS); // To match column definition datetime(6)

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Immutable immutable = (Immutable) o;
    return getId() != null && Objects.equals(getId(), immutable.getId());
  }

  @Override
  public int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
