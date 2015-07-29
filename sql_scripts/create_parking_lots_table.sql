CREATE table parking_lots (
    ID INTEGER NOT NULL AUTO_INCREMENT,
    gps_time LONG,
    latitude REAL,
    longitude REAL,
    user_id LONG,
    parking_lot_availability VARCHAR(10),
    address VARCHAR(200),
    PRIMARY KEY(ID)
);
