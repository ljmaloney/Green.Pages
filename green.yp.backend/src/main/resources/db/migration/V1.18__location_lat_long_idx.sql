DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE INDEX prdcr_loc_lat_long_idx ON greenyp.producer_location (latitude, longitude);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;