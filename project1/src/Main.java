import enums.*;
import beans.*;
import modules.*;

import javax.swing.*;
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
       // writeMeanToFile();
        configureMysql();

    }







    public static void main(String[] args) {

//        AllPlots ap = new AllPlots();
//        ap.plotAllGraphs();
        //        java.util.Date date = new java.util.Date();
//        System.out.println(new Timestamp(date.getTime()));
    //      initialConfig();
////
//       // To calculate correlation
        CorrelationCalculator crObj = new CorrelationCalculator();
        crObj.computeCorrelation(Configurations.maxCorrelationCoeff);



        //date = new java.util.Date();
        //System.out.println(new Timestamp(date.getTime()));


//        MysqlConnector sql = new MysqlConnector();
//        AdjacencyListGraph G = sql.readStringRows(MySqlConfig.testTable);
//        System.out.print("i am starting");
//        //float num = G.getClusteringCoefficientAllNodes(G);
//        //System.out.print(num+"\n");
//        float num = G.getCharacteristicPathLength(G);
//        System.out.print(num);




    }
}