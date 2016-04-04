-- count ways in a square around Budapest
SELECT COUNT(street_id)
AS num_of_streets_of_budapest
FROM vehicle_data.streets
WHERE street_id = ANY (SELECT street_id
                       FROM vehicle_data.street_references
                       WHERE node_id = ANY (SELECT node_id
                                               FROM vehicle_data.nodes
                                               WHERE latitude < 47.623717
                                               AND latitude > 47.351792
                                               AND longitude < 19.358956
                                               AND longitude > 18.936853)
					  );

-- filter nodes in a square around Budapest
CREATE TABLE vehicle_data.budapest_nodes LIKE vehicle_data.nodes;
INSERT vehicle_data.budapest_nodes SELECT * FROM vehicle_data.nodes
											WHERE latitude < 47.623717
											AND latitude > 47.351792
											AND longitude < 19.358956
											AND longitude > 18.936853;
                                                  
-- filter street_references in a square around Budapest
CREATE TABLE vehicle_data.budapest_street_references LIKE vehicle_data.street_references;
INSERT vehicle_data.budapest_street_references SELECT * FROM vehicle_data.street_references
WHERE node_id = ANY (SELECT node_id FROM vehicle_data.budapest_nodes);

-- filter streets in a square around Budapest
CREATE TABLE vehicle_data.budapest_streets LIKE vehicle_data.streets;
INSERT vehicle_data.budapest_streets SELECT * FROM vehicle_data.streets
WHERE street_id = ANY (SELECT street_id FROM vehicle_data.budapest_street_references);

-- delete streets, street references and nodes which are not related to these types of ways:
-- secondary, tertiary, unclassified, residential, service or living_street ways
DELETE FROM vehicle_data.budapest_streets
WHERE (highway <> "secondary"
       AND highway <> "tertiary"
       AND highway <> "unclassified"
       AND highway <> "residential"
       AND highway <> "service"
       AND highway <> "living_street")
      OR highway IS NULL;

DELETE FROM vehicle_data.budapest_street_references
WHERE vehicle_data.budapest_street_references.street_id NOT IN (SELECT street_id FROM vehicle_data.budapest_streets);

CREATE TABLE vehicle_data.budapest_nodes_tmp LIKE vehicle_data.budapest_nodes;
INSERT vehicle_data.budapest_nodes_tmp (SELECT * FROM vehicle_data.budapest_nodes
WHERE node_id = ANY (SELECT node_id FROM vehicle_data.budapest_street_references));
DROP TABLE budapest_nodes;
RENAME TABLE budapest_nodes_tmp TO budapest_nodes;
