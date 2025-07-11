DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
        CREATE INDEX `classified_cust_email_idx` ON `greenyp`.`classified_Customer` (email_address);
        CREATE INDEX `classified_cust_email_idx` ON `greenyp`.`classified_Customer` (phone_number);
    END;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;