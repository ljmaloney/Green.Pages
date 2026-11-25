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
- Spring Boot 4, Java 25
- Spock 2.4+ on Groovy 4.0.x (verify against the Boot 4 BOM), managed via BOMs
- Descriptive quoted-string test names
- Constructor injection for SUT; `@Subject` annotation
- `@Shared` only for expensive immutable fixtures (containers, static payloads)
- Keep tests isolated; no shared mutable state

## Directory and naming
- Place specs under `src/test/groovy/...` mirroring production packages
- Name files `{ClassName}Spec.groovy`
- Controller test data under `green.yp.backend/src/test/resources/data/controller/[testcase]/`, grouped by spec or test case with descriptive filenames
- Store readable JSON fixtures there and load them from the test classpath in Spock; use SQL scripts in the same directory for relational database seed and cleanup data
- WireMock fixtures under `src/test/resources/[testclassname]/servicename/[endpoint_httpstatus].json`
- Place other database seed data under `src/test/resources/data/`, grouped by test case

## Feature method structure
- **given:** setup input state, stubs, preconditions
- **when:** invoke one behavior
- **then:** assert outputs + interactions
- **where:** data tables for parameterized cases; use `@Unroll` with `#variable` interpolation

## MongoDB integration tests
- Declare `@Shared static MongoDBContainer mongo = new MongoDBContainer("mongo:8")`
- Wire with `@DynamicPropertySource`
- Seed via Spring Data repositories from classpath JSON fixtures under `src/test/resources/data/`
- Clean in `setup()` or `cleanup()`

## MySQL integration tests
- Use a Spring-managed `MySQLContainer` bean with `@ServiceConnection` and an available, application-compatible MySQL image
- Create the schema using application migrations or a test schema
- Seed and clean an isolated test database with classpath `@Sql` scripts in the relevant test-case directory before and after each feature; for controller tests use `data/controller/[testcase]/`
- Include Spring Boot Testcontainers integration, the Testcontainers MySQL module, a MySQL JDBC driver, and Spring JDBC; Docker must be running
- See [the MySQL seed-data example](assets/EXAMPLES.md#mysql-integration-spec-with-seed-data)

## WireMock integration tests
- `@Shared static WireMockServer` with dynamic port
- Override base URL with `@DynamicPropertySource`
- Fixture naming: `servicename/endpoint-token_httpstatus.json`

## Gradle inspection
Before generating Spock tests, verify the Gradle build has:
- `spock-bom` imported as a test dependency platform
- `spock-core` and `spock-spring` in `testImplementation`
- `wiremock` if mocking HTTP; `testcontainers:mongodb` + `testcontainers:spock` for container tests
- The Groovy plugin and `useJUnitPlatform()` for the test task

If any are missing, tell the user which ones and ask before adding them.

## Edge-case checklist
- Null/blank inputs and malformed IDs
- Missing data (`Optional.empty()`, empty lists)
- Downstream failures (timeouts, 4xx/5xx)
- Concurrency-sensitive paths
- Pagination/sorting boundaries

See [assets/EXAMPLES.md](assets/EXAMPLES.md) for core unit test template, data tables, Spring Boot integration spec, JSON fixture helper, WireMock patterns (inline, fixture files, parameterized), MongoDB integration specs, interaction testing, and Gradle dependencies.

## Definition of done
- [ ] Spec follows `{ClassName}Spec.groovy` naming
- [ ] SUT annotated with `@Subject`, constructed explicitly
- [ ] Uses `given:`/`when:`/`then:` blocks
- [ ] Covers at least one happy path and one edge/failure path
- [ ] Verifies important collaborator interactions
- [ ] `@Shared` used only where lifecycle/performance justifies it
