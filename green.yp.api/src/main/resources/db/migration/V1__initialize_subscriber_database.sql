CREATE TABLE greenyp.botanical_group
(
    name          VARCHAR(255) NOT NULL,
    create_date   datetime NULL,
    genus         VARCHAR(255) NULL,
    species       VARCHAR(255) NULL,
    sub_species   VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_botanical_group PRIMARY KEY (name)
);

CREATE TABLE greenyp.invalid_credentials_counter
(
    id                  BINARY(16)   NOT NULL,
    created_date        datetime     NOT NULL,
    user_id             VARCHAR(255) NOT NULL,
    bad_creds           VARCHAR(255) NOT NULL,
    ip_address          VARCHAR(255) NOT NULL,
    user_credentials_id BINARY(16)   NOT NULL,
    CONSTRAINT pk_invalid_credentials_counter PRIMARY KEY (id)
);

CREATE TABLE greenyp.line_of_business
(
    id                     BINARY(16)   NOT NULL,
    version                BIGINT NULL,
    create_date            datetime NULL,
    last_update_date       datetime NULL,
    line_of_business       VARCHAR(50) NOT NULL,
    `description`          VARCHAR(255) NULL,
    created_by_type        VARCHAR(255) NULL,
    create_by_reference    VARCHAR(255) NULL,
    enable_distance_radius VARCHAR(255) NULL,
    CONSTRAINT pk_line_of_business PRIMARY KEY (id)
);

CREATE TABLE greenyp.lob_service
(
    id                   BINARY(16)   NOT NULL,
    version              BIGINT NULL,
    create_date          datetime NULL,
    last_update_date     datetime NULL,
    line_of_business_id  BINARY(16)   NULL,
    created_by_reference VARCHAR(50) NOT NULL,
    created_by_type      VARCHAR(50) NOT NULL,
    service_name         VARCHAR(50) NOT NULL,
    service_description  VARCHAR(512) NULL,
    CONSTRAINT pk_lob_service PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer
(
    id                  BINARY(16)   NOT NULL,
    version             BIGINT NULL,
    create_date         datetime NULL,
    last_update_date    datetime NULL,
    name                VARCHAR(60) NOT NULL,
    subscription_type   VARCHAR(255) NULL,
    cancel_date         datetime NULL,
    last_bill_date      datetime NULL,
    last_bill_paid_date datetime NULL,
    website_url         VARCHAR(150) NULL,
    narrative           VARCHAR(512) NULL,
    CONSTRAINT pk_producer PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_audit
(
    id                    BINARY(16)   NOT NULL,
    create_date           datetime NOT NULL,
    producer_id           BINARY(16)   NULL,
    action_type           VARCHAR(255) NULL,
    object_type           VARCHAR(255) NULL,
    class_name            VARCHAR(255) NULL,
    ip_address            VARCHAR(255) NULL,
    action_ref_id         VARCHAR(255) NULL,
    action_email_address  VARCHAR(255) NULL,
    object_data           VARCHAR(255) NULL,
    object_data_encrypted VARCHAR(255) NULL,
    CONSTRAINT pk_producer_audit PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_contact
(
    id                       BINARY(16)   NOT NULL,
    version                  BIGINT NULL,
    create_date              datetime NULL,
    last_update_date         datetime NULL,
    producer_id              BINARY(16)   NOT NULL,
    producer_location_id     BINARY(16)   NULL,
    contact_type             VARCHAR(255) NOT NULL,
    display_type             VARCHAR(255) NOT NULL,
    generic_contact_name     VARCHAR(50) NULL,
    first_name               VARCHAR(50) NULL,
    last_name                VARCHAR(50) NULL,
    phone_number             VARCHAR(12) NULL,
    cell_phone_number        VARCHAR(12) NULL,
    email_confirmed          VARCHAR(255) NULL,
    auth_cancel_date         datetime NULL,
    email_confirmed_date     datetime NULL,
    email_confirmation_token VARCHAR(255) NULL,
    email_address            VARCHAR(150) NULL,
    CONSTRAINT pk_producer_contact PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_invoice
(
    id                       BINARY(16)  NOT NULL,
    version                  BIGINT NULL,
    create_date              datetime NULL,
    last_update_date         datetime NULL,
    producer_id              BINARY(16)  NOT NULL,
    subscription_id          BINARY(16)  NOT NULL,
    producer_subscription_id BINARY(16)  NOT NULL,
    paid_date                datetime NULL,
    invoice_number           VARCHAR(20) NOT NULL,
    invoice_total            DECIMAL NULL,
    CONSTRAINT pk_producer_invoice PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_invoice_lineitem
(
    id                  BINARY(16)     NOT NULL,
    create_date         datetime       NOT NULL,
    line_item           INT            NOT NULL,
    producer_id         BINARY(16)     NOT NULL,
    subscription_id     BINARY(16)     NOT NULL,
    producer_invoice_id BINARY(16)     NOT NULL,
    `description`       VARCHAR(255)   NOT NULL,
    amount              DECIMAL(10, 2) NOT NULL,
    CONSTRAINT pk_producer_invoice_lineitem PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_line_of_business
(
    producer_id         BINARY(16)   NOT NULL,
    line_of_business_id BINARY(16)   NOT NULL,
    primary_lob         VARCHAR(255) NULL,
    CONSTRAINT pk_producer_line_of_business PRIMARY KEY (producer_id, line_of_business_id)
);

CREATE TABLE greenyp.producer_location
(
    id               BINARY(16)   NOT NULL,
    version          BIGINT NULL,
    create_date      datetime NULL,
    last_update_date datetime NULL,
    producer_id      BINARY(16)   NOT NULL,
    location_name    VARCHAR(100) NOT NULL,
    location_type    VARCHAR(255) NOT NULL,
    display_type     VARCHAR(255) NOT NULL,
    active           VARCHAR(255) NOT NULL,
    address_line_1   VARCHAR(255) NOT NULL,
    address_line_2   VARCHAR(255) NULL,
    address_line_3   VARCHAR(255) NULL,
    city             VARCHAR(255) NOT NULL,
    state            VARCHAR(2)   NOT NULL,
    postal_code      VARCHAR(10)  NOT NULL,
    latitude         VARCHAR(255) NULL,
    longitude        VARCHAR(255) NULL,
    website_url      VARCHAR(150) NULL,
    CONSTRAINT pk_producer_location PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_location_hours
(
    id                   BINARY(16)   NOT NULL,
    version              BIGINT NULL,
    create_date          datetime NULL,
    last_update_date     datetime NULL,
    producer_id          BINARY(16)   NOT NULL,
    producer_location_id BINARY(16)   NOT NULL,
    day_of_week          VARCHAR(255) NOT NULL,
    open_time            VARCHAR(255) NOT NULL,
    close_time           VARCHAR(255) NOT NULL,
    CONSTRAINT pk_producer_location_hours PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_payment_method
(
    id                BINARY(16)   NOT NULL,
    version           BIGINT NULL,
    create_date       datetime NULL,
    last_update_date  datetime NULL,
    active            VARCHAR(255) NOT NULL,
    capture_method    VARCHAR(255) NOT NULL,
    cancel_date       datetime NULL,
    producer_id       BINARY(16)   NOT NULL,
    method_type       VARCHAR(255) NOT NULL,
    pan_last_four     CHAR(4)      NOT NULL,
    payment_method    BLOB NULL,
    payor_name        BLOB         NOT NULL,
    payor_address_1   BLOB         NOT NULL,
    payor_address_2   BLOB         NOT NULL,
    payor_city        BLOB         NOT NULL,
    payor_state       BLOB         NOT NULL,
    payor_postal_code BLOB         NOT NULL,
    CONSTRAINT pk_producer_payment_method PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_payment_transaction
(
    id                            BINARY(16)   NOT NULL,
    create_date                   datetime     NOT NULL,
    producer_id                   BINARY(16)   NOT NULL,
    payment_method_id             BINARY(16)   NOT NULL,
    producer_invoice_id           BINARY(16)   NOT NULL,
    payment_type                  VARCHAR(255) NOT NULL,
    amount                        DECIMAL      NOT NULL,
    status                        VARCHAR(255) NULL,
    reference_number              VARCHAR(255) NULL,
    avs_error_code                VARCHAR(1) NULL,
    avs_postal_response_code      VARCHAR(1) NULL,
    avs_street_addr_response_code VARCHAR(1) NULL,
    cvv_response_code             VARCHAR(1) NULL,
    response_code                 VARCHAR(255) NULL,
    response_text                 VARCHAR(255) NULL,
    CONSTRAINT pk_producer_payment_transaction PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_product
(
    id                   BINARY(16)   NOT NULL,
    version              BIGINT NULL,
    create_date          datetime NULL,
    last_update_date     datetime NULL,
    producer_id          BINARY(16)   NOT NULL,
    producer_location_id BINARY(16)   NOT NULL,
    product_type         VARCHAR(255) NOT NULL,
    botanical_group      VARCHAR(50) NULL,
    name                 VARCHAR(100) NULL,
    price                DECIMAL NULL,
    available_quantity   DECIMAL NULL,
    container_size       VARCHAR(255) NULL,
    discontinued         VARCHAR(255) NULL,
    discontinue_date     date NULL,
    last_order_date      date NULL,
    `description`        VARCHAR(512) NULL,
    attributes           JSON NULL,
    product_image        BLOB NULL,
    CONSTRAINT pk_producer_product PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_service
(
    id                        BINARY(16)   NOT NULL,
    version                   BIGINT NULL,
    create_date               datetime NULL,
    last_update_date          datetime NULL,
    producer_id               BINARY(16)   NOT NULL,
    producer_location_id      BINARY(16)   NOT NULL,
    min_service_price         DECIMAL NULL,
    max_service_price         DECIMAL NULL,
    price_units_type          VARCHAR(255) NULL,
    service_short_description VARCHAR(255) NOT NULL,
    service_description       LONGTEXT NULL,
    service_terms             LONGTEXT NULL,
    CONSTRAINT pk_producer_service PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_subscription
(
    id                 BINARY(16)   NOT NULL,
    version            BIGINT NULL,
    create_date        datetime NULL,
    last_update_date   datetime NULL,
    producer_id        BINARY(16)   NOT NULL,
    subscription_id    BINARY(16)   NOT NULL,
    next_invoice_date  date         NOT NULL,
    start_date         date         NOT NULL,
    end_date           date         NOT NULL,
    invoice_cycle_type VARCHAR(255) NOT NULL,
    CONSTRAINT pk_producer_subscription PRIMARY KEY (id)
);

CREATE TABLE greenyp.producer_user_credentials
(
    id                      BINARY(16)   NOT NULL,
    version                 BIGINT NULL,
    create_date             datetime NULL,
    last_update_date        datetime NULL,
    user_id                 VARCHAR(150) NOT NULL,
    email_address           VARCHAR(150) NULL,
    producer_id             BINARY(16)   NULL,
    producer_contact_id     BINARY(16)   NULL,
    extern_auth_service_ref VARCHAR(25)  NOT NULL,
    registration_ref        VARCHAR(25)  NOT NULL,
    first_name              VARCHAR(50)  NOT NULL,
    last_name               VARCHAR(50)  NOT NULL,
    password                VARCHAR(255) NULL,
    enabled                 VARCHAR(1) NULL,
    last_change_date        datetime NULL,
    admin_user              VARCHAR(255) NULL,
    reset_token             BINARY(16)   NULL,
    reset_token_timeout     datetime NULL,
    CONSTRAINT pk_producer_user_credentials PRIMARY KEY (id)
);

CREATE TABLE greenyp.subscription
(
    id                       BINARY(16)   NOT NULL,
    version                  BIGINT NULL,
    create_date              datetime NULL,
    last_update_date         datetime NULL,
    annual_bill_amount       DECIMAL      NOT NULL,
    display_name             VARCHAR(30)  NOT NULL,
    end_date                 date         NOT NULL,
    html_description         LONGTEXT NULL,
    line_of_business_id      BINARY(16)   NULL,
    monthly_autopay_amount   DECIMAL      NOT NULL,
    quarterly_autopay_amount DECIMAL      NOT NULL,
    short_description        VARCHAR(100) NOT NULL,
    sort_order               INT          NOT NULL,
    start_date               date         NOT NULL,
    subscription_type        VARCHAR(255) NOT NULL,
    CONSTRAINT pk_subscription PRIMARY KEY (id)
);

ALTER TABLE greenyp.line_of_business
    ADD CONSTRAINT uc_line_of_business_line_of_business UNIQUE (line_of_business);

ALTER TABLE greenyp.producer_invoice_lineitem
    ADD CONSTRAINT FK_PRODUCER_INVOICE_LINEITEM_ON_PRODUCER_INVOICE FOREIGN KEY (producer_invoice_id) REFERENCES greenyp.producer_invoice (id);

ALTER TABLE greenyp.producer_line_of_business
    ADD CONSTRAINT FK_PRODUCER_LINE_OF_BUSINESS_ON_PRODUCER FOREIGN KEY (producer_id) REFERENCES greenyp.producer (id);

ALTER TABLE greenyp.producer_location_hours
    ADD CONSTRAINT FK_PRODUCER_LOCATION_HOURS_ON_PRODUCER_LOCATION FOREIGN KEY (producer_location_id) REFERENCES greenyp.producer_location (id);

ALTER TABLE greenyp.producer_subscription
    ADD CONSTRAINT FK_PRODUCER_SUBSCRIPTION_ON_PRODUCER FOREIGN KEY (producer_id) REFERENCES greenyp.producer (id);