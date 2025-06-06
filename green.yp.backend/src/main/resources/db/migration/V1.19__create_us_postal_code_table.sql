DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE postal_code_geocode (
         postal_code VARCHAR(10),
         place_name VARCHAR(100),
         state VARCHAR(2),
         latitude DECIMAL(9,6) not null,
         longitude DECIMAL(9,6) not null,
         geo_point POINT GENERATED ALWAYS AS (ST_SRID(POINT(longitude, latitude), 4326)) STORED not null,
         PRIMARY KEY (postal_code)
    );
    CREATE INDEX us_zip_lat_long_idx ON greenyp.postal_code_geocode (latitude, longitude);
    ALTER TABLE greenyp.postal_code_geocode ADD SPATIAL INDEX postal_code_geocode_idx (geo_point);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;