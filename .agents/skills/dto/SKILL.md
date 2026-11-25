---
name: dto
description: 'Use when creating or refactoring a REST request DTO - Java record with jakarta.validation constraints (@NotBlank, @Size, @Email, @DecimalMin, @Digits), UUID/BigDecimal field types. Not for response wrappers or controllers (use spring-controller).'
---
# Skill: request-dto

## When to use this skill
- Creating request DTOs for new REST endpoints
- Refactoring mutable request POJOs into immutable Java records
- Adding or tightening field-level validation for request payloads

## Core rules
- Use Java `record` for request DTOs
- Use `@NotBlank` for required `String` fields; `@NotNull` for other required non-nullable fields
- Use `@Size` for `String` length constraints; fallback to `@Size(max = 150)` only when no constraint is known
- Use `@Email` for email fields
- Use `@DecimalMin`/`@DecimalMax` for numeric ranges; `@Digits` for precision
- Prefer `UUID` over `String` for UUID fields
- Always use `BigDecimal` for currency fields (never `Double`/`Float`)

## Validation mapping

| Field type / intent | Required annotation(s) | Notes |
|---|---|---|
| Required `String` | `@NotBlank`, `@Size(max = 150)` | `150` is fallback only; use user-specified max when provided |
| Optional `String` with length | `@Size(max = 150)` | Fallback only |
| Required non-`String` object | `@NotNull` | Enums, nested objects, collections, dates, UUID |
| Email | `@Email` (+ `@NotBlank` if required) | Combine with `@Size` if length caps exist |
| Integer/Long/Short number | `@NotNull`, `@DecimalMin`, `@DecimalMax`, `@Digits(integer=..., fraction=0)` | Prompt for range |
| Decimal number | `@NotNull`, `@DecimalMin`, `@DecimalMax`, `@Digits(integer=..., fraction=...)` | Prompt for range |
| Currency field | `BigDecimal`, `@NotNull`, `@DecimalMin`, `@DecimalMax`, `@Digits(integer=..., fraction=2)` | Never `Double`/`Float` |
| UUID identifier | `UUID` (+ `@NotNull` if required) | |

## Domain-specific field rules

Field-name semantics drive stronger defaults. Each field type's full annotation set is documented in [assets/FIELD-VALIDATIONS.md](assets/FIELD-VALIDATIONS.md). Summary:

| Field | Key constraints |
|---|---|
| `firstName`, `lastName` | `@NotBlank`, `@Size(max=150)`, multiple `@Pattern` for alpha, spacing, casing |
| `city` | `@NotBlank`, `@Pattern` for alpha/spaces/hyphens, no leading/trailing/consecutive spaces, capital start |
| `state` | `@Size(min=2, max=2)`, `@Pattern(regexp="^[A-Z]+$")` |
| `postalCode`/`zipCode`/`zip` | `@Size(min=5, max=10)`, `@Pattern(regexp="^[0-9]{5}(?:-[0-9]{4})?$")` |
| `addressLine1`/`addressLine2` | `@Size(max=150)`, `@Pattern(regexp="^[0-9A-Za-z\\s\\-.,']+$")` |
| Currency (`amount`, `price`, etc.) | `BigDecimal`, `@DecimalMin`, `@DecimalMax`, `@Digits(fraction=2)` |

## Prompting rules
- If no explicit string size constraint is provided and no domain-specific rule applies, apply fallback `@Size(max = 150)`
- If any size bound is specified by the user, use exact values -- do not default to 150
- If only one bound is provided, prompt for the missing bound
- For numeric fields without range, prompt for min/max before finalizing
- For unspecified `String` fields (no domain rule), prompt: (1) alpha-numeric only? (2) which additional chars? (3) no pattern needed?

## Recommended DTO shape
- Keep DTOs focused on endpoint input only
- Prefer nested records for structured child objects
- No business logic inside DTOs
- Annotations on record components

See [assets/EXAMPLES.md](assets/EXAMPLES.md) for a full `CreateCustomerRequest` record example with nested `Address`.
See [assets/FIELD-VALIDATIONS.md](assets/FIELD-VALIDATIONS.md) for complete annotation sets per field type.

## Checklist
- [ ] DTO is a Java `record`
- [ ] Required strings use `@NotBlank`; non-strings use `@NotNull`
- [ ] String fields use `@Size` (default 150 fallback)
- [ ] Generic strings prompted for pattern before adding `@Pattern`
- [ ] Email fields use `@Email`
- [ ] Numeric ranges use `@DecimalMin`/`@DecimalMax`/`@Digits`
- [ ] Currency fields use `BigDecimal` with `@Digits(fraction=2)`
- [ ] UUID fields use `UUID` type
- [ ] Missing bounds clarified with user before final output
