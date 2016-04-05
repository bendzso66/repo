CREATE table ways (
    way_id BIGINT(20) NOT NULL,
    name_of_way VARCHAR(200),
    highway VARCHAR(30),
    PRIMARY KEY(way_id)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
