DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE classified_image_gallery (
                                            `id`                BINARY(16)      NOT NULL,
                                            `version`           VARCHAR(45)     NOT NULL,
                                            `create_date`       DATETIME        NOT NULL,
                                            `last_update_date`  DATETIME        NOT NULL,
                                            `classified_id`     binary(16)      not null,
                                            `image_file_name`   varchar(80)     not null,
                                            `image_url_link`    varchar(256)    not null,
                                            `description`       TEXT,
                                            PRIMARY KEY (`id`),
                                            CONSTRAINT `fk_image_gallery_classified`
                                                FOREIGN KEY (`classified_id`)
                                                    REFERENCES `greenyp`.`classified` (`id`)
                                                    ON DELETE NO ACTION
                                                    ON UPDATE NO ACTION
    );
    CREATE UNIQUE INDEX classified_image_gallery_idx1
        ON classified_image_gallery (classified_id, image_file_name);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;