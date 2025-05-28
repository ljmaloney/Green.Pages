create table  if not exists greenyp.subscription_feature(
    `id`               BINARY(16)   NOT NULL,
    `version`          INT          NOT NULL,
    `create_date`      DATETIME     NOT NULL,
    `last_update_date` DATETIME     NOT NULL,
    subscription_id    binary(16)   NOT NULL,
    start_date         date         ,
    end_date           date         default '9999-12-31',
    feature_name       varchar(100) not null,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_subscription_feature_subscription`
        FOREIGN KEY (`subscription_id`)
            REFERENCES `greenyp`.`subscription` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
)