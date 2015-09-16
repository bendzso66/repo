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

public class ReadXMLFile {

    private static final String OSM_FILE_PATH = "d:\\Programs/repo/parse_hungary_map/hungary_map/hungary-latest.osm";

    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://127.0.0.1/smart_parking";
    private static final String USER = "user";
    private static final String userName = "root";
    private static final String PASSWORD = "password";
    private static final String pass = "";

    private static final String NODE = "node";
    private static final String TAG = "tag";
    private static final String WAY = "way";
    private static final String ND = "nd";
    private static final String PARKING = "parking";
    private static final String V = "v";
    private static final String NAME = "name";
    private static final String K = "k";

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

    private static final String STREET_SECTIONS_TABLE = "vehicle_data.street_sections ";
    private static final String STREET_SECTIONS_TABLE_HEADERS = "(section_id, latitude, longitude) ";
    private static final String PARKING_EQUALS_ONE = "parking=1 ";
    private static final String SECTION_ID_EQUALS = "section_id= ";

    private static final String STREETS_TABLE = "vehicle_data.streets ";
    private static final String STREETS_TABLE_HEADER = "(street_id) ";
    private static final String NAME_OF_STREET_EQUALS = "name_of_street=";
    private static final String STREET_ID_EQUALS = "street_id=";

    private static final String STREET_REFERENCES_TABLE = "vehicle_data.street_references ";
    private static final String STREET_REFERENCES_TABLE_HEADERS = "(street_id, section_id) ";

    private static final String SPACE = " ";

    private static final String STREET_SECTIONS_NEW_RECORD_ERROR = "SQL error: cannot create new record in table street_sections.";
    private static final String STREET_SECTIONS_UPDATE_ERROR = "SQL error: cannot update table street_sections.";
    private static final String STREETS_NEW_RECORD_ERROR = "SQL error: cannot create new record in table streets.";
    private static final String STREETS_UPDATE_ERROR = "SQL error: cannot update table streets.";
    private static final String STREET_REFERENCES_NEW_RECORD_ERROR = "SQL error: cannot create new record in table street_references.";
    private static final String CONNECTION_ERROR = "SQL error: cannot create the connection.";
    private static final String SAX_ERROR = "SAX Parser error: cannot create the parser or the factory.";

    public static void main(String argv[]) throws ClassNotFoundException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put(USER, userName);
        p.put(PASSWORD, pass);

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            try {
                Connection c = DriverManager.getConnection(CONNECTION, p);
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
                                    + STREET_SECTIONS_TABLE
                                    + STREET_SECTIONS_TABLE_HEADERS
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
                                        sqlStatement,
                                        STREET_SECTIONS_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(TAG)
                                && attributes.getValue(V).equals(PARKING)
                                && getParkingAttr) {

                            String sqlStatement = UPDATE
                                    + STREET_SECTIONS_TABLE
                                    + SET
                                    + PARKING_EQUALS_ONE
                                    + WHERE
                                    + SECTION_ID_EQUALS
                                    + nodeId
                                    + SEMICOLON;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, STREET_SECTIONS_UPDATE_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(WAY)) {

                            getNameAttr = true;
                            getParkingAttr = false;

                            wayId = attributes.getValue(ID);

                            String sqlStatement = INSERT_INTO
                                    + STREETS_TABLE
                                    + STREETS_TABLE_HEADER
                                    + VALUES
                                    + QUOTATION_MARK
                                    + wayId
                                    + CLOSING_BRACKET;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, STREETS_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(ND)) {

                            String nodeRef = attributes.getValue(REF);

                            String sqlStatement = INSERT_INTO
                                    + STREET_REFERENCES_TABLE
                                    + STREET_REFERENCES_TABLE_HEADERS
                                    + VALUES
                                    + QUOTATION_MARK
                                    + wayId
                                    + QUOTATION_MARKS_WITH_COMMA
                                    + nodeRef
                                    + CLOSING_BRACKET;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement,
                                        STREET_REFERENCES_NEW_RECORD_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        } else if (qName.equalsIgnoreCase(TAG)
                                && attributes.getValue(K).equals(NAME)
                                && getNameAttr) {

                            String nameOfStreet = attributes.getValue(V);

                            String sqlStatement = UPDATE
                                    + STREETS_TABLE
                                    + SET
                                    + NAME_OF_STREET_EQUALS
                                    + QUOTATION_MARK
                                    + nameOfStreet
                                    + QUOTATION_MARK
                                    + SPACE
                                    + WHERE
                                    + STREET_ID_EQUALS
                                    + wayId
                                    + SEMICOLON;

                            try {
                                CommonJdbcMethods.executeUpdateStatement(stmt,
                                        sqlStatement, STREETS_UPDATE_ERROR);
                            } catch (ForwardedSqlException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }

                        }

                    }
                };

                saxParser.parse(OSM_FILE_PATH, handler);
                System.out.println("Parsing is done!");
                CommonJdbcMethods.closeConnections(c, stmt);
                System.out.println("SQL connections are closed!");
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
