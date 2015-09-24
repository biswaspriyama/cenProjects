package modules;
import beans.MysqlConnector;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
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

    //old library will remove it later

    public void createCorrelationMatrix() {

        ParallelFileReader pRead = new ParallelFileReader();
        ArrayList<float[]> completeData = pRead.LoadEntireDataNew();
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

        System.out.print("I am about to start");
        int edgeCount = 0;
        int param = 0;
        for (int x = 0; x < dataSize; x++) {
            for (int y = x + 1; y < dataSize; y++) {
                float correlationCoeff = computeCoefficients(x, y, AllMeans[x], AllMeans[y], completeData);
                System.out.print(correlationCoeff + "\n");
                if (correlationCoeff >= Configurations.maxCorrelationCoeff) {
                    graph.setEdge(x, y);
                    edgeCount++;
                    param++;
                    //System.out.print(edgeCount + "\n");
                }
            }
        }

        System.out.print("\n");
        System.out.print(edgeCount);
        System.out.print("\n");
    }

    float computeCoefficients(int x, int y, float meanX, float meanY, ArrayList<float[]> completeData) {
        float Sx = 0;
        float Sy = 0;
        float Sxy = 0;
        //System.out.print(meanX+","+ meanY);
        if (meanX == 0 || meanY == 0)
            return 0;
        for (int timeSeries = 0; timeSeries < Configurations.timeSeries; timeSeries++) {
            float term1 = (completeData.get(timeSeries)[x] - meanX);
            Sx = Sx + term1;
            if (Sx == 0.0)
                return 0;
            float term2 = (completeData.get(timeSeries)[y] - meanY);
            Sy = Sy + term2;
            if (Sy == 0.0)
                return 0;
            Sxy = Sxy + term1 * term2;
        }
        //System.out.print(Sxy+","+Sx+','+Sy + "\n");
        float correlationCoeff = Math.abs(Sxy / (Sx * Sy));
        return correlationCoeff;
    }


    //THESE FUNCTIONS ARE FOR NEW LIBRARY

    public void createCorrelationMatrixNew() {

        ParallelFileReaderNew pRead = new ParallelFileReaderNew();
        ArrayList<float[]> completeData = pRead.LoadEntireData();
        ListIterator listIterator = completeData.listIterator();
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
                correlationCoeff = computeCoefficientsNew(x, y, AllMeans[x], AllMeans[y], completeData);
                if (correlationCoeff >= Configurations.maxCorrelationCoeff) {
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


    float computeCoefficientsNew(int x, int y, float meanX, float meanY, ArrayList<float[]> completeData) {
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
