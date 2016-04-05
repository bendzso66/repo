CREATE table budapest_way_sections (
    section_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    way_id BIGINT(20),
    node_id_1 BIGINT(20),
    latitude_1 REAL,
    longitude_1 REAL,
    node_id_2 BIGINT(20),
    latitude_2 REAL,
    longitude_2 REAL,
    length_of_section REAL,
    PRIMARY KEY(section_id)
);
