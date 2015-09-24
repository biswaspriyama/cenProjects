import enums.*;
import beans.*;
import modules.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class Main {

    public static void configureMysql(){


        MysqlConnector sqlObj= new MysqlConnector();
        if (Configurations.FirstRun)
            sqlObj.createDatabase(MySqlConfig.DbName);

        sqlObj.createTable(MySqlConfig.tableName);
        //sqlObj.readStringRows(MySqlConfig.testTable);
        sqlObj.closeDbConnection();

    }

    public static void writeMeanToFile(){
        //To compute Mean
        InputReaderService ir = new InputReaderService();
        try {
            ir.computeGlobalMean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialConfig(){
        //writeMeanToFile();
        configureMysql();

    }

    public static void main(String[] args) {
        //java.util.Date date = new java.util.Date();
        //System.out.println(new Timestamp(date.getTime()));
        //initialConfig();

        //To calculate correlation
        //CorrelationCalculator crObj = new CorrelationCalculator();
        //crObj.createCorrelationMatrixNew();
        PlotGraphs pg = new PlotGraphs();
        pg.plotDegreeDistribution();


        //date = new java.util.Date();
        //System.out.println(new Timestamp(date.getTime()));




    }
}