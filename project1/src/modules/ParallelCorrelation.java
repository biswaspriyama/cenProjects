package modules;

import beans.AdjacencyListGraph;
import enums.Configurations;

import java.util.ArrayList;

/**
 * Created by yugarsi on 9/4/15.
 */
public class ParallelCorrelation implements Runnable {

    int x;
    int y;
    float meanX;
    float meanY;
    AdjacencyListGraph graphObj;
    CorrelationCalculator crObj = new CorrelationCalculator();
    public  ArrayList<float[]> completeData = new ArrayList<float[]>();
    public static int edgeCount=0;


    public ParallelCorrelation(int x, int y, float meanX, float meanY, AdjacencyListGraph graph, ArrayList<float[]> data ) {
        this.x = x;
        this.y = y;
        this.meanX = meanX;
        this.meanY = meanY;
        this.graphObj = graph;
        this.completeData = data;


    }

    public void run() {

        float correlationCoeff = crObj.computeCoefficients(this.x, this.y, this.meanX, this.meanY, this.completeData);
        System.out.print(correlationCoeff);
        if(correlationCoeff >= Configurations.minCorrelationCoeff){
            this.graphObj.setEdge(this.x, this.y);
                edgeCount++;
                System.out.print(edgeCount);

        }

    }
    float computeCoeff(int x, int y, float meanX, float meanY, ArrayList<float[]> completeData){
        float Sx = 0;
        float Sy = 0;
        float Sxy = 0;
        System.out.print(meanX+","+ meanY);
        if (meanX == 0 || meanY == 0)
            return 0;
        for (int timeSeries = 0 ; timeSeries< Configurations.timeSeries; timeSeries++){
            float term1 = (completeData.get(timeSeries)[x] - meanX);
            Sx = Sx + term1;
            if (Sx == 0.0)
                return 0;
            float term2 = (completeData.get(timeSeries)[y] - meanY);
            Sy = Sy +term2;
            if (Sy == 0.0)
                return 0;
            Sxy = Sxy + term1*term2;

        }
        System.out.print(Sxy+","+Sx+','+Sy + "\n");
        float correlationCoeff = Math.abs(Sxy/(Sx*Sy));
        return correlationCoeff;
    }

}
