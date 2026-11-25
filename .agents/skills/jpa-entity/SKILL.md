---
name: jpa-entity
description: Create, review, and manage Jakarta Persistence (JPA) entities, identifiers, mappings, relationships, and lifecycle behavior in Spring Boot applications.
---

# JPA Entity Patterns

## When to use this skill
- Creating or changing JPA entities, mapped superclasses, composite IDs, converters, or relationships.
- Investigating persistence, entity lifecycle, optimistic locking, or mapping/schema mismatches.
- Coordinating entity changes with Spring Data repositories and Flyway migrations.

## Workflow
1. Inspect the relevant entities under `green.yp.backend/src/main/java/com/green/yp/**/data/model/`, their repositories, callers, tests, and current database schema. Not every class in a model directory is an entity.
2. Identify the ID, columns, constraints, existing data, relationship ownership, and lifecycle requirements. Compare mappings with the effective schema and configuration, not just the first migration.
3. Implement the smallest compatible change using `jakarta.persistence` and local conventions. Extend `Mutable` for entities expected to change over time, and `Immutable` for entities not expected to change; check the superclass ID, timestamps, version, and equality behavior against the existing schema.
4. If the schema must change, add a new forward Flyway migration in the configured location (typically `green.yp.backend/src/main/resources/db/migration/`); do not edit migrations already applied in shared environments. Review existing data, constraints, deployment compatibility, and MySQL DDL behavior.
5. Test persistence against an isolated MySQL database with migrations applied. Flush and clear the persistence context before reloading so tests exercise actual database mappings; cover constraints, relationships, updates, and concurrency as applicable.

## Mapping guidelines
- Declare `@Entity` and the correct `@Table`; verify schema qualifiers against deployment configuration. Supply a public or protected no-argument constructor, including when using Lombok builders.
- Match `@Column` names, length, nullability, precision, and scale to the schema. Bean Validation constraints and database constraints serve different purposes; align both where required.
- Extend `Mutable` when an entity is expected to mutate over time; extend `Immutable` when it is not. `Mutable` supplies an application-generated UUID and inherits `Versioned`; `Immutable` independently supplies a UUID and creation timestamp. `Immutable` does not itself enforce immutability; design the entity and its usage accordingly.
- Preserve existing UUID storage representation and ID generation. Check schema compatibility before changing an existing entity's superclass; composite identifiers and legacy mappings may require a different approach.
- `Versioned` has a nullable `@Version` and timestamp callbacks. Preserve optimistic-lock semantics and check overridden lifecycle callbacks invoke superclass behavior when needed. Test stale updates when changing versioning.
- Persist enums and booleans in the representation already used by the database: `EnumType.STRING` and the project's `BooleanConverter` appear in existing models. Verify converter behavior and historical data before changing representations.
- Place domain-specific JPA converters in `com.green.yp.[domain].data.converter`. Create reusable converters as separate classes under `com.green.yp.common.data.converter`; reuse the existing `BooleanConverter` and `JsonMapConverter` where their behavior matches the mapping. Check auto-apply scope, null handling, and existing stored values before using a converter.
- Place projection interfaces and records in `com.green.yp.[domain].data.record`, replacing `[domain]` with the actual subdomain, such as `classifieds`, `producer`, `payment`, `invoice`, or `message`.
- For composite IDs, keep `@IdClass` field names and types aligned with entity ID fields and implement serializable, stable equality and hash code.
- Identify the owning side of each association. Maintain both sides in application helpers, specify cascades and orphan removal only when their lifecycle implications are intended, and choose fetch strategies based on use cases and query cost. Avoid serializing lazy entities directly across API boundaries.
- Avoid mutable fields or relationships in new entity equality, hash code, and `toString` implementations; consider generated IDs and Hibernate proxies. Existing entity implementations differ and should not be copied blindly.
- Use transactions at the appropriate service boundary and avoid accidental lazy access outside it. Use MapStruct for DTO mapping where appropriate, without triggering unnecessary relationship loads.

## Verification checklist
- [ ] Mapping, ID representation, constraints, and migration agree with the effective schema.
- [ ] Insert, reload, update, and relevant failure and relationship cases are verified with an isolated database.
- [ ] Optimistic locking, callback overrides, equality, and fetch/cascade behavior are considered where relevant.
- [ ] Targeted Gradle tests were run, or any verification limitations were reported.

See [assets/EXAMPLES.md](assets/EXAMPLES.md) for examples from this project's entities. For repository and migration operations, also consult the `mysql-repository` skill; for Groovy Spock specifications, consult `spock-testing`.