DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE IF NOT EXISTS `greenyp`.`producer_subscription_process`
    (
        `id`                        BINARY(16)          NOT NULL,
        `create_date`               DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `last_update_date`          DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
        `version`                   INT                 NOT NULL,
        `producer_id`               BINARY(16)          NOT NULL,
        `process_step`              VARCHAR(50)         DEFAULT 'IN_PROCESS',
        PRIMARY KEY (`id`)
    ) ENGINE = InnoDB;
    CREATE UNIQUE INDEX producer_subscription_process_idx on producer_subscription_process(`producer_id`);
    SHOW WARNINGS;
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;