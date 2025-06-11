DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
END;
ALTER TABLE producer_user_credentials MODIFY COLUMN extern_auth_service_ref varchar(100);
ALTER TABLE producer_user_credentials MODIFY COLUMN registration_ref varchar(100);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;