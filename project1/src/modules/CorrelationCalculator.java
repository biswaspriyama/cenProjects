package modules;
import enums.Configurations;

import java.io.FileNotFoundException;
import java.util.*;
import beans.AdjacencyListGraph;

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
        int edgeCount = 0;
        int x = 0;
        int param = 0;
        for (x = 0; x < dataSize; x++) {
            //while(param < Configurations.actualDatasize) {
            for (int y = x + 1; y < Configurations.actualDatasize; y++) {
                float correlationCoeff = computeCoefficientsNew(x, y, AllMeans[x], AllMeans[y], completeData);
                System.out.print(correlationCoeff + "\n");
                if (correlationCoeff >= Configurations.maxCorrelationCoeff) {
                    graph.setEdge(x, y);
                    edgeCount++;
                    //System.out.print(edgeCount+"\n");
                }
            }
            //completeData.remove(0);
            param++;
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
            Sx = Sx + term1;
            float term2 = (yData[i] - meanY);
            Sy = Sy + term2;
            Sxy = Sxy + term1 * term2;
        }
        //System.out.print(Sxy+","+Sx+','+Sy + "\n");
        if (Sx == 0 || Sy == 0)
            return 0;

        float correlationCoeff = Math.abs(Sxy / (Sx * Sy));
        return correlationCoeff;
    }

}
//Not used


//    public void computeNew(){
//        ParallelFileReader pRead = new ParallelFileReader();
//        ArrayList<float[]> completeData = pRead.LoadEntireDataNew();
//        int dataSize = completeData.get(0).length;
//        System.out.print(dataSize);
//
//
//        float[] AllMeans = new float[dataSize];
//
//        try {
//            AllMeans = readObj.readMeanFile(Configurations.meanOutFile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//        AdjacencyListGraph graph = new AdjacencyListGraph(dataSize);
//        //ParallelCorrelation pCorr = new ParallelCorrelation(completeData);
//
//        System.out.print("I am about to start");
//
//        int count = 0;
//        int param = 0 ;
//        int edgeLimit = dataSize*(dataSize-1)/2;
//        ArrayList<Thread> threadList = new ArrayList<>();
//        for(int x = 0; x < dataSize; x++){
//            for (int y = x+1; y < dataSize; y++) {
//                ParallelCorrelation pCorr = new ParallelCorrelation(x, y, AllMeans[x], AllMeans[y], graph, completeData);
//                threadList.add(new Thread(pCorr));
//
//                if (count == Configurations.cThreadLimit || param == edgeLimit-1){
//
//                    for (Thread thread : threadList)
//                        thread.start();
//                    for (Thread thread : threadList)
//                        try {
//                            thread.join();
//                        } catch (InterruptedException e) {
//                            System.out.print("Unable to join threads");
//                        }
//                    threadList = new ArrayList<>();
//                    count=0;
//                }
//            }
//
//        }
//    }
//}
