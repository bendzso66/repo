CREATE table smartparking_parking_lots (
    id INTEGER NOT NULL AUTO_INCREMENT,
    parking_lot_availability VARCHAR(10),
    address VARCHAR(255),
    user_id LONG,
    latitude REAL,
    longitude REAL,
    time_of_submission LONG,
    PRIMARY KEY(id)
);
