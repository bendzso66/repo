-- filter nodes in a square around Budapest
CREATE TABLE vehicle_data.budapest_nodes LIKE vehicle_data.nodes;
INSERT vehicle_data.budapest_nodes SELECT * FROM vehicle_data.nodes
											WHERE latitude < 47.623717
											AND latitude > 47.351792
											AND longitude < 19.358956
											AND longitude > 18.936853;
                                                  
-- filter way_references in a square around Budapest
CREATE TABLE vehicle_data.budapest_way_references LIKE vehicle_data.way_references;
INSERT vehicle_data.budapest_way_references SELECT * FROM vehicle_data.way_references
WHERE node_id = ANY (SELECT node_id FROM vehicle_data.budapest_nodes);

-- filter ways in a square around Budapest
CREATE TABLE vehicle_data.budapest_ways LIKE vehicle_data.ways;
INSERT vehicle_data.budapest_ways SELECT * FROM vehicle_data.ways
WHERE way_id = ANY (SELECT way_id FROM vehicle_data.budapest_way_references);

-- delete ways, way references and nodes which are not related to these types of ways:
-- secondary, tertiary, unclassified, residential, service or living_street ways
DELETE FROM vehicle_data.budapest_ways
WHERE (highway <> "secondary"
       AND highway <> "tertiary"
       AND highway <> "unclassified"
       AND highway <> "residential"
       AND highway <> "service"
       AND highway <> "living_street")
      OR highway IS NULL;

DELETE FROM vehicle_data.budapest_way_references
WHERE vehicle_data.budapest_way_references.way_id NOT IN (SELECT way_id FROM vehicle_data.budapest_ways);

CREATE TABLE vehicle_data.budapest_nodes_tmp LIKE vehicle_data.budapest_nodes;
INSERT vehicle_data.budapest_nodes_tmp (SELECT * FROM vehicle_data.budapest_nodes
WHERE node_id = ANY (SELECT node_id FROM vehicle_data.budapest_way_references));
DROP TABLE budapest_nodes;
RENAME TABLE budapest_nodes_tmp TO budapest_nodes;

CREATE TABLE vehicle_data.budapest_parking_conditions LIKE vehicle_data.parking_conditions;
INSERT vehicle_data.budapest_parking_conditions (SELECT * FROM vehicle_data.parking_conditions
WHERE way_id = ANY (SELECT way_id FROM vehicle_data.budapest_ways));

CREATE TABLE vehicle_data.budapest_parking_lanes LIKE vehicle_data.parking_lanes;
INSERT vehicle_data.budapest_parking_lanes (SELECT * FROM vehicle_data.parking_conditions
WHERE way_id = ANY (SELECT way_id FROM vehicle_data.budapest_ways));
