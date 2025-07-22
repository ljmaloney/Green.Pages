DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE payment_transaction ADD COLUMN error_detail varchar(256) after error_code;
    ALTER TABLE payment_transaction ADD COLUMN error_status_code varchar(100) after error_code;
    SHOW WARNINGS;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;