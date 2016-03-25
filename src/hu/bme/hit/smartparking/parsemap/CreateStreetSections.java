package hu.bme.hit.smartparking.parsemap;

import hu.bme.hit.smartparking.jdbc.CommonJdbcMethods;
import hu.bme.hit.smartparking.jdbc.ForwardedSqlException;
import hu.bme.hit.smartparking.map.MapHandler;
import hu.bme.hit.smartparking.map.Node;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class CreateStreetSections {
    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String HOST = "localhost";
    private static final String DATABASE = "vehicle_data";
    private static final String CONNECTION = "jdbc:mysql://"
            + HOST
            + "/"
            + DATABASE
            + "?useUnicode=yes&characterEncoding=UTF-8";
    private static final String USER = "user";
    private static final String userName = "smartparking";
    private static final String PASSWORD = "password";
    private static final String pass = "spict2015";

    private static final String SELECT = "SELECT ";
    private static final String FROM = "FROM ";
    private static final String INNER_JOIN = "INNER JOIN ";
    private static final String ON = "ON ";
    private static final String WHERE = "WHERE ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String REQUESTED_FIELDS = "vehicle_data.budapest_street_references_not_null.node_id, "
            + "vehicle_data.budapest_nodes_not_null.latitude, "
            + "vehicle_data.budapest_nodes_not_null.longitude ";
    private static final String STREET_REFERENCES_TABLE = "vehicle_data.budapest_street_references_not_null ";
    private static final String NODES_TABLE = "vehicle_data.budapest_nodes_not_null ";
    private static final String STREET_SECTIONS_TABLE = "vehicle_data.budapest_street_sections_not_null ";
    private static final String STREET_SECTIONS_TABLE_HEADERS = "(street_id,node_id_1,latitude_1,longitude_1,node_id_2,latitude_2,longitude_2,length_of_section) ";
    private static final String JOIN_CONDITION = "vehicle_data.budapest_street_references_not_null.node_id=vehicle_data.budapest_nodes_not_null.node_id ";
    private static final String STREET_ID_EQUALS = "street_id=";
    private static final String NODE_ID = "node_id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SEMICOLON = ";";
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = "VALUES (";
    private static final String QUOTATION_MARK = "'";
    private static final String QUOTATION_MARKS_WITH_COMMA = "','";
    private static final String CLOSING_BRACKET = "');";

    private static final String CONNECTION_ERROR = "SQL error: cannot create the connection.";
    private static final String SQL_QUERY_ERROR = "SQL error: cannot execute query.";
    private static final String SQL_CONNECTIONS_ARE_CLOSED = "SQL connections are closed!";
    private static final String STREET_SECTIONS_NEW_RECORD_ERROR = "SQL error: cannot create new record in table budapest_street_sections_not_null.";

    public static void main(String argv[]) throws ClassNotFoundException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put(USER, userName);
        p.put(PASSWORD, pass);

        try {
            final Connection c = DriverManager.getConnection(CONNECTION, p);
            final Statement streetsStatement = c.createStatement();
            final Statement nodesStatement = c.createStatement();
            final Statement streetSectionsStatement = c.createStatement();

            String sqlQueryFromStreetsTable = "SELECT street_id FROM "
                    + DATABASE
                    + ".budapest_streets_not_null;";
            String sqlQueryFromStreetsTableErrorMsg = "SQL error: query from budapest_streets_not_null was unsuccessful.";
            ResultSet streetsResultSet = CommonJdbcMethods
                    .executeQueryStatement(streetsStatement,
                            sqlQueryFromStreetsTable,
                            sqlQueryFromStreetsTableErrorMsg);
            ResultSet nodesResultSet = null;

            int streetCounter = 1;
            while (streetsResultSet.next()) {
                long streetId = streetsResultSet.getLong("street_id");

                String sqlQueryFromStreetReferencesTable = SELECT
                        + REQUESTED_FIELDS
                        + FROM
                        + STREET_REFERENCES_TABLE
                        + INNER_JOIN
                        + NODES_TABLE
                        + ON
                        + JOIN_CONDITION
                        + WHERE
                        + STREET_ID_EQUALS
                        + streetId
                        + ORDER_BY
                        + LATITUDE
                        + SEMICOLON;
                String sqlQueryFromStreetReferencesTableErrorMsg = "SQL error: query from budapest_streets_not_null was unsuccessful.";
                nodesResultSet = CommonJdbcMethods.executeQueryStatement(
                        nodesStatement, sqlQueryFromStreetReferencesTable,
                        sqlQueryFromStreetReferencesTableErrorMsg);

                ArrayList<Node> nodes = new ArrayList<>();
                while (nodesResultSet.next()) {
                    nodes.add(new Node(nodesResultSet.getLong(NODE_ID),
                            nodesResultSet.getDouble(LATITUDE), nodesResultSet
                                    .getDouble(LONGITUDE)));
                }

                int numOfSections = nodes.size() - 1;
                for (int i = 0, j = 0; i < numOfSections; i++) {
                    Node startNode = nodes.get(j);
                    nodes.remove(j);

                    long minNodeId = 0;
                    double minLat = 0;
                    double minLon = 0;
                    double minDistance = Double.POSITIVE_INFINITY;

                    for (int k = 0; k < nodes.size(); k++) {
                        Node potentialEndNode = nodes.get(k);
                        double distance = MapHandler.getDistance(startNode,
                                potentialEndNode);

                        if (distance < minDistance) {
                            minNodeId = potentialEndNode.getNodeId();
                            minLat = potentialEndNode.getLatitude();
                            minLon = potentialEndNode.getLongitude();
                            minDistance = distance;
                            j = k;
                        }
                    }

                    String sqlInsertIntoStatement = INSERT_INTO
                            + STREET_SECTIONS_TABLE
                            + STREET_SECTIONS_TABLE_HEADERS
                            + VALUES
                            + QUOTATION_MARK
                            + streetId
                            + QUOTATION_MARKS_WITH_COMMA
                            + startNode.getNodeId()
                            + QUOTATION_MARKS_WITH_COMMA
                            + startNode.getLatitude()
                            + QUOTATION_MARKS_WITH_COMMA
                            + startNode.getLongitude()
                            + QUOTATION_MARKS_WITH_COMMA
                            + minNodeId
                            + QUOTATION_MARKS_WITH_COMMA
                            + minLat
                            + QUOTATION_MARKS_WITH_COMMA
                            + minLon
                            + QUOTATION_MARKS_WITH_COMMA
                            + minDistance * 1000
                            + CLOSING_BRACKET;
                    CommonJdbcMethods.executeUpdateStatement(streetSectionsStatement,
                            sqlInsertIntoStatement,
                            STREET_SECTIONS_NEW_RECORD_ERROR);
                }
                System.out.println(streetCounter++);
            }

            CommonJdbcMethods.closeConnections(c, streetSectionsStatement);
            CommonJdbcMethods.closeConnections(c, nodesStatement,
                    nodesResultSet);
            CommonJdbcMethods.closeConnections(c, streetsStatement,
                    streetsResultSet);
            System.out.println(SQL_CONNECTIONS_ARE_CLOSED);
        } catch (SQLException e) {
            System.out.println(CONNECTION_ERROR);
            e.printStackTrace();
        } catch (ForwardedSqlException e) {
            System.out.println(SQL_QUERY_ERROR);
            e.printStackTrace();
        }
    }
}
