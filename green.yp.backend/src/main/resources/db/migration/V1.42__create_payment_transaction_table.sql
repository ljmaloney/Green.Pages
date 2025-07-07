DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    CREATE TABLE payment_transaction (`id`                  binary(16)          not null,
                                      `version`             int                 not null,
                                      `create_date`         timestamp           not null,
                                      `updated_date`        timestamp           not null,
                                      `payment_ref`         varchar(255)        not null,
                                      `location_ref`        varchar(50),
                                      `order_ref`           varchar(50),
                                      `customer_ref`        varchar(50),
                                      `receipt_number`      varchar(10),
                                      `status`              varchar(50),
                                      `source_type`         varchar(50),
                                      `ip_address`          varchar(20),
                                      `note`                text,
                                      `amount`              decimal(12,2),
                                      `app_fee_amount`      decimal(12,2),
                                      `approved_amount`     decimal(12,2),
                                      `processing_fee`      decimal(12,2),
                                      `refund_amount`       decimal(12,2),
                                      `total_amount`        decimal(12,2),
                                      `currency_code`       varchar(5)          default 'USD',
                                      `first_name`           varchar(100)       not null,
                                      `last_name`            varchar(100)       not null,
                                      `address`              varchar(100)       not null,
                                      `city`                 varchar(100)       not null,
                                      `state`                varchar(2)         not null,
                                      `postal_code`          varchar(10)        not null,
                                      `phone_number`         varchar(15)        NOT NULL,
                                      `email_address`        varchar(100)       not null,
                                      `statement_description_identifier` text,
                                      `receipt_url`          varchar(255),
                                      `version_token`        varchar(255),
                                      `payment_details`      JSON,
                                      PRIMARY KEY (`id`)
                                     );
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;