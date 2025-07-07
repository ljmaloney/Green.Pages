DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE contact_message (`id`                      BINARY(16)     NOT NULL,
                                  `version`                 VARCHAR(45)    NOT NULL,
                                  `create_date`             DATETIME       NOT NULL,
                                  `last_upd_date`           DATETIME       NOT NULL,
                                  `message_sent_date`       DATETIME,
                                  `source_ip_address`       VARCHAR(16)    NOT NULL,
                                  `sms_email_type`          VARCHAR(50)    NOT NULL,
                                  `contact_request_type`    VARCHAR(50)    NOT NULL,
                                  `producer_id`             BINARY(16),
                                  `location_id`             binary(16),
                                  `classified_id`           binary(16),
                                  `product_service_ref`     binary(16),
                                  `addressee_name`          VARCHAR(150),
                                  `destination`             VARCHAR(150)   NOT NULL,
                                  `from_email`              VARCHAR(150)   NOT NULL,
                                  `from_phone`              VARCHAR(15),
                                  `title`                   VARCHAR(255)   NOT NULL,
                                  `message`                 TEXT,
                          PRIMARY KEY (`id`),
                          CONSTRAINT contact_msg_producer_fk FOREIGN KEY (producer_id) REFERENCES `greenyp`.`producer` (`id`),
                          CONSTRAINT contact_msg_location_fk FOREIGN KEY (location_id) REFERENCES `greenyp`.`producer_location` (`id`),
                          CONSTRAINT contact_msg_classified_fk FOREIGN KEY (classified_id) REFERENCES `greenyp`.`classified` (`id`));
    alter table contact_message add index contact_date_idx(create_date);

END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;