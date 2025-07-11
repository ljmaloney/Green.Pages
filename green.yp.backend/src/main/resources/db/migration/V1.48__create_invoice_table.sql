DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE producerInvoice (`id`                      binary(16)          not null,
                          `version`                 int                 not null,
                          `create_date`             timestamp           not null,
                          `last_update_date`        timestamp           not null,
                          `payment_transaction_id`  binary(16),
                          `external_ref`            varchar(50)         NOT NULL,
                          `invoice_type`            varchar(50)         NOT NULL,
                          `paid_date`               date,
                          `description`             varchar(100)        not null,
                          `invoice_total`           decimal(12,2)       not null,
                          `payment_receipt_number`  varchar(20),
                          `invoice_number`          varchar(20),
                          `payment_receipt_url`     varchar(255),
                          PRIMARY KEY (`id`),
                          CONSTRAINT `fk_payment_transaction` FOREIGN KEY (`payment_transaction_id`)
                              REFERENCES `greenyp`.`payment_transaction` (`id`)
                              ON DELETE NO ACTION
                              ON UPDATE NO ACTION);
    CREATE INDEX `invoice_paid_date_idx` ON `greenyp`.`producerInvoice` (paid_date);
    CREATE INDEX `invoice_extern_ref_idx` ON `greenyp`.`producerInvoice` (external_ref);
    CREATE INDEX `invoice_type_idx` ON `greenyp`.`producerInvoice` (invoice_type);
    CREATE INDEX `invoice_paid_date_type_idx` ON `greenyp`.`producerInvoice` (paid_date, invoice_type);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;