DELIMITER $$
CREATE PROCEDURE SetFreeSpace(IN wayId INT, IN isFree TINYINT)
BEGIN

    IF isFree=1
    AND (SELECT free_spaces FROM budapest_ways WHERE way_id=wayId) < (SELECT all_spaces FROM budapest_ways WHERE way_id=wayId)
    THEN
        UPDATE budapest_ways SET free_spaces=free_spaces+1 WHERE way_id=wayId;
    ELSEIF isFree=0
    AND (SELECT free_spaces FROM budapest_ways WHERE way_id=wayId) > 0
    THEN
        UPDATE budapest_ways SET free_spaces=free_spaces-1 WHERE way_id=wayId;
    END IF;

END $$
DELIMITER ;
