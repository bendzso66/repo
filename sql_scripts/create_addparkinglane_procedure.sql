DELIMITER $$
CREATE PROCEDURE AddParkingLane(IN newWayId BIGINT(20), IN newSide VARCHAR(30), IN newDirection VARCHAR(20))
BEGIN
    INSERT INTO vehicle_data.budapest_parking_lanes (way_id, side, direction, from_user)
    VALUES (newWayId, newSide, newDirection, 1);
END$$
DELIMITER ;
