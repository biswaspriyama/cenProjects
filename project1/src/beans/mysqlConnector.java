package beans;

import java.sql.*;
import enums.MySqlConfig;

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
    public void createTable(String TableName){
        try{
            System.out.println("Creating MY SQL DB...");
            stmt = conn.createStatement();
            String sql = "CREATE TABLE REGISTRATION " +
                    "(id INTEGER not NULL, " +
                    " nodes VARCHAR(255), " +
                    " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

            }catch (SQLException se) {
                se.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

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






