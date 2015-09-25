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

    public static void plotAllGraphs(){

        String chartTitle = "Degree Distribution Graph for r > 0.95";
        String xAxisLabel = "Area Location";
        String yAxisLabel = "Degree";
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MysqlConnector sqlObj = new MysqlConnector();
                float[] edgeCount1 = sqlObj.edgeCount(MySqlConfig.tableName);
                new PlotGraphs(edgeCount1, chartTitle, xAxisLabel, yAxisLabel).setVisible(true);

                float[] edgeCount2 = sqlObj.edgeCount(MySqlConfig.testTable);
                new PlotGraphs(edgeCount2, "Degree Distribution Graph for r > 0.90", xAxisLabel, yAxisLabel).setVisible(true);


            }
        });
    }

    public static void smallWorld() {

//        AdjacencyListGraph G = new AdjacencyListGraph(5);
//        G.setEdge(1,2);
//        G.setEdge(3,4);
//        G.setEdge(2,3);
//        G.setEdge(1,3);
//        G.setEdge(1,4);
//        PathFinder pf=new PathFinder();
        MysqlConnector sql = new MysqlConnector();
        AdjacencyListGraph G = sql.readStringRows(MySqlConfig.tableName);


        for(int i=0; i < Configurations.actualDatasize;i++)
            G.getClusteringCoefficientSingleNode(G, i);
            //G.getDistance(G, i);

    }

    public static void main(String[] args) {
        //java.util.Date date = new java.util.Date();
        //System.out.println(new Timestamp(date.getTime()));
        //initialConfig();

        //To calculate correlation
        //CorrelationCalculator crObj = new CorrelationCalculator();
        //crObj.createCorrelationMatrixNew();

        //plotAllGraphs();
        smallWorld();

        //date = new java.util.Date();
        //System.out.println(new Timestamp(date.getTime()));




    }
}