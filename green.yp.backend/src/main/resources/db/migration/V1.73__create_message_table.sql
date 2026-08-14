DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    create table if not exists greenyp.message
    (
        id                   binary(16)   not null primary key,
        version              varchar(45)  not null,
        create_date          datetime     not null,
        last_update_date     datetime     not null,
        message_sent_date    datetime     null,
        read_date            datetime     null,
        message_meta_id      binary(16)   not null,
        source_ip_address    varchar(16)  not null,
        message_status       varchar(50)  not null,
        sms_email_type       varchar(50)  not null,
        addressee_name       varchar(150),
        destination          varchar(255),
        addressee_ref        binary(16),
        addressee_email      varchar(150),
        from_ref             binary(16),
        from_email           varchar(150),
        from_phone           varchar(15),
        message              text         not null,
        constraint message_meta_fk
            foreign key (message_meta_id) references greenyp.message_meta (id)
    )ENGINE = InnoDB;
    alter table message add index message_date_idx(create_date);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;