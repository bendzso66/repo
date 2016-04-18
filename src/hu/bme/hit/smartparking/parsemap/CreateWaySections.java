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

public class CreateWaySections {
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
    private static final String ID = "ID ";
    private static final String REQUESTED_FIELDS = "vehicle_data.budapest_way_references.node_id, "
            + "vehicle_data.budapest_nodes.latitude, "
            + "vehicle_data.budapest_nodes.longitude ";
    private static final String WAY_REFERENCES_TABLE = "vehicle_data.budapest_way_references ";
    private static final String NODES_TABLE = "vehicle_data.budapest_nodes ";
    private static final String WAY_SECTIONS_TABLE = "vehicle_data.budapest_way_sections ";
    private static final String WAY_SECTIONS_TABLE_HEADERS = "(way_id,node_id_1,latitude_1,longitude_1,node_id_2,latitude_2,longitude_2,length_of_section) ";
    private static final String JOIN_CONDITION = "vehicle_data.budapest_way_references.node_id=vehicle_data.budapest_nodes.node_id ";
    private static final String WAY_ID = "way_id";
    private static final String WAY_ID_EQUALS = WAY_ID + "=";
    private static final String NODE_ID = "node_id";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SEMICOLON = ";";
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = "VALUES (";
    private static final String QUOTATION_MARK = "'";
    private static final String QUOTATION_MARKS_WITH_COMMA = "','";
    private static final String CLOSING_BRACKET = "');";
    private static final String UPDATE_BUDAPEST_WAYS = "UPDATE vehicle_data.budapest_ways ";
    private static final String SET_LATITUDE_1_EQUALS = "SET latitude_1=";
    private static final String COMMA = ", ";
    private static final String LONGITUDE_1_EQUALS = "longitude_1=";
    private static final String CENTER_LATITUDE_EQUALS = "center_latitude=";
    private static final String CENTER_LONGITUDE_EQUALS = "center_longitude=";
    private static final String LATITUDE_2_EQUALS = "latitude_2=";
    private static final String LONGITUDE_2_EQUALS = "longitude_2=";
    private static final String WHERE_WAY_ID_EQUALS = "WHERE " + WAY_ID_EQUALS;

    private static final String CONNECTION_ERROR = "SQL error: cannot create the connection.";
    private static final String SQL_QUERY_ERROR = "SQL error: cannot execute query.";
    private static final String SQL_CONNECTIONS_ARE_CLOSED = "SQL connections are closed!";
    private static final String WAY_SECTIONS_NEW_RECORD_ERROR = "SQL error: cannot create new record in table budapest_way_sections.";
    private static final String WAY_REFERENCES_QUERY_ERROR = "SQL error: query from budapest_ways was unsuccessful.";
    private static final String WAYS_UPDATE_ERROR = "SQL error: update in budapest_ways was unsuccessful.";

    public static void main(String argv[]) throws ClassNotFoundException {

        Class.forName(DB_CLASS_NAME);
        final Properties p = new Properties();
        p.put(USER, userName);
        p.put(PASSWORD, pass);

        try {
            final Connection c = DriverManager.getConnection(CONNECTION, p);
            final Statement waysStatement = c.createStatement();
            final Statement nodesStatement = c.createStatement();
            final Statement waySectionsStatement = c.createStatement();

            String sqlQueryFromWaysTable = "SELECT way_id FROM "
                    + DATABASE
                    + ".budapest_ways;";
            String sqlQueryFromWaysTableErrorMsg = "SQL error: query from budapest_ways was unsuccessful.";
            ResultSet waysResultSet = CommonJdbcMethods.executeQueryStatement(
                    waysStatement, sqlQueryFromWaysTable,
                    sqlQueryFromWaysTableErrorMsg);
            ResultSet nodesResultSet = null;

            int wayCounter = 1;
            while (waysResultSet.next()) {
                long wayId = waysResultSet.getLong(WAY_ID);

                String sqlQueryFromWayReferencesTable = SELECT
                        + REQUESTED_FIELDS
                        + FROM
                        + WAY_REFERENCES_TABLE
                        + INNER_JOIN
                        + NODES_TABLE
                        + ON
                        + JOIN_CONDITION
                        + WHERE
                        + WAY_ID_EQUALS
                        + wayId
                        + ORDER_BY
                        + ID
                        + SEMICOLON;
                nodesResultSet = CommonJdbcMethods.executeQueryStatement(
                        nodesStatement, sqlQueryFromWayReferencesTable,
                        WAY_REFERENCES_QUERY_ERROR);

                ArrayList<Node> nodes = new ArrayList<>();
                while (nodesResultSet.next()) {
                    nodes.add(new Node(nodesResultSet.getLong(NODE_ID),
                            nodesResultSet.getDouble(LATITUDE), nodesResultSet
                                    .getDouble(LONGITUDE)));
                }

                int numOfNodes = nodes.size();
                if (numOfNodes > 0) {
                    int middleNodePos = (int) Math.floor(numOfNodes / 2);
                    String sqlUpdateStatement = UPDATE_BUDAPEST_WAYS
                            + SET_LATITUDE_1_EQUALS
                            + nodes.get(0).getLatitude()
                            + COMMA
                            + LONGITUDE_1_EQUALS
                            + nodes.get(0).getLongitude()
                            + COMMA
                            + CENTER_LATITUDE_EQUALS
                            + nodes.get(middleNodePos).getLatitude()
                            + COMMA
                            + CENTER_LONGITUDE_EQUALS
                            + nodes.get(middleNodePos).getLongitude()
                            + COMMA
                            + LATITUDE_2_EQUALS
                            + nodes.get(numOfNodes - 1).getLatitude()
                            + COMMA
                            + LONGITUDE_2_EQUALS
                            + nodes.get(numOfNodes - 1).getLongitude()
                            + WHERE_WAY_ID_EQUALS
                            + wayId
                            + SEMICOLON;
                    CommonJdbcMethods.executeUpdateStatement(
                            waySectionsStatement, sqlUpdateStatement,
                            WAYS_UPDATE_ERROR);
                }

                int numOfSections = numOfNodes - 1;
                for (int i = 0; i < numOfSections; i++) {
                    Node startNode = nodes.get(i);
                    Node endNode = nodes.get(i + 1);
                    double distance = MapHandler
                            .getDistance(startNode, endNode);

                    String sqlInsertIntoStatement = INSERT_INTO
                            + WAY_SECTIONS_TABLE
                            + WAY_SECTIONS_TABLE_HEADERS
                            + VALUES
                            + QUOTATION_MARK
                            + wayId
                            + QUOTATION_MARKS_WITH_COMMA
                            + startNode.getNodeId()
                            + QUOTATION_MARKS_WITH_COMMA
                            + startNode.getLatitude()
                            + QUOTATION_MARKS_WITH_COMMA
                            + startNode.getLongitude()
                            + QUOTATION_MARKS_WITH_COMMA
                            + endNode.getNodeId()
                            + QUOTATION_MARKS_WITH_COMMA
                            + endNode.getLatitude()
                            + QUOTATION_MARKS_WITH_COMMA
                            + endNode.getLongitude()
                            + QUOTATION_MARKS_WITH_COMMA
                            + distance
                            * 1000
                            + CLOSING_BRACKET;
                    CommonJdbcMethods.executeUpdateStatement(
                            waySectionsStatement, sqlInsertIntoStatement,
                            WAY_SECTIONS_NEW_RECORD_ERROR);
                }
                System.out.println(wayCounter++);
            }

            CommonJdbcMethods.closeConnections(c, waySectionsStatement);
            CommonJdbcMethods.closeConnections(c, nodesStatement,
                    nodesResultSet);
            CommonJdbcMethods.closeConnections(c, waysStatement, waysResultSet);
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
