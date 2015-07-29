CREATE table streets (
    street_id INTEGER NOT NULL,
    changeset INTEGER,
    name_of_street VARCHAR(200),
    street_of_budapest BOOLEAN DEFAULT 0,
    PRIMARY KEY(street_id)
);
