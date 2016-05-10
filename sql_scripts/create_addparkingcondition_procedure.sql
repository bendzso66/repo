DELIMITER $$
CREATE PROCEDURE AddParkingCondition(IN newWayId BIGINT(20), IN newSide VARCHAR(30), IN newParkingCondition VARCHAR(20))
BEGIN
    INSERT INTO vehicle_data.budapest_parking_conditions (way_id, side, parking_condition, from_user)
    VALUES (newWayId, newSide, newParkingCondition, 1);
END$$
DELIMITER ;
