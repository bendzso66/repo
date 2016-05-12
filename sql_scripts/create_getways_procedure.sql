DELIMITER $$
CREATE PROCEDURE GetWays(IN mylat DOUBLE,
                         IN mylng DOUBLE,
                         IN dist INT)
BEGIN

    DECLARE dlat FLOAT; 
    DECLARE dlng FLOAT;
    DECLARE lng1 FLOAT;
    DECLARE lng2 FLOAT;
    DECLARE lat1 FLOAT; 
    DECLARE lat2 FLOAT;

    SET dlat = dist / 111319;
    SET dlng = dist / 75218;
    SET lat1 = mylat - dlat;
    SET lat2 = mylat + dlat;
    SET lng1 = mylng - dlng;
    SET lng2 = mylng + dlng;

    SELECT way_id,
           name_of_way,
           center_latitude,
           center_longitude,
           latitude_1,
           longitude_1,
           latitude_2,
           longitude_2,
           all_spaces,
           free_spaces,
           ((2 * 6371
           * asin(sqrt(sin(radians(mylat - latitude_1) / 2)
                     * sin(radians(mylat - latitude_1) / 2)
                     + cos(radians(mylat))
                     * cos(radians(latitude_1))
                     * sin(radians(mylng - longitude_1) / 2)
                     * sin(radians(mylng - longitude_1) / 2)
                      )
                 )
            ) * 1000) AS distance
        FROM vehicle_data.budapest_ways
        WHERE budapest_ways.all_spaces > 0
        AND budapest_ways.length_of_way > 5
        AND (budapest_ways.latitude_1 BETWEEN lat1 AND lat2)
        AND (budapest_ways.longitude_1 BETWEEN lng1 AND lng2)
        HAVING distance < dist
    UNION
    SELECT way_id,
           name_of_way,
           center_latitude,
           center_longitude,
           latitude_1,
           longitude_1,
           latitude_2,
           longitude_2,
           all_spaces,
           free_spaces,
           ((2 * 6371
           * asin(sqrt(sin(radians(mylat - latitude_2) / 2)
                     * sin(radians(mylat - latitude_2) / 2)
                     + cos(radians(mylat))
                     * cos(radians(latitude_2))
                     * sin(radians(mylng - longitude_2) / 2)
                     * sin(radians(mylng - longitude_2) / 2)
                      )
                 )
            ) * 1000) AS distance
        FROM vehicle_data.budapest_ways
        WHERE budapest_ways.all_spaces > 0
        AND budapest_ways.length_of_way > 5
        AND (budapest_ways.latitude_2 BETWEEN lat1 AND lat2)
        AND (budapest_ways.longitude_2 BETWEEN lng1 AND lng2)
        HAVING distance < dist
    ORDER BY distance;
END $$
DELIMITER ;
