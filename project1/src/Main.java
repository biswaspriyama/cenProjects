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
        writeMeanToFile();
        configureMysql();

    }



    public static void main(String[] args) {
        initialConfig();

        //To calculate correlation
        CorrelationCalculator crObj = new CorrelationCalculator();
        int lag = 4;
        crObj.computeCorrelation(Configurations.maxCorrelationCoeff,lag);


        GraphParameters gr = new GraphParameters();
        gr.computeAllParameters();


        AllPlots ap = new AllPlots();
        ap.plotAllGraphs();






    }
}