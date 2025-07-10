DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE payment_transaction ADD COLUMN error_message varchar(255) after status;
    ALTER TABLE payment_transaction ADD COLUMN error_code int after error_message;
    alter table payment_transaction add column error_body varchar(512) after version_token;
    END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;