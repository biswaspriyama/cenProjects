package beans;
import java.math.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import enums.Configurations;
import enums.MySqlConfig;
import beans.AdjacencyListGraph;

/**
 * Created by yugarsi on 9/7/15.
 */
public class MysqlConnector {
    Connection conn = null;
    Statement stmt = null;
    public MysqlConnector() {
        try {
            Class.forName(MySqlConfig.driver);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(MySqlConfig.dbUrl, MySqlConfig.user, MySqlConfig.password);
            System.out.print("Connection Successful! ");
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createDatabase(String DbName){
        try {
            System.out.println("Creating MY SQL DB...");
            stmt = conn.createStatement();

            String sql = "CREATE DATABASE "+DbName;
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully!");
            }catch (SQLException se) {
                se.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    public void createTable(String tableName){
        try{
            System.out.println("Creating MY SQL DB...");
            stmt = conn.createStatement();
            String sql = "CREATE TABLE  "+tableName +
                    "(id INTEGER not NULL, " +
                    " Nodes LONGTEXT, " +
                    " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

            }catch (SQLException se) {
                se.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void insertValues(String tableName, int vertexNum, String edges){
        String query = "INSERT INTO "+tableName+" (id, Nodes) VALUES (?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, vertexNum);
            pstmt.setString(2, edges);
            pstmt.execute();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public AdjacencyListGraph readStringRows(String tableName){

        AdjacencyListGraph graph = new AdjacencyListGraph(Configurations.actualDatasize);
        try {
            Statement st = conn.createStatement();
            st.executeQuery("SELECT Nodes FROM "+tableName);//+"WHERE id="+Integer.toString(id));
            ResultSet rs = st.getResultSet();
            int count = 0;
            int i;
            while (rs.next()) {
                String nodes = rs.getString("Nodes");

                if (nodes != ""){
                    nodes = nodes.substring(1); //removing first comma
                    List<String> edgeList = Arrays.asList(nodes.split(","));
                    for(i=0;i<edgeList.size();i++)
                        graph.setEdge(count,Integer.parseInt(edgeList.get(i)));
                    count ++;
                }
            }
            rs.close();
            st.close();
            System.out.println(count + " rows were retrieved");
        }catch (SQLException se){
            throw new RuntimeException(se);
        }
        return graph;
    }

    public float[] edgeCount(String tableName){

        float[] degree = new float[Configurations.actualDatasize];
        try {
            Statement st = conn.createStatement();
            st.executeQuery("SELECT Nodes FROM "+tableName);//+"WHERE id="+Integer.toString(id));
            ResultSet rs = st.getResultSet();
            int count = 0;
            while (rs.next()) {
                String nodes = rs.getString("Nodes");

                if (nodes != ""){
                    nodes = nodes.substring(1); //removing first comma
                    List<String> edgeList = Arrays.asList(nodes.split(","));
                    //System.out.print(edgeList.size()+"\n");
                    degree[count] = edgeList.size();
                    count++;
                }
                else {
                    degree[count] = 0;
                    count++;

                }
            }
            rs.close();
            st.close();
        }catch (SQLException se){
            throw new RuntimeException(se);
        }
        return degree;
    }

    public void closeDbConnection() {
        try{
            if(stmt!=null)
                stmt.close();
        }catch(SQLException se2){
            System.out.print("exception");
        }
        try{
            if(conn!=null)
                conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }


}






