# MySQL repository examples

These examples use migrations in `green.yp.backend/src/main/resources/db/migration`. Run database commands only against a verified, approved target; use an isolated MySQL instance for experiments. Do not copy real credentials into commands or source control.

## Inspect the classified category schema

`V1.29__create_classified_ad_category_table.sql` creates `classified_category` with a `BINARY(16)` primary key, required `name`, `url_name`, and `short_description` fields, and a unique index on `url_name`. `V1.35__initial_classified_category.sql` seeds categories including `produce`.

```bash
mysql --host=HOST --port=PORT --user=USER --password greenyp
```

```sql
SELECT DATABASE(), VERSION();
SHOW CREATE TABLE classified_category;
SHOW INDEX FROM classified_category;
SELECT HEX(id) AS id_hex, name, url_name, active
FROM classified_category
WHERE url_name = 'produce';
EXPLAIN SELECT id, name FROM classified_category WHERE url_name = 'produce';
```

Inspect the actual table and query plan rather than assuming a migration produced the intended schema. The original migration wraps DDL in a stored procedure with a `CONTINUE` handler for SQL exceptions, so an error may be suppressed.

## Procedure-wrapped schema changes

`V1.15__increase_contact_name_size.sql` changes the original `producer_contact.generic_contact_name` from `VARCHAR(45)` to `VARCHAR(100)` using this existing wrapper:

```sql
DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
END;
ALTER TABLE producer_contact MODIFY COLUMN generic_contact_name varchar(100);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;
```

`DELIMITER` separates the procedure body from the surrounding statements; the procedure is created, called, and dropped. The handler continues after SQL exceptions, so Flyway can record success even if the `ALTER` failed. This is a historical example, not a recommended wrapper for new migrations: let unexpected errors surface instead. Do not rerun it against an existing database; inspect the actual result after migration:

```sql
SHOW CREATE TABLE producer_contact;
SELECT COLUMN_TYPE, IS_NULLABLE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'greenyp' AND TABLE_NAME = 'producer_contact'
  AND COLUMN_NAME = 'generic_contact_name';
```

Check privileges for routine creation and execution when testing these migrations, and verify every intended change separately for wrappers with multiple statements, such as `V1.44__add_fields_for_payment_error.sql`.

## Data migrations and verification

The migration sequence includes initial inserts (`V1.2`, `V1.3`, `V1.34`, `V1.35`), targeted updates (`V1.7`, `V1.8`, `V1.40`, `V1.59`, `V1.67`), and later inserts (`V1.60`). For example, `V1.40__set_default_ad_type.sql` sets `default_package` to `Y` for a specific binary UUID; `V1.59` populates URLs after `V1.58` adds the column and before `V1.61` adds its unique index. Verify matching rows, UUID encoding, missing targets and duplicate URLs on the actual database, rather than assuming a successful update changed a row.

`V1.21__reset_subscription_data.sql` truncates `subscription_feature`, deletes subscriptions other than `DATA_IMPORT_NO_DISPLAY`, then inserts replacement subscriptions and features. `V1.25__modify_subscription_feature_data.sql` again truncates and repopulates features before updating their sort order. These are destructive historical migrations, not seed scripts to rerun: inspect foreign keys, existing and dependent data, backups, and recovery plans before any upgrade. MySQL `TRUNCATE` causes an implicit commit and cannot be rolled back like ordinary transactional DML.

`V1.70__update_subscription_annual_amount.sql` recalculates annual and quarterly amounts for subscriptions with `end_date = '9999-12-31'` except `DATA_IMPORT_NO_DISPLAY`. On an isolated representative upgrade, capture the matching rows before migration and compare afterward:

```sql
SELECT HEX(id) AS id_hex, subscription_type, end_date,
       monthly_autopay_amount, quarterly_autopay_amount, annual_bill_amount
FROM subscription
WHERE end_date = '9999-12-31'
  AND subscription_type != 'DATA_IMPORT_NO_DISPLAY';
```

Check the calculated amounts against the captured monthly values, and verify excluded and null-valued rows remain appropriate. Exercise both fresh installation and upgrade paths with representative data; a successful history entry alone does not establish that DML matched the intended rows or that seeded data has not subsequently changed.

## Query with Spring Data JPA

The existing `ClassifiedCategory` entity maps to `classified_category`; its `urlName` field maps to `url_name`. The existing repository uses the entity's UUID identifier and a derived lookup:

```java
public interface ClassifiedCategoryRepository extends JpaRepository<ClassifiedCategory, UUID> {
  Optional<ClassifiedCategory> findByUrlName(String categoryName);
}
```

For example, `repository.findByUrlName("produce")` can retrieve the seeded category; handle an empty `Optional` when no row matches. Test both outcomes against an isolated database with migrations applied. The schema stores IDs as `BINARY(16)`; verify the application's UUID mapping before writing raw IDs or converting them in SQL.

## Plan a forward migration

`V1.58__add_url_top_nav_fields.sql` adds `url_lob` to `line_of_business`; `V1.61__add_lob_url_index.sql` adds a unique index on that column. These demonstrate the sequence of a column change followed by an index, not a template to rerun on an existing database. Before adding a unique index, inspect duplicates and existing indexes on an approved target:

```sql
SHOW CREATE TABLE line_of_business;
SHOW INDEX FROM line_of_business;
SELECT url_lob, COUNT(*) AS occurrences
FROM line_of_business
WHERE url_lob IS NOT NULL
GROUP BY url_lob
HAVING COUNT(*) > 1;
```

For a new change, inspect all migration filenames and `flyway_schema_history`, choose an unused version greater than the versions in use, and add a new `V<version>__<description>.sql` file. Never edit an applied migration. Review existing data, locking and MySQL's implicit DDL commits; validate both a fresh database and an upgrade from a representative prior state in isolation.

```sql
SELECT installed_rank, version, description, script, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

The initial migration creates and uses the `greenyp` schema, and some seed scripts explicitly reference `greenyp`; an isolated test must account for that schema name and required privileges. Several existing migrations suppress SQL exceptions in stored procedures: inspect resulting tables and indexes as well as Flyway history. Also investigate `V1.6_lob_add_short_description.sql` (single underscore) and ` V1.44__add_payment_failed_fields.sql` (leading space), alongside `V1.44__add_fields_for_payment_error.sql`; Flyway's configured naming and validation determine whether these files are recognized. Do not rename applied migrations or repair history without investigating the target environment.
