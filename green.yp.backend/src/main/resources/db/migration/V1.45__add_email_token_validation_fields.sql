DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE classified_customer ADD COLUMN email_validation_token varchar(25) after postal_code;
    ALTER TABLE classified_customer ADD COLUMN email_validation_date timestamp after email_validation_token;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;