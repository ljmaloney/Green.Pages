DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE search_master (`id`                      BINARY(16)     NOT NULL,
                                `version`                 VARCHAR(45)    NOT NULL,
                                `create_date`             DATETIME       NOT NULL,
                                `last_update_date`        DATETIME       NOT NULL,
                                `extern_id`               BINARY(16),
                                `producer_id`             binary(16),
                                `location_id`             binary(16),
                                `category_ref`            binary(16),
                                `active`                  char(1),
                                `last_active_date`        date,
                                `record_type`             varchar(50),
                                `keywords`                TEXT,
                                `title`                    varchar(256),
                                `business_name`            VARCHAR(256),
                                `business_url`             varchar(256),
                                `business_icon_url`        varchar(256),
                                `image_url`                varchar(256),
                                `address_line_1`           VARCHAR(100),
                                `address_line_2`           VARCHAR(100),
                                `city`                     varchar(100)    not null,
                                `state`                    char(2)         not null,
                                `postal_code`              varchar(12)     not null,
                                `email_address`            varchar(150),
                                `phone_number`             varchar(15)     not null,
                                `min_price`                decimal(10,2),
                                `max_price`                decimal(10,2),
                                `price_units_type`         VARCHAR(50),
                                `longitude`                DOUBLE          NOT NULL,
                                `latitude`                 double          not null,
                                `location_geo_point`       POINT GENERATED ALWAYS AS (ST_SRID(POINT(longitude, latitude), 4326)) STORED,
                                `description`              TEXT,
                                PRIMARY KEY (`id`));
    ALTER TABLE search_master ADD SPATIAL INDEX search_master_spatial_idx (location_geo_point);
    alter table search_master add index search_master_extern_id_idx(extern_id, producer_id, location_id, category_ref);
    ALTER TABLE search_master ADD INDEX search_master_category_ref_idx(category_ref);
    ALTER TABLE search_master ADD FULLTEXT search_master_keyword(keywords);
    ALTER TABLE search_master ADD FULLTEXT search_master_text_idx(keywords, title, description);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;