CREATE table street_sections (
    section_id INTEGER NOT NULL,
    latitude REAL,
    longitude REAL,
    changeset INTEGER,
    parking BOOLEAN DEFAULT 0,
    PRIMARY KEY(section_id)
);
