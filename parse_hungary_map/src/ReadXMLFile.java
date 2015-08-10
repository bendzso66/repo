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

    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://127.0.0.1/smart_parking";
    private static final String userName = "root";
    private static final String password = "";

    public static void main(String argv[]) throws ClassNotFoundException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put("user", userName);
        p.put("password", password);

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                private boolean getNameAttr = false;
                private boolean getParkingAttr = false;
                private String nodeId = null;
                private String wayId = null;

                @Override
                public void startElement(String uri, String localName,
                        String qName, Attributes attributes)
                        throws SAXException {

                    Connection c = null;
                    Statement stmt = null;

                    try {
                        c = DriverManager.getConnection(CONNECTION, p);
                        stmt = c.createStatement();

                        if (qName.equalsIgnoreCase("node")) {

                            getNameAttr = false;
                            getParkingAttr = true;

                            nodeId = attributes.getValue("id");
                            String nodeLat = attributes.getValue("lat");
                            String nodeLon = attributes.getValue("lon");

                            System.out.print("NODE       ");
                            System.out.print("id: " + nodeId);
                            System.out.print(" lat: " + nodeLat);
                            System.out.print(" lon: " + nodeLon);
                            String sqlStatement = "INSERT INTO street_sections (section_id, latitude, longitude) VALUES ("
                                    + "'"
                                    + nodeId
                                    + "','"
                                    + nodeLat
                                    + "','"
                                    + nodeLon
                                    + "');";
                            String errorMsg = "SQL error: cannot create new record in table street_sections.";
                            ManageJdbcConnections.executeStatement(stmt,
                                    sqlStatement, errorMsg);

                        } else if (qName.equalsIgnoreCase("tag")
                                && attributes.getValue("v").equals("parking")
                                && getParkingAttr) {

                            System.out.print("NODE TAG   ");
                            System.out.println("parking");

                            String sqlStatement = "UPDATE street_sections SET parking=1 WHERE section_id="
                                    + nodeId
                                    + ";";
                            String errorMsg = "SQL error: cannot update table street_sections.";
                            ManageJdbcConnections.executeStatement(stmt,
                                    sqlStatement, errorMsg);

                        } else if (qName.equalsIgnoreCase("way")) {

                            getNameAttr = true;
                            getParkingAttr = false;

                            wayId = attributes.getValue("id");

                            System.out.print("WAY        ");
                            System.out.print("id: " + wayId);

                            String sqlStatement = "INSERT INTO streets (street_id) VALUES ("
                                    + "'"
                                    + wayId
                                    + "');";
                            String errorMsg = "SQL error: cannot create new record in table streets.";
                            ManageJdbcConnections.executeStatement(stmt,
                                    sqlStatement, errorMsg);

                        } else if (qName.equalsIgnoreCase("nd")) {

                            String nodeRef = attributes.getValue("ref");

                            System.out.print("WAY ND     ");
                            System.out.println("ref: " + nodeRef);

                            String sqlStatement = "INSERT INTO street_references (street_id, section_id) VALUES ("
                                    + "'"
                                    + wayId
                                    + "','"
                                    + nodeRef
                                    + "');";
                            String errorMsg = "SQL error: cannot create new record in table street_references.";
                            ManageJdbcConnections.executeStatement(stmt,
                                    sqlStatement, errorMsg);

                        } else if (qName.equalsIgnoreCase("tag")
                                && attributes.getValue("k").equals("name")
                                && getNameAttr) {

                            String nameOfStreet = attributes.getValue("v");

                            System.out.print("WAY TAG    ");
                            System.out.println("street: " + nameOfStreet);

                            String sqlStatement = "UPDATE streets SET name_of_street='"
                                    + nameOfStreet
                                    + "' WHERE street_id="
                                    + wayId
                                    + ";";
                            String errorMsg = "SQL error: cannot update table streets.";
                            ManageJdbcConnections.executeStatement(stmt,
                                    sqlStatement, errorMsg);

                        }

                    } catch (SQLException e) {
                        System.out
                                .println("SQL error: cannot create the connection.");
                        e.printStackTrace();
                    } finally {
                        ManageJdbcConnections.closeConnections(c, stmt);
                    }
                }

            };

            saxParser
                    .parse("d:\\Programs/repo/parse_hungary_map/hungary_map/hungary-latest.osm",
                            handler);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
