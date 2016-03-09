-- count ways in a square around Budapest
SELECT COUNT(street_id)
AS num_of_streets_of_budapest
FROM vehicle_data.streets
WHERE street_id = ANY (SELECT street_id
                       FROM vehicle_data.street_references
                       WHERE section_id = ANY (SELECT section_id
                                               FROM vehicle_data.street_sections
                                               WHERE latitude < 47.623717
                                               AND latitude > 47.351792
                                               AND longitude < 19.358956
                                               AND longitude > 18.936853)
					  );

-- filter street_sections in a square around Budapest
insert vehicle_data.budapest_street_sections SELECT *
												  FROM vehicle_data.street_sections
												  WHERE latitude < 47.623717
												  AND latitude > 47.351792
												  AND longitude < 19.358956
												  AND longitude > 18.936853;
-- filter street_references in a square around Budapest
insert vehicle_data.budapest_street_references select * from vehicle_data.street_references where section_id = any (select section_id from vehicle_data.budapest_street_sections);
-- filter streets in a square around Budapest
insert vehicle_data.budapest_streets select * from vehicle_data.streets where street_id = any(select street_id from vehicle_data.budapest_street_references);

-- fill not null tables
insert vehicle_data.budapest_streets_not_null select * from vehicle_data.budapest_streets where name_of_street is not null;
insert vehicle_data.budapest_street_references_not_null (select * from vehicle_data.budapest_street_references where street_id = any (select street_id from vehicle_data.budapest_streets_not_null));
insert vehicle_data.budapest_street_sections_not_null (select * from vehicle_data.budapest_street_sections where section_id = any (select section_id from vehicle_data.budapest_street_references_not_null));
