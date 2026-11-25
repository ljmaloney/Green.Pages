---
name: spock-testing
description: Write Groovy - Spock specifications (not Junit) for testing Spring Boot code. Use when the user asks for Spock Tests, Groovy Tests, or specifications. For Java JUnit tests, use integration testing instead. 
---
# Skill: spock-testing

## When to use this skill
- Writing unit tests for services, mappers, utilities, or validators
- Migrating JUnit/Mockito tests to Spock
- Adding data-driven tests for input validation and edge cases
- Testing Spring MVC controllers with Spock + MockMvc
- Creating integration tests with Testcontainers or WireMock

## Baseline conventions
- Spring Boot 4, Java 25 (`ms-spring-boot-parent` 2.x)
- Spock 2.4+ on Groovy 4.0.x (verify against the Boot 4 BOM), managed via BOMs
- Descriptive quoted-string test names
- Constructor injection for SUT; `@Subject` annotation
- `@Shared` only for expensive immutable fixtures (containers, static payloads)
- Keep tests isolated; no shared mutable state

## Directory and naming
- Place specs under `src/test/groovy/...` mirroring production packages
- Name files `{ClassName}Spec.groovy`
- JSON fixtures under `src/test/resources/[testclassname]/`
- WireMock fixtures under `src/test/resources/[testclassname]/servicename/[endpoint_httpstatus].json`
- MongoDB seed data under `src/test/resources/data/`

## Feature method structure
- **given:** setup input state, stubs, preconditions
- **when:** invoke one behavior
- **then:** assert outputs + interactions
- **where:** data tables for parameterized cases; use `@Unroll` with `#variable` interpolation

## MongoDB integration tests
- Declare `@Shared static MongoDBContainer mongo = new MongoDBContainer("mongo:8")`
- Wire with `@DynamicPropertySource`
- Seed via Spring Data repositories from `src/test/resources/data/*.json`
- Clean in `setup()` or `cleanup()`

## WireMock integration tests
- `@Shared static WireMockServer` with dynamic port
- Override base URL with `@DynamicPropertySource`
- Fixture naming: `servicename/endpoint-token_httpstatus.json`

## POM inspection
Before generating Spock tests, verify Maven has:
- `spock-bom` in `<dependencyManagement>`
- `spock-core` and `spock-spring` with `<scope>test</scope>`
- `wiremock` if mocking HTTP; `testcontainers:mongodb` + `testcontainers:spock` for container tests
- Groovy test compiler plugin if not already configured

If any are missing, tell the user which ones and ask before adding them.

## Edge-case checklist
- Null/blank inputs and malformed IDs
- Missing data (`Optional.empty()`, empty lists)
- Downstream failures (timeouts, 4xx/5xx)
- Concurrency-sensitive paths
- Pagination/sorting boundaries

See [assets/EXAMPLES.md](assets/EXAMPLES.md) for core unit test template, data tables, Spring Boot integration spec, JSON fixture helper, WireMock patterns (inline, fixture files, parameterized), MongoDB integration specs, interaction testing, and Maven dependencies.

## Definition of done
- [ ] Spec follows `{ClassName}Spec.groovy` naming
- [ ] SUT annotated with `@Subject`, constructed explicitly
- [ ] Uses `given:`/`when:`/`then:` blocks
- [ ] Covers at least one happy path and one edge/failure path
- [ ] Verifies important collaborator interactions
- [ ] `@Shared` used only where lifecycle/performance justifies it
