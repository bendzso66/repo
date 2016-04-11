CREATE table parking_lanes (
    ID BIGINT(20) NOT NULL AUTO_INCREMENT,
    way_id BIGINT(20) NOT NULL,
    side VARCHAR(30),
    direction VARCHAR(20),
    from_user TINYINT(1),
    PRIMARY KEY(ID)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
