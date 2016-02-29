CREATE table streets (
    street_id BIGINT(20) NOT NULL,
    name_of_street VARCHAR(200),
    street_of_budapest BOOLEAN,
    PRIMARY KEY(street_id)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
