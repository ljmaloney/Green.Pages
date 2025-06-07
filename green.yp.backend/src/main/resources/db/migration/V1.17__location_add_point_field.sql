DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
END;
    ALTER TABLE producer_location
            MODIFY COLUMN longitude DOUBLE NOT NULL;
    ALTER table producer_location            modify column latitude double not null;

    ALTER TABLE `producer_location`
        ADD location_geo_point POINT GENERATED ALWAYS AS (ST_SRID(POINT(longitude, latitude), 4326)) STORED NOT NULL;

    ALTER TABLE greenyp.producer_location ADD SPATIAL INDEX prdcr_loc_spatial_idx (location_geo_point);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;