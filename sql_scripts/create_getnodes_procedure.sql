DELIMITER $$
CREATE PROCEDURE GetNodes(IN wayId BIGINT(20))
BEGIN
    SELECT vehicle_data.budapest_nodes.latitude, vehicle_data.budapest_nodes.longitude
    FROM vehicle_data.budapest_way_references
    INNER JOIN vehicle_data.budapest_nodes
    ON vehicle_data.budapest_way_references.node_id=vehicle_data.budapest_nodes.node_id
    WHERE way_id=wayId
    ORDER BY vehicle_data.budapest_way_references.ID;
END $$
DELIMITER ;
