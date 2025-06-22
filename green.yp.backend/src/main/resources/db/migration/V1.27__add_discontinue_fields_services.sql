DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE producer_service ADD COLUMN discontinued varchar(60) after producer_location_id;
    ALTER TABLE producer_service ADD COLUMN discontinue_date date after producer_location_id;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;