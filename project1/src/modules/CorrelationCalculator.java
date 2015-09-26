package modules;
import beans.MysqlConnector;
import enums.Configurations;

import java.io.*;
import java.util.*;
import beans.AdjacencyListGraph;
import enums.MySqlConfig;

/**
 * Created by yugarsi on 9/1/15.
 */
public class CorrelationCalculator {


    InputReaderService readObj = new InputReaderService();


    //THESE FUNCTIONS ARE FOR NEW LIBRARY

    public void computeCorrelation(double threshold) {

        ParallelFileReader pRead = new ParallelFileReader();
        ArrayList<float[]> completeData = pRead.LoadEntireData();
        int dataSize = Configurations.actualDatasize;
        System.out.print(dataSize);


        float[] AllMeans = new float[dataSize];

        try {
            AllMeans = readObj.readMeanFile(Configurations.meanOutFile);
            System.out.print("," + AllMeans.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AdjacencyListGraph graph = new AdjacencyListGraph(dataSize);
        System.out.print("I am Starting\n");

        MysqlConnector sqlObj= new MysqlConnector();

        int edgeCount = 0;
        float correlationCoeff;
        int x = 0;
        int y;
        for (x = 0; x < dataSize; x++) {
            String allEdges = "";
            for (y = x + 1; y < Configurations.actualDatasize; y++) {
                correlationCoeff = computeCoefficients(x, y, AllMeans[x], AllMeans[y], completeData);
                if (correlationCoeff >= threshold) {
                    allEdges=allEdges+","+Integer.toString(y);
                    if (!Configurations.writeToDb){
                        graph.setEdge(x,y);
                    }
                    edgeCount++;
                }
            }
            sqlObj.insertValues(MySqlConfig.tableName, x, allEdges);

        }

        System.out.print("\n");
        System.out.print(edgeCount);
        System.out.print("\n");
    }


    float computeCoefficients(int x, int y, float meanX, float meanY, ArrayList<float[]> completeData) {
        float Sx = 0;
        float Sy = 0;
        float Sxy = 0;

        if (meanX == 0 || meanY == 0)
            return 0;

        float[] xData = completeData.get(x);
        float[] yData = completeData.get(y);
        for (int i = 0; i < xData.length; i++) {
            float term1 = (xData[i] - meanX);
            Sx = Sx + term1*term1;
            float term2 = (yData[i] - meanY);
            Sy = Sy + term2*term2;
            Sxy = Sxy + term1 * term2;
        }
        //System.out.print(Sxy+","+Sx+','+Sy + "\n");
        if (Sx == 0 || Sy == 0)
            return 0;

        float correlationCoeff = Math.abs(Sxy / (float)Math.sqrt(Sx * Sy));
        return correlationCoeff;
    }

}
