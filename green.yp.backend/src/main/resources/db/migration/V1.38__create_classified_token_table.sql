DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE classified_token (`id`                     BINARY(16)     NOT NULL,
                                  `version`                 VARCHAR(45)    NOT NULL,
                                  `create_date`             DATETIME       NOT NULL,
                                  `last_upd_date`           DATETIME       NOT NULL,
                                  `classified_id`           BINARY(16)      NOT NULL,
                                  `token_expiry_date`       DATETIME        NOT NULL,
                                  `token_value`             VARCHAR(255)    NOT NULL,
                                  `token_used`              CHAR(1)         DEFAULT 'N',
                                  `destination_type`        VARCHAR(15)     NOT NULL,
                                  `destination`             VARCHAR(150)    NOT NULL,
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT classifed_token_classified_fk FOREIGN KEY (classified_id) REFERENCES `greenyp`.`classified` (`id`));
    alter table classified_token add unique index classifed_token_id_idx(classified_id, token_value);
    alter table classified_token add index contact_date_idx(create_date);

END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;