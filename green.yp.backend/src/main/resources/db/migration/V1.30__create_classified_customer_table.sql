DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE classified_customer (
                                         `id`                   BINARY(16)      NOT NULL,
                                         `version`              VARCHAR(45)     NOT NULL,
                                         `create_date`          DATETIME        NOT NULL,
                                         `last_update_date`     DATETIME        NOT NULL,
                                         `first_name`           varchar(100)    not null,
                                         `last_name`            varchar(100)    not null,
                                         `address`              varchar(100)    not null,
                                         `city`                 varchar(100)    not null,
                                         `state`                varchar(2)      not null,
                                         `postal_code`          varchar(10)     not null,
                                         `phone_number`         varchar(15)     NOT NULL,
                                         `email_address`        varchar(100)    not null,
                                         PRIMARY KEY (`id`) );

END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;