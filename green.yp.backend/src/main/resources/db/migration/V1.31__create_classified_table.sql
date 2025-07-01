DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE classified (`id`                       BINARY(16)      NOT NULL,
                             `version`                  VARCHAR(45)     NOT NULL,
                             `create_date`              DATETIME        NOT NULL,
                             `last_update_date`         DATETIME        NOT NULL,
                             `last_active_date`         DATE            NOT NULL,
                             `classified_category_id`   binary(16)      not null,
                             `classified_ad_type`       binary(16)      not null,
                             `classified_customer_id`   binary(16)      not null,
                             `renewal_count`            int             default 0,
                             `price`                    decimal(12,2)   not null,
                             `per_unit_type`            varchar(50)     not null,
                             `title`                    varchar(200)    not null,
                             `description`              text            not null,
                             `city`                     varchar(100)    not null,
                             `postal_code`              varchar(12)     not null,
                             `email_address`            varchar(150)    not null,
                             `phone_number`             varchar(15)     not null,
                             `longitude`                DOUBLE          NOT NULL,
                             `latitude`                 double          not null,
                             `location_geo_point`       POINT GENERATED ALWAYS AS (ST_SRID(POINT(longitude, latitude), 4326)) STORED,
                             PRIMARY KEY (`id`),
                             CONSTRAINT `fk_classified_ad_typ` FOREIGN KEY (`classified_ad_type`)
                                     REFERENCES `greenyp`.`classified_ad_type` (`id`)
                                     ON DELETE NO ACTION
                                     ON UPDATE NO ACTION,
                             CONSTRAINT `fk_classified_category` FOREIGN KEY (`classified_category_id`)
                                 REFERENCES `greenyp`.`classified_category` (`id`)
                                 ON DELETE NO ACTION
                                 ON UPDATE NO ACTION,
                             CONSTRAINT `fk_classified_customer` FOREIGN KEY (`classified_customer_id`)
                                 REFERENCES `greenyp`.`classified_customer` (`id`)
                                 ON DELETE NO ACTION
                                 ON UPDATE NO ACTION);

    ALTER TABLE classified ADD SPATIAL INDEX classified_spatial_idx (location_geo_point);
    alter table classified add fulltext (title);

END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;