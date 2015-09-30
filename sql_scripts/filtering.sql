-- count ways in a square around Budapest
SELECT COUNT(street_id)
AS num_of_streets_of_budapest
FROM test_vehicle_data.streets
WHERE street_id = ANY (SELECT street_id
                       FROM test_vehicle_data.street_references
                       WHERE section_id = ANY (SELECT section_id
                                               FROM test_vehicle_data.street_sections
                                               WHERE latitude < 47.623717
                                               AND latitude > 47.351792
                                               AND longitude < 19.358956
                                               AND longitude > 18.936853)
					  );
                      
-- filter street_sections in a square around Budapest
insert test_budapest_vehicle_data.street_sections SELECT *
												  FROM test_vehicle_data.street_sections
												  WHERE latitude < 47.623717
												  AND latitude > 47.351792
												  AND longitude < 19.358956
												  AND longitude > 18.936853;
-- filter street_references in a square around Budapest
insert test_budapest_vehicle_data.street_references select * from vehicle_data.street_references where section_id = any (select section_id from test_budapest_vehicle_data.street_sections);
-- filter streets in a square around Budapest
insert test_budapest_vehicle_data.streets select * from vehicle_data.streets where street_id = any(select street_id from test_budapest_vehicle_data.street_references);

-- fill not null tables
insert test_budapest_vehicle_data.streets_not_null select * from test_budapest_vehicle_data.streets where name_of_street is not null;
insert test_budapest_vehicle_data.street_references_not_null (select * from test_budapest_vehicle_data.street_references where street_id = any (select street_id from test_budapest_vehicle_data.streets_not_null));
insert test_budapest_vehicle_data.street_sections_not_null (select * from test_budapest_vehicle_data.street_sections where section_id = any (select section_id from test_budapest_vehicle_data.street_references_not_null));
