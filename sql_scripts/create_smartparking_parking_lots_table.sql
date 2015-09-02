CREATE table smartparking_parking_lots (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    parking_lot_availability VARCHAR(10),
    address VARCHAR(255),
    user_id BIGINT(20),
    latitude REAL,
    longitude REAL,
    time_of_submission BIGINT(20),
    PRIMARY KEY(id)
);
