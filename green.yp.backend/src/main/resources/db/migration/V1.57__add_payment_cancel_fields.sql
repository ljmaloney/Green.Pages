DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE producer ADD COLUMN cancel_type varchar(50) after cancel_date;
    ALTER TABLE producer ADD COLUMN cancel_reason varchar(255) after cancel_type;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;