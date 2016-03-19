CREATE table nodes (
    node_id BIGINT(20) NOT NULL,
    latitude REAL,
    longitude REAL,
    parking BOOLEAN DEFAULT 0,
    PRIMARY KEY(node_id)
);
