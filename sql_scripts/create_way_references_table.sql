CREATE table way_references (
    ID BIGINT(20) NOT NULL AUTO_INCREMENT,
    way_id BIGINT(20),
    node_id BIGINT(20),
    PRIMARY KEY(ID)
);
