CREATE table parking_conditions (
    ID BIGINT(20) NOT NULL AUTO_INCREMENT,
    way_id BIGINT(20) NOT NULL,
    side VARCHAR(30),
    parking_condition VARCHAR(20),
    PRIMARY KEY(ID)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
