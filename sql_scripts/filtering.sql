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
insert vehicle_data.budapest_nodes SELECT *
												  FROM vehicle_data.nodes
												  WHERE latitude < 47.623717
												  AND latitude > 47.351792
												  AND longitude < 19.358956
												  AND longitude > 18.936853;
                                                  
-- filter street_references in a square around Budapest
CREATE TABLE vehicle_data.budapest_street_references LIKE vehicle_data.street_references;
insert vehicle_data.budapest_street_references select * from vehicle_data.street_references where node_id = any (select node_id from vehicle_data.budapest_nodes);

-- filter streets in a square around Budapest
CREATE TABLE vehicle_data.budapest_streets LIKE vehicle_data.streets;
insert vehicle_data.budapest_streets select * from vehicle_data.streets where street_id = any(select street_id from vehicle_data.budapest_street_references);

-- fill not null tables
CREATE TABLE vehicle_data.budapest_streets_not_null LIKE vehicle_data.streets;
insert vehicle_data.budapest_streets_not_null select * from vehicle_data.budapest_streets where name_of_street is not null;
CREATE TABLE vehicle_data.budapest_street_references_not_null LIKE vehicle_data.street_references;
insert vehicle_data.budapest_street_references_not_null (select * from vehicle_data.budapest_street_references where street_id = any (select street_id from vehicle_data.budapest_streets_not_null));
CREATE TABLE vehicle_data.budapest_nodes_not_null LIKE vehicle_data.nodes;
insert vehicle_data.budapest_nodes_not_null (select * from vehicle_data.budapest_nodes where node_id = any (select node_id from vehicle_data.budapest_street_references_not_null));
