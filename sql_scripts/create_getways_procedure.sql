DELIMITER $$
CREATE PROCEDURE GetWays(IN mylat DOUBLE, IN mylng DOUBLE, IN dist INT)
BEGIN

    DECLARE df FLOAT; 
    DECLARE dl FLOAT; 
    DECLARE lng1 FLOAT; 
    DECLARE lng2 FLOAT;
    DECLARE lat1 FLOAT; 
    DECLARE lat2 FLOAT;

    SET df = dist / 1000 / 110.06;
    SET dl = df / abs(cos( radians( mylat ) ) );
    SET lat1 = mylat - df; 
    SET lat2 = mylat + df;
    SET lng1 = mylng - dl;
    SET lng2 = mylng + dl;

SELECT way_id, name_of_way, latitude_1, longitude_1, latitude_2,  longitude_2, all_spaces, free_spaces,
    ( ( 2 * 6371
    * asin( sqrt( sin( radians( mylat - center_latitude ) / 2 )
                * sin( radians( mylat - center_latitude ) / 2 )
                + cos( radians( mylat ) )
                * cos( radians( center_latitude ) )
                * sin( radians( mylng - center_longitude ) / 2 )
                * sin( radians( mylng - center_longitude ) / 2 ) ) ) )
    * 1000 ) AS distance
    FROM vehicle_data.budapest_ways
    WHERE budapest_ways.all_spaces > 0
    AND (budapest_ways.center_latitude BETWEEN lat1 AND lat2)
    AND (budapest_ways.center_longitude BETWEEN lng1 AND lng2)
    HAVING distance < dist
    ORDER BY distance;
END $$
DELIMITER ;
