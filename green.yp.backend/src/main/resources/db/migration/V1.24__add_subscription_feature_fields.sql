DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    ALTER TABLE subscription_feature ADD COLUMN feature varchar(60) after end_date;
    ALTER TABLE subscription_feature ADD COLUMN sort_order INT default 0 after feature ;
    ALTER TABLE subscription_feature ADD COLUMN display char(1) default 'Y';
    ALTER TABLE subscription_feature ADD COLUMN config JSON;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;