package hu.bme.hit.smartparking.parsemap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import hu.bme.hit.smartparking.jdbc.*;

public class ParseOsmFile {

    private static final String OSM_FILE_PATH = "d:\\Programs/repo/res/hungary_map/hungary-latest.osm";

    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://127.0.0.1/vehicle_data?useUnicode=yes&characterEncoding=UTF-8";
    private static final String USER = "user";
    private static final String userName = "smartparking";
    private static final String PASSWORD = "password";
    private static final String pass = "spict2015";

    private static final String NODE = "node";
    private static final String TAG = "tag";
    private static final String WAY = "way";
    private static final String ND = "nd";
    private static final String PARKING = "parking";
    private static final String V = "v";
    private static final String NAME = "name";
    private static final String K = "k";
    private static final String RELATION = "relation";
    private static final String HIGHWAY = "highway";
    private static final String PARKING_LANE = "parking:lane";
    private static final String PARKING_CONDITION = "parking:condition";

    private static final String ID = "id";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String REF = "ref";

    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String UPDATE = "UPDATE ";
    private static final String VALUES = "VALUES (";
    private static final String SET = "SET ";
    private static final String WHERE = "WHERE ";
    private static final String QUOTATION_MARK = "'";
    private static final String QUOTATION_MARKS_WITH_COMMA = "','";
    private static final String CLOSING_BRACKET = "');";
    private static final String SEMICOLON = ";";

    private static final String NODES_TABLE = "vehicle_data.nodes ";
    private static final String NODES_TABLE_HEADERS = "(node_id, latitude, longitude) ";
    private static final String PARKING_EQUALS_ONE = "parking=1 ";
    private static final String NODE_ID_EQUALS = "node_id= ";

    private static final String WAYS_TABLE = "vehicle_data.ways ";
    private static final String WAYS_TABLE_HEADER = "(way_id) ";
    private static final String NAME_OF_WAY_EQUALS = "name_of_way=";
    private static final String WAY_ID_EQUALS = "way_id=";
    private static final String HIGHWAY_EQUALS = "highway=";

    private static final String WAY_REFERENCES_TABLE = "vehicle_data.way_references ";
    private static final String WAY_REFERENCES_TABLE_HEADERS = "(way_id, node_id) ";

    private static final String PARKING_LANES_TABLE = "vehicle_data.parking_lanes ";
    private static final String PARKING_LANES_TABLE_HEADERS = "(way_id, side, direction, from_user) ";
    private static final int FROM_USER = 0;

    private static final String PARKING_CONDITIONS_TABLE = "vehicle_data.parking_conditions ";
    private static final String PARKING_CONDITIONS_TABLE_HEADERS = "(way_id, side, parking_condition, from_user) ";

    private static final String SPACE = " ";

    private static final String NODES_NEW_RECORD_ERROR = "SQL error: cannot create new record in table nodes.";
    private static final String NODES_UPDATE_ERROR = "SQL error: cannot update table nodes.";
    private static final String WAYS_NEW_RECORD_ERROR = "SQL error: cannot create new record in table ways.";
    private static final String WAYS_UPDATE_ERROR = "SQL error: cannot update table ways.";
    private static final String WAY_REFERENCES_NEW_RECORD_ERROR = "SQL error: cannot create new record in table way_references.";
    private static final String CONNECTION_ERROR = "SQL error: cannot create the connection.";
    private static final String SAX_ERROR = "SAX Parser error: cannot create the parser or the factory.";
    private static final String PARKING_LANE_NEW_RECORD_ERROR = "SQL error: cannot update table parking_lanes.";
    private static final String PARKING_CONDITION_NEW_RECORD_ERROR = "SQL error: cannot update table parking_conditions.";

    private static final String PARSING_IS_DONE = "Parsing is done!";
    private static final String SQL_CONNECTIONS_ARE_CLOSED = "SQL connections are closed!";
    private static final String REFERENCES_ARE_REACHED = "References are reached.";

    public static void main(String argv[]) throws ClassNotFoundException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put(USER, userName);
        p.put(PASSWORD, pass);

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            try {
                final Connection c = DriverManager.getConnection(CONNECTION, p);
                final Statement stmt = c.createStatement();

                DefaultHandler handler = new DefaultHandler() {

                    private boolean getNameAttr = false;
                    private boolean getParkingAttr = false;
                    private String nodeId = null;
                    private String wayId = null;
                    private Integer counter = 0;

                    @Override
                    public void startElement(String uri, String localName,
                            String qName, Attributes attributes)
                            throws SAXException {

                        counter++;
                        System.out.println(counter);

                        if (qName.equalsIgnoreCase(NODE)) {

                            getNameAttr = false;
                            getParkingAttr = true;

                            nodeId = attributes.getValue(ID);
                            String nodeLat = attributes.getValue(LAT);
                            String nodeLon = attributes.getValue(LON);

                            String sqlStatement = INSERT_INTO
                                    + NODES_TABLE
                                    + NODES_TABLE_HEADERS
                                    + VALUES
                                    + QUOTATION_MARK
                                    + nodeId
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + nodeLat
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + nodeLon
                                    + CLOSING_BRACKET;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, NODES_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(TAG)
                                && attributes.getValue(V).equals(PARKING)
                                && getParkingAttr) {

                            String sqlStatement = UPDATE
                                    + NODES_TABLE
                                    + SET
                                    + PARKING_EQUALS_ONE
                                    + WHERE
                                    + NODE_ID_EQUALS
                                    + nodeId
                                    + SEMICOLON;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, NODES_UPDATE_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(WAY)) {

                            getNameAttr = true;
                            getParkingAttr = false;

                            wayId = attributes.getValue(ID);

                            String sqlStatement = INSERT_INTO
                                    + WAYS_TABLE
                                    + WAYS_TABLE_HEADER
                                    + VALUES
                                    + QUOTATION_MARK
                                    + wayId
                                    + CLOSING_BRACKET;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, WAYS_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(ND)) {

                            String nodeRef = attributes.getValue(REF);

                            String sqlStatement = INSERT_INTO
                                    + WAY_REFERENCES_TABLE
                                    + WAY_REFERENCES_TABLE_HEADERS
                                    + VALUES
                                    + QUOTATION_MARK
                                    + wayId
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + nodeRef
                                    + CLOSING_BRACKET;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement,
                                        WAY_REFERENCES_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(TAG)
                                && attributes.getValue(K).equals(NAME)
                                && getNameAttr) {

                            String nameOfWay = attributes.getValue(V)
                                    .replaceAll("'", "\\\\'");

                            String sqlStatement = UPDATE
                                    + WAYS_TABLE
                                    + SET
                                    + NAME_OF_WAY_EQUALS
                                    + QUOTATION_MARK
                                    + nameOfWay
                                    + QUOTATION_MARK
                                    + SPACE
                                    + WHERE
                                    + WAY_ID_EQUALS
                                    + wayId
                                    + SEMICOLON;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, WAYS_UPDATE_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(TAG)
                                && attributes.getValue(K).equals(HIGHWAY)
                                && getNameAttr) {

                            String highWayType = attributes.getValue(V);

                            String sqlStatement = UPDATE
                                    + WAYS_TABLE
                                    + SET
                                    + HIGHWAY_EQUALS
                                    + QUOTATION_MARK
                                    + highWayType
                                    + QUOTATION_MARK
                                    + SPACE
                                    + WHERE
                                    + WAY_ID_EQUALS
                                    + wayId
                                    + SEMICOLON;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, WAYS_UPDATE_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(TAG)
                                && attributes.getValue(K).startsWith(
                                        PARKING_LANE)
                                && getNameAttr) {

                            String side = attributes.getValue(K);
                            String strippedSide;
                            if (side.length() < 13) {
                                strippedSide = "";
                            } else {
                                strippedSide = side.substring(13);
                            }
                            String direction = attributes.getValue(V);

                            String sqlStatement = INSERT_INTO
                                    + PARKING_LANES_TABLE
                                    + PARKING_LANES_TABLE_HEADERS
                                    + VALUES
                                    + QUOTATION_MARK
                                    + wayId
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + strippedSide
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + direction
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + FROM_USER
                                    + CLOSING_BRACKET;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement,
                                        PARKING_LANE_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(TAG)
                                && attributes.getValue(K).startsWith(
                                        PARKING_CONDITION)
                                && getNameAttr) {

                            String side = attributes.getValue(K);
                            String strippedSide;
                            if (side.length() < 18) {
                                strippedSide = "";
                            } else {
                                strippedSide = side.substring(18);
                            }
                            String condition = attributes.getValue(V);

                            String sqlStatement = INSERT_INTO
                                    + PARKING_CONDITIONS_TABLE
                                    + PARKING_CONDITIONS_TABLE_HEADERS
                                    + VALUES
                                    + QUOTATION_MARK
                                    + wayId
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + strippedSide
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + condition
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + FROM_USER
                                    + CLOSING_BRACKET;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement,
                                        PARKING_CONDITION_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equals(RELATION)) {
                            getNameAttr = false;
                            getParkingAttr = false;
                            System.out.println(PARSING_IS_DONE);
                            System.out.println(REFERENCES_ARE_REACHED);
                            CommonJdbcMethods.closeConnections(c, stmt);
                            System.out.println(SQL_CONNECTIONS_ARE_CLOSED);
                            System.exit(0);
                        }

                    }
                };

                saxParser.parse(OSM_FILE_PATH, handler);
                System.out.println(PARSING_IS_DONE);
                CommonJdbcMethods.closeConnections(c, stmt);
                System.out.println(SQL_CONNECTIONS_ARE_CLOSED);
            } catch (SQLException e) {
                System.out.println(CONNECTION_ERROR);
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(SAX_ERROR);
            e.printStackTrace();
        }

    }
}
