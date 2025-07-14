DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE invoice_line_item (`id`            binary(16)          not null,
                          `create_date`             timestamp           not null,
                           `invoice_id`             binary(16)          not null,
                          `external_ref_1`          varchar(50)         NOT NULL,
                          `external_ref_2`          varchar(50),
                          `line_item_number`        int,
                          `quantity`                int                 default 1,
                          `description`             varchar(255)        not null,
                          `amount`                  decimal(12,2)       not null,
                          PRIMARY KEY (`id`),
                          CONSTRAINT `fk_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `greenyp`.`invoice` (`id`)
                              ON DELETE NO ACTION
                              ON UPDATE NO ACTION);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;