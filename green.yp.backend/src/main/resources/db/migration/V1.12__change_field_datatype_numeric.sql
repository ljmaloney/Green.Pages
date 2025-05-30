DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE producer_location MODIFY COLUMN latitude double;
    alter table producer_location modify column longitude double;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;