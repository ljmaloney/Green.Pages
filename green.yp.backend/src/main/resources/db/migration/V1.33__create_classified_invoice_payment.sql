DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE classified_invoice_payment (`id`                       BINARY(16)      NOT NULL,
                             `version`                  VARCHAR(45)     NOT NULL,
                             `create_date`              DATETIME        NOT NULL,
                             `classified_id`            binary(16)      not null,
                             `classified_ad_type`       binary(16)      not null,
                             `classified_customer_id`   binary(16)      not null,
                             `invoice_number`           varchar(20)     not null,
                             `payment_customer_ref`     varchar(50)     not null,
                             `payment_ref`              varchar(50)     not null,
                             `payment_amount`           DECIMAL(12,2)   NOT NULL,
                             PRIMARY KEY (`id`),
                             CONSTRAINT `fk_invoice_classified_ad_typ` FOREIGN KEY (`classified_ad_type`)
                                 REFERENCES `greenyp`.`classified_ad_type` (`id`)
                                 ON DELETE NO ACTION
                                 ON UPDATE NO ACTION,
                             CONSTRAINT `fk_invoice_classified` FOREIGN KEY (`classified_id`)
                                 REFERENCES `greenyp`.`classified_ad_type` (`id`)
                                 ON DELETE NO ACTION
                                 ON UPDATE NO ACTION,
                             CONSTRAINT `fk_invoice_classified_customer` FOREIGN KEY (`classified_customer_id`)
                                 REFERENCES `greenyp`.`classified_customer` (`id`)
                                 ON DELETE NO ACTION
                                 ON UPDATE NO ACTION);
    alter table classified_invoice_payment add index classifed_inv_pmt_date_idx(create_date);

END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;