# JPA entity examples from Green Pages

These excerpts show existing mappings, not complete standalone classes. Inspect the source and current schema before adapting them. Paths below are relative to `green.yp.backend/src/main/java/com/green/yp/`.

## UUID, versioning, and converted boolean

`classifieds/data/model/ClassifiedCategory.java` extends `common/data/embedded/Mutable`, which supplies a UUID ID and inherits `Versioned`'s version and timestamps:

```java
@Entity
@Table(name = "classified_category")
public class ClassifiedCategory extends Mutable {
  @Column(name = "active", length = 1, nullable = false)
  @Convert(converter = BooleanConverter.class)
  private Boolean active;

  @NotNull
  @Column(name = "name", length = 75, nullable = false)
  private String name;
}
```

Check `BooleanConverter` and the actual column before using the same conversion. `Mutable` initializes the ID in Java; `Versioned` uses a nullable `@Version` and lifecycle callbacks. Do not assume database-generated IDs or overwrite a managed version.

## JPA converters

Domain-specific converters belong in `com.green.yp.[domain].data.converter`, such as `com.green.yp.producer.data.converter.ProductAttributesConverter`. Place reusable converters in separate classes under `com.green.yp.common.data.converter`. The existing shared converters include:

- `BooleanConverter`: maps `true` to `Y` and `false` or null to `N`; reading `Y` (case-insensitive) returns `true`, and other values including null return `false`. It uses `@Converter(autoApply = true)`.
- `JsonMapConverter`: converts `Map<String, Object>` to and from JSON strings, preserving null; conversion failures raise `SystemException`. It is not auto-applied, so select it explicitly with `@Convert` when appropriate.

```java
import com.green.yp.common.data.converter.JsonMapConverter;
import jakarta.persistence.Convert;
import java.util.Map;

@Convert(converter = JsonMapConverter.class)
private Map<String, Object> metadata;
```

This is an illustrative field, not an existing entity mapping. Verify the column representation, null behavior, and converter auto-application before adopting it.

## Enum, monetary amount, and child collection

`invoice/data/model/Invoice.java` contains these mappings:

```java
@Enumerated(EnumType.STRING)
@Column(name = "invoice_type", nullable = false, length = 50)
private InvoiceType invoiceType;

@NotNull
@Column(name = "invoice_total", nullable = false, precision = 12, scale = 2)
private BigDecimal invoiceTotal;

@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
private List<InvoiceLineItem> lineItems;
```

The child owns the relationship via its `invoice` field. The existing `addLineItem` updates the collection and total but does not set the child's invoice; callers must ensure the owning side is set. For new helpers, maintain both sides and test persistence after flush and clear. Choose cascade and orphan removal deliberately rather than copying them unconditionally.

## Composite identifier and read-only association

`producer/data/model/ProducerLineOfBusiness.java` uses a composite key and maps the producer join column twice, with only the ID field writable:

```java
@Entity
@IdClass(ProducerLineOfBusinessId.class)
@Table(name = "producer_line_of_business", schema = "greenyp")
public class ProducerLineOfBusiness {
  @Id
  @Column(name = "producer_id")
  private UUID producerId;

  @Id
  @Column(name = "line_of_business_id", length = 50)
  private UUID lineOfBusinessId;

  @ManyToOne
  @JoinColumn(name = "producer_id", insertable = false, updatable = false)
  private Producer producer;
}
```

`ProducerLineOfBusinessId` is serializable and declares matching UUID fields. Assigning `producer` alone does not write `producer_id`; populate the key fields and keep the association consistent. Verify the actual schema rather than inferring the UUID column type from the length annotation.

## Lifecycle and collection caution

`reference/data/model/Subscription.java` uses `@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)` and assigns the owning side in lifecycle callbacks. Its `onPrePersist` calls the superclass, while `onPreUpdate` does not; do not copy this override without considering the inherited timestamp callback. Eager loading and orphan removal have performance and deletion consequences; test the intended behavior before adopting either.

Several legacy entities include mutable properties or collections in equality and hash code. Treat these as historical implementations, not guidance for new entities: changing a hash code while an entity is in a hash-based collection and traversing lazy associations can cause problems.