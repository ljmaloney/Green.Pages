DROP PROCEDURE IF EXISTS `?`;
DELIMITER //
CREATE PROCEDURE `?`()
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN
END;
create table if not exists greenyp.message_meta
(
    id                   binary(16)   not null primary key,
    version              varchar(45)  not null,
    create_date          datetime     not null,
    last_update_date     datetime     not null,
    source_ip_address    varchar(16)  not null,
    sms_email_type       varchar(50)  not null,
    message_status       varchar(50)  not null,
    contact_request_type varchar(50)  not null,
    source_ref           binary(16)   not null,
    parent_source_ref    binary(16)   not null,
    requestor_ref        binary(16),
    company_name         varchar(100),
    requestor_name       varchar(150) not null,
    requestor_email      varchar(150) not null,
    requestor_phone      varchar(15),
    subject              varchar(255) not null,
    message_descr        varchar(255) not null
)ENGINE = InnoDB;
alter table message_meta add index msg_meta_date_idx (create_date);
alter table message_meta add index msg_meta_requestor_req_idx(requestor_ref);
alter table message_meta add index msg_meta_req_email_idx(requestor_email);
alter table message_meta add index parent_src_ref_idx(parent_source_ref);
alter table message_meta add index src_type_idx (source_ref, contact_request_type);
END //
DELIMITER ;
CALL `?`();
DROP PROCEDURE `?`;