DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
        CREATE INDEX `classified_active_dt_idx` ON `greenyp`.`classified` (active_date, last_active_date);
    END;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;