DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
    END;
    create table greenyp.email_contact_message
    (
        id                   binary(16)   not null primary key,
        version              varchar(45)  not null,
        create_date          datetime     not null,
        last_upd_date        datetime     not null,
        message_sent_date    datetime     null,
        source_ip_address    varchar(16)  not null,
        sms_email_type       varchar(50)  not null,
        contact_request_type varchar(50)  not null,
        producer_id          binary(16)   null,
        location_id          binary(16)   null,
        classified_id        binary(16)   null,
        product_service_ref  binary(16)   null,
        addressee_name       varchar(150) null,
        destination          varchar(150) not null,
        from_email           varchar(150) not null,
        from_phone           varchar(15)  null,
        title                varchar(255) not null,
        message              text         null,
        constraint contact_msg_classified_fk
            foreign key (classified_id) references greenyp.classified (id),
        constraint contact_msg_location_fk
            foreign key (location_id) references greenyp.producer_location (id),
        constraint contact_msg_producer_fk
            foreign key (producer_id) references greenyp.producer (id)
    );
    create index contact_date_idx on greenyp.contact_message (create_date);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;