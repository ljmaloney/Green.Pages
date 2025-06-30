DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE classified_ad_type (
                  `id`                BINARY(16)      NOT NULL,
                  `version`           VARCHAR(45)     NOT NULL,
                  `create_date`       DATETIME        NOT NULL,
                  `last_update_date`  DATETIME        NOT NULL,
                  `active`            char(1)         NOT NULL,
                  `ad_type_name`      varchar(75)     NOT NULL,
                  `monthly_price`     decimal(5,2)    NOT NULL,
                  `three_month_price` decimal(5,2),
                  `features`          JSON,
                  PRIMARY KEY (`id`) );
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;