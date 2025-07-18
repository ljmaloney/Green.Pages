DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE IF NOT EXISTS `greenyp`.`payment_method`
    (
        `id`                       BINARY(16)       NOT NULL,
        `create_date`              DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `last_update_date`         DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `version`                  INT              NOT NULL,
        `active`                   CHAR(1)          NOT NULL,
        `reference_id`             VARCHAR(50)      NOT NULL,
        `extern_cust_ref`          VARCHAR(50),
        `card_ref`                 VARCHAR(100),
        `cancel_date`              DATETIME,
        `given_name`               VARCHAR(100),
        `family_name`              VARCHAR(100),
        `company_name`             VARCHAR(150),
        `payor_address_1`          VARCHAR(100)     NOT NULL,
        `payor_address_2`          VARCHAR(100)     NULL,
        `payor_city`               VARCHAR(100)     NOT NULL,
        `payor_state`              VARCHAR(100)     NOT NULL,
        `payor_postal_code`        VARCHAR(16)      NOT NULL,
        `phone_number`             VARCHAR(20),
        `email_address`            VARCHAR(150),
        `card_details`             JSON,
        PRIMARY KEY (`id`)
    ) ENGINE = InnoDB;
    SHOW WARNINGS;
    CREATE INDEX `payment_ref_id_idx` on `payment_method` (`reference_id`);
    CREATE INDEX `payment_method_active_idx` ON `greenyp`.`payment_method` (`active` ASC) VISIBLE;
    CREATE INDEX `payment_mthd_ref_active_idx` on `payment_method` (`reference_id`, `active`);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;