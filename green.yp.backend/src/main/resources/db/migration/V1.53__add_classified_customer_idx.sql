DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE UNIQUE INDEX classified_customer_email_idx on classified_customer(`email_address`,`phone_number`);
    SHOW WARNINGS;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;