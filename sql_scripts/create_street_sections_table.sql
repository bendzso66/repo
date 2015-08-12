CREATE table street_sections (
    section_id BIGINT(20) NOT NULL,
    latitude REAL,
    longitude REAL,
    parking BOOLEAN DEFAULT 0,
    PRIMARY KEY(section_id)
);
