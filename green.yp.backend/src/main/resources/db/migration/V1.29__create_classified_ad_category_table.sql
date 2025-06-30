DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE classified_category (
                                        `id`                BINARY(16)      NOT NULL,
                                        `version`           VARCHAR(45)     NOT NULL,
                                        `create_date`       DATETIME        NOT NULL,
                                        `last_update_date`  DATETIME        NOT NULL,
                                        `active`            char(1)         NOT NULL,
                                        `name`              varchar(75)     NOT NULL,
                                        `url_name`          varchar(75)     not null,
                                        `short_description` varchar(100)    not null,
                                        `description`       text,
                                        PRIMARY KEY (`id`) );
    CREATE UNIQUE INDEX classified_category_idx1 ON classified_category (url_name);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;