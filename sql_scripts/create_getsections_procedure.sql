DELIMITER $$
CREATE PROCEDURE GetSections(IN mylat DOUBLE, IN mylng DOUBLE)
BEGIN

    DECLARE df FLOAT; 
    DECLARE dl FLOAT; 
    DECLARE lng1 FLOAT; 
    DECLARE lng2 FLOAT;
    DECLARE lat1 FLOAT; 
    DECLARE lat2 FLOAT;

    SET df = 0.75 / 110.06;
    SET dl = df / abs(cos( radians( mylat ) ) );
    SET lat1 = mylat - df; 
    SET lat2 = mylat + df;
    SET lng1 = mylng - dl;
    SET lng2 = mylng + dl;

SELECT way_id, latitude_1, longitude_1, latitude_2, longitude_2
    FROM budapest_way_sections
    WHERE ((budapest_way_sections.latitude_1 BETWEEN lat1 AND lat2)
    AND (budapest_way_sections.longitude_1 BETWEEN lng1 AND lng2))
	OR ((budapest_way_sections.latitude_2 BETWEEN lat1 AND lat2)
    AND (budapest_way_sections.longitude_2 BETWEEN lng1 AND lng2));
END $$
DELIMITER ;
