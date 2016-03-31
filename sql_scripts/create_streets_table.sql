CREATE table streets (
    street_id BIGINT(20) NOT NULL,
    name_of_street VARCHAR(200),
    highway VARCHAR(30),
    PRIMARY KEY(street_id)
)
CHARACTER SET utf8 COLLATE utf8_unicode_ci;
