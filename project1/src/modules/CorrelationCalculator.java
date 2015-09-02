package modules;
import enums.Configurations;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import beans.AdjacencyListGraph;

/**
 * Created by yugarsi on 9/1/15.
 */
public class CorrelationCalculator {

    public static float[] magikMat = new float[Configurations.areaDimension];
    InputReaderService readObj = new InputReaderService();

    public CorrelationCalculator(){
        for (int i=0;i<Configurations.areaDimension;i++)
            magikMat[i]=0;
    }

    public void createCorrelationMatrix(){

        ParallelFileReader pRead = new ParallelFileReader();
        ArrayList<float[]> completeData = pRead.LoadEntireDataNew();
        int dataSize = completeData.get(0).length;
        System.out.print(dataSize);
        AdjacencyListGraph graph = new AdjacencyListGraph(dataSize);

        float[] AllMeans = new float[dataSize];
        try {
            AllMeans = readObj.readMeanFile(Configurations.meanOutFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int count = 0;
        for(int x = 0; x < dataSize; x++)
            for (int y = x+1; y < dataSize; y++) {
                float correlationCoeff = computeCoefficients(x, y, AllMeans[x], AllMeans[y], completeData);
                if((correlationCoeff >=Configurations.minCorrelationCoeff && correlationCoeff<=Configurations.maxCorrelationCoeff)){
                    graph.setEdge(x,y);
                    count++;
                }

            }

        System.out.print("\n");
        System.out.print(count);
        System.out.print("\n");
    }

    float computeCoefficients(int x, int y, float meanX, float meanY, ArrayList<float[]> completeData){
        float Sxx = 0;
        float Syy = 0;
        float Sxy = 0;

        for (int timeSeries = 0 ; timeSeries< Configurations.timeSeries; timeSeries++){
            float term1 = (completeData.get(timeSeries)[x] - meanX);
            Sxx = Sxx + term1*term1;
            float term2 = (completeData.get(timeSeries)[y] - meanY);
            Syy = Syy +term2*term2;

            Sxy = Sxy + term1*term2;
        }

        double correlationCoeff = Sxy/ Math.sqrt(Sxx*Syy);
        return (float)correlationCoeff;
    }


    //this is not used now. A different approach needs to be done in case of memory overflow . This will solve it

    void updateMagikMat(ArrayList<float[]> partialData){

        int dataSize = partialData.get(0).length;
        float[] AllMeans = new float[dataSize];
        try {
            AllMeans = readObj.readMeanFile(Configurations.meanOutFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int timeSeries = 0 ; timeSeries < partialData.size() ; timeSeries++)
        for(int i=0; i < Configurations.areaDimension;i++){
            magikMat[i]+= (partialData.get(timeSeries)[i] - AllMeans[i]);
        }


    }

}
