CREATE table parking_lanes (
    ID BIGINT(20) NOT NULL AUTO_INCREMENT,
    street_id BIGINT(20) NOT NULL,
    parking_lane_key VARCHAR(20),
    parking_lane_value VARCHAR(20),
    PRIMARY KEY(ID)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
