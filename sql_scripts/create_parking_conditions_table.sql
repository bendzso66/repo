CREATE table parking_conditions (
    ID BIGINT(20) NOT NULL AUTO_INCREMENT,
    street_id BIGINT(20) NOT NULL,
    parking_condition_key VARCHAR(20),
    parking_condition_value VARCHAR(20),
    PRIMARY KEY(ID)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
