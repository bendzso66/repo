CREATE table ways (
    way_id BIGINT(20) NOT NULL,
    name_of_way VARCHAR(200),
    highway VARCHAR(30),
    center_latitude REAL,
    center_longitude REAL,
    latitude_1 REAL,
    longitude_1 REAL,
    latitude_2 REAL,
    longitude_2 REAL,
    length_of_way REAL,
    all_spaces SMALLINT,
    free_spaces SMALLINT,
    PRIMARY KEY(way_id)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
