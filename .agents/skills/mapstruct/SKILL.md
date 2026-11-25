---
name: mapstruct
description: 'Use when adding or changing a MapStruct @Mapper - entity/DTO/generated-model conversion, @Named qualifiers, @ValueMapping enum bridging, @MappingTarget partial updates, @AfterMapping hooks, or debugging unmapped-target warnings. Never hand-write conversions that MapStruct can generate.'
---

# MapStruct Patterns

## When to use this skill
- You need to add or adjust transformations between Mongo entities, REST payloads, or external partner models.
- You are wiring new request/response DTOs into existing services and want to follow the project’s mapper conventions.
- You must debug or extend generated mapper implementations without rewriting boilerplate by hand.

## Core conventions observed in the repo
- **Spring components**: every mapper declares `@Mapper(componentModel = SPRING)` so implementations are injectable beans.
- **Shared helpers via `uses`**: complex mappers delegate to smaller helpers (`uses = { ... }`) instead of duplicating logic.
- **Qualified defaults**: reusable transformations (masking fields, deriving enum values, formatting ZIP+4) live in `@Named` methods referenced through `qualifiedByName`.
- **Enum bridging**: `@ValueMapping` / `@ValueMappings` translate between internal enums and partner enum values while catching unmapped cases.
- **Partial updates**: methods that merge requests into existing entities accept `@MappingTarget` parameters, and `IGNORE` for unmapped targets only on those update methods. Default to `unmappedTargetPolicy = ERROR` elsewhere so a new field can't be silently dropped.
- **Lombok + MapStruct**: add `lombok-mapstruct-binding` to `annotationProcessorPaths` and list Lombok before MapStruct, otherwise mappers silently generate empty/incomplete code.
- **Post-processing hooks**: `@AfterMapping` (default interface methods) adjust balances, totals, or nested collections when pure declarative mapping isn’t enough.
- **Manual helpers alongside MapStruct**: abstract mappers sometimes include imperative builders (e.g., constructing `Line` aggregates) while still benefiting from MapStruct for nested mappings.

## Java 25 / Boot 4 notes
- MapStruct maps to and from records natively (constructor mapping); prefer record DTOs and models over Lombok classes.
- Use a recent MapStruct (1.6+) managed by the parent BOM; do not pin versions.
- For sealed hierarchies, use `@SubclassMapping` instead of manual `instanceof` chains.

## Implementation playbook
1. **Define/extend the mapper interface**
   - Annotate with `@Mapper(componentModel = SPRING, uses = {...})`.
   - Import constants/enums with static imports to keep annotations readable.
2. **Declare mappings explicitly**
   - Use `@Mapping` for any field renames, nested properties, or qualifier usage.
   - Apply `qualifiedByName` to call helper methods (masking, formatting) rather than reimplementing logic.
3. **Handle enums intentionally**
   - Add `@ValueMappings` when an enum maps to another enum/string. Include an `ANY_REMAINING -> NULL` rule when unmatched values should be ignored.
4. **Support partial updates**
   - For PATCH-style operations, accept `@MappingTarget` parameters and map into existing entities. Combine with helper mappers to keep methods succinct.
5. **Use `@AfterMapping` when calculations are required**
   - Adjust derived totals (balances, finance amounts, etc.) after the declarative mapping completes.
6. **Regenerate implementations**
   - Run `mvn compile` (or your IDE’s build) so the MapStruct processor generates new `*Impl` classes under `target/generated-sources/annotations`.

## Debugging tips
- Build failures will cite the exact mapping that’s missing or ambiguous—read the MapStruct error message and add the required `@Mapping`/`@ValueMapping` directive.
- If qualifier methods aren’t being picked up, ensure they’re annotated with `@Named` and referenced via `qualifiedByName`.
- When collections aren’t updating as expected, confirm the target accessor (`setX`/`getX`) exists or provide custom merge logic in a default method.

## Adding a new mapper-driven feature
1. Model the new DTO/entity fields with Lombok and validation annotations.
2. Update the relevant mapper interface to translate between the new types.
3. Inject the mapper into the service layer (Spring will supply the generated implementation).
4. Compile and verify downstream payloads (REST responses, partner requests) using integration or contract tests.

## References
- MapStruct dependencies are declared in the build (`mapstruct` + `mapstruct-processor`) and wired through the compiler plugin.
- Generated implementations follow the `${MapperName}Impl` naming convention inside the same package hierarchy.

