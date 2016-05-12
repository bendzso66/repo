DELIMITER $$
CREATE PROCEDURE GetWays(IN mylat DOUBLE,
                         IN mylng DOUBLE,
                         IN dist INT)
BEGIN

    DECLARE df FLOAT; 
    DECLARE dl FLOAT; 
    DECLARE lng1 FLOAT; 
    DECLARE lng2 FLOAT;
    DECLARE lat1 FLOAT; 
    DECLARE lat2 FLOAT;

    SET df = dist / 1000 / 110.06;
    SET dl = df / abs(cos(radians(mylat)));
    SET lat1 = mylat - df; 
    SET lat2 = mylat + df;
    SET lng1 = mylng - dl;
    SET lng2 = mylng + dl;

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
