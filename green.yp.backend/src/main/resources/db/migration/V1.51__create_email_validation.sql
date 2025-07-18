DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE IF NOT EXISTS `greenyp`.`email_validation`
    (
        `id`                        BINARY(16)          NOT NULL,
        `create_date`               DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `last_update_date`          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `version`                   INT                 NOT NULL,
        `email_validation_date`     DATETIME,
        `invalid_email`             VARCHAR(50)         default 'NOT_VALIDATED',
        `ip_address`                VARCHAR(50),
        `extern_ref`                varchar(50),
        `email_token`               varchar(50),
        `email_address`             VARCHAR(150)        NOT NULL,
        PRIMARY KEY (`id`)
    ) ENGINE = InnoDB;
    CREATE UNIQUE INDEX email_validation_email_idx on email_validation(`email_address`);
    CREATE INDEX email_validation_email_ref_idx on email_validation (`extern_ref`, `email_address`);
    SHOW WARNINGS;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;