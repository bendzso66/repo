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

    static boolean getNameAttr = false;

    public static void main(String argv[]) throws ClassNotFoundException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put("user", userName);
        p.put("password", password);

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                @Override
                public void startElement(String uri, String localName,
                        String qName, Attributes attributes)
                        throws SAXException {

                    Connection c = null;
                    Statement stmt = null;

                    try {
                        c = DriverManager.getConnection(CONNECTION, p);
                        stmt = c.createStatement();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (qName.equalsIgnoreCase("node")) {

                        getNameAttr = false;

                        System.out.print("NODE       ");
                        System.out.print("id: " + attributes.getValue("id"));
                        System.out.print(" lat: " + attributes.getValue("lat"));
                        System.out.print(" lon: " + attributes.getValue("lon"));
                        System.out.println(" changeset: "
                                + attributes.getValue("changeset"));
                        try {
                            stmt.execute("INSERT INTO street_sections (section_id, latitude, longitude, changeset) VALUES ("
                                    + "'"
                                    + attributes.getValue("id")
                                    + "','"
                                    + attributes.getValue("lat")
                                    + "','"
                                    + attributes.getValue("lon")
                                    + "','"
                                    + attributes.getValue("changeset") + "');");
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (qName.equalsIgnoreCase("tag")
                            && attributes.getValue("v").equals("parking")) {
                        System.out.print("NODE TAG   ");
                        System.out.println("parking");
                    } else if (qName.equalsIgnoreCase("way")) {
                        System.out.print("WAY        ");
                        getNameAttr = true;
                        System.out.print("id: " + attributes.getValue("id"));
                        System.out.println(" changeset: "
                                + attributes.getValue("changeset"));
                    } else if (qName.equalsIgnoreCase("nd")) {
                        System.out.print("WAY ND     ");
                        System.out
                                .println("ref: " + attributes.getValue("ref"));
                    } else if (qName.equalsIgnoreCase("tag")
                            && attributes.getValue("k").equals("name")
                            && getNameAttr) {
                        System.out.print("WAY TAG    ");
                        System.out.println("street: "
                                + attributes.getValue("v"));
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
