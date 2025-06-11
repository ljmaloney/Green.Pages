DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
END;
    ALTER TABLE producer_contact MODIFY COLUMN phone_number varchar(20) not null;
    ALTER TABLE producer_contact DROP COLUMN cell_phone_number;
    ALTER TABLE producer_contact ADD COLUMN cell_phone_number varchar(20);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;