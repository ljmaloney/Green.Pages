---
name: mysql-repository
description: Manage MySQL-backed repositories, schemas, and data with standard MySQL commands, Spring Boot, Spring Data JPA, and Flyway. Use when inspecting or changing MySQL repositories, queries, mappings, or migrations.
---

# Skill: mysql-repository

## When to use this skill
- Investigating or changing Spring Data JPA repositories backed by MySQL
- Inspecting MySQL schema, data, indexes, or query performance
- Creating or reviewing Flyway migrations and JPA mappings

## Repository management workflow
1. Identify the requested behavior, entity, repository, schema, and target environment. Inspect nearby repositories, entities, migrations, tests, and configuration before making changes.
2. Confirm the database host, port, database, and permissions from the user or configuration; do not guess a connection or expose credentials. Start with read-only inspection. Obtain explicit approval before modifying a shared database or running destructive operations.
3. Inspect the relevant table definitions, constraints, indexes, and representative data. Check query plans and expected result size where performance matters.
4. Implement repository methods using existing project conventions. Coordinate entity changes with a new Flyway migration when the schema changes.
5. Test against an isolated MySQL database with the application's migrations applied. Cover successful and missing results, constraints, query boundaries, and any changed transaction behavior. Report commands, results, and remaining risks.

## Standard MySQL commands
Use a least-privileged account and an approved target. Prompt for the password rather than putting it in the command line; do not print secrets.

```bash
mysql --host=HOST --port=PORT --user=USER --password DATABASE
```

```sql
SELECT DATABASE(), VERSION();
SHOW TABLES;
DESCRIBE table_name;
SHOW CREATE TABLE table_name;
SHOW INDEX FROM table_name;
SELECT * FROM table_name LIMIT 10;
EXPLAIN SELECT * FROM table_name WHERE indexed_column = 'value';
```

Replace placeholders with verified names and values. Avoid `SELECT *` for sensitive or large datasets; select only necessary columns and avoid exposing private data. For writes, first inspect the matching rows with the same `WHERE` clause, confirm the target and expected row count, and use a transaction where supported:

```sql
START TRANSACTION;
UPDATE table_name SET column_name = 'new_value' WHERE id = 123;
SELECT ROW_COUNT();
-- Inspect the result, then choose exactly one:
COMMIT;
-- ROLLBACK;
```

MySQL DDL commonly causes implicit commits and cannot generally be rolled back this way. Do not run `DROP`, `TRUNCATE`, unbounded updates/deletes, or production schema changes without explicit approval, a verified backup, and a recovery plan. Do not use manual DDL as a substitute for a versioned migration.

## Spring Boot and JPA
- Inspect the active Spring profile and datasource settings; use environment variables or a secret manager for connection credentials, not committed passwords. Verify that the JDBC URL, driver, and MySQL version are compatible.
- In this project, `green.yp.backend/build.gradle` already declares `spring-boot-starter-data-jpa`, `flyway-core`, `flyway-mysql`, and `mysql-connector-j`; inspect the active build before changing dependencies.
- Follow existing `JpaRepository<Entity, Id>` interfaces and entity/ID mappings. Prefer derived queries for simple lookups and parameterized JPQL or native queries for more complex cases; never concatenate untrusted input into SQL.
- Define transaction boundaries in the service layer as appropriate. Consider pagination, stable sorting, indexes, nullability, unique constraints, lazy loading, and concurrent updates when relevant. Inspect generated SQL and MySQL `EXPLAIN` for expensive queries.
- Let Flyway own schema evolution. Check the effective `spring.jpa.hibernate.ddl-auto` setting and avoid using Hibernate schema creation or updates to manage persistent environments; use validation where compatible with the project's mappings.

## Flyway integration
- Check the configured migration locations and schema history; this project normally stores SQL migrations in `green.yp.backend/src/main/resources/db/migration` using names such as `V1.1__greenyp_initial_schema.sql`.
- Inspect all existing versions and Flyway history before choosing a unique, correctly ordered `V<version>__<description>.sql` filename. Investigate duplicate versions or malformed filenames rather than assuming the next version is safe.
- Never edit a migration already applied to a shared environment; add a forward migration. Review MySQL-specific syntax, existing data, locks, implicit commits, and compatibility with deployed application versions.
- Validate and apply migrations to an isolated MySQL instance first, including upgrade from a representative prior schema and, where practical, a fresh database. Check `flyway_schema_history` and investigate failures; do not blindly repair checksums, baseline, or clean a database.

## Verification checklist
- [ ] Correct environment and schema confirmed; sensitive credentials and data protected
- [ ] Repository behavior and entity mappings match the MySQL schema
- [ ] Migration version is unique and upgrade impact reviewed
- [ ] Targeted tests run against isolated MySQL with migrations applied, or limitations reported
- [ ] Query plans and transaction/rollback risks reviewed where relevant
