package modules;

import beans.AdjacencyListGraph;
import beans.MysqlConnector;
import enums.Configurations;
import enums.MySqlConfig;

import javax.swing.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by yugarsi on 9/26/15.
 */
public class AllPlots {

    public  float[] appendLandIndex(String tableName){

        MysqlConnector sqlObj = new MysqlConnector();
        AdjacencyListGraph graph = sqlObj.readStringRows(tableName);
        float[] vertexDegree = graph.getAlldegrees(graph);
        float[] finalData = new float[Configurations.areaDimension];
        try{
            Queue<Integer> landIndexes = InputReaderService.getLandIndexes(Configurations.sampleFileName);
            int i=0;
            int j=0;
            int index;
            while(!landIndexes.isEmpty()){
                index = landIndexes.peek();
                while (j<index){
                    finalData[j]= vertexDegree[i];
                    i++;
                    j++;
                }
                finalData[index]=0;
                j++;
                landIndexes.remove();
            }
            while (i<vertexDegree.length){
                finalData[j]= vertexDegree[i];
                i++;
                j++;
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return finalData;
        }

    public void inputPlotParameters(String chartTitle, String xAxisLabel, String yAxisLabel, String TableName) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                float[] vertexDegree = appendLandIndex(TableName);
                new PlotGraphs(vertexDegree, chartTitle, xAxisLabel, yAxisLabel).setVisible(true);

            }
        });


    }

    public  void plotAllGraphs(){

        String chartTitle = "Degree Distribution for 1979-1987, r>0.95";
        String xAxisLabel = "Geographic Locations";
        String yAxisLabel = "Degree";
        String TableName = "ice9years1";
        inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);


        chartTitle = "Degree Distribution for 1979-1987, r>0.90";
        xAxisLabel = "Geographic Locations";
        yAxisLabel = "Degree";
        TableName = "ice9years1large";
        inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);

        chartTitle = "Degree Distribution for 1988-1996, r>0.95";
        xAxisLabel = "Geographic Locations";
        yAxisLabel = "Degree";
        TableName = "ice9years2";
        inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);

        chartTitle = "Degree Distribution for 1988-1996, r>0.90";
        xAxisLabel = "Geographic Locations";
        yAxisLabel = "Degree";
        TableName = "ice9years2large";
        inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);


        chartTitle = "Degree Distribution for 1997-2005, r>0.95";
        xAxisLabel = "Geographic Locations";
        yAxisLabel = "Degree";
        TableName = "ice9years3";
        inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);


        chartTitle = "Degree Distribution for 1997-2005, r>0.90";
        xAxisLabel = "Geographic Locations";
        yAxisLabel = "Degree";
        TableName = "ice9years3large";
        inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);

        chartTitle = "Degree Distribution for entire 1979-2005, r>0.95";
        xAxisLabel = "Geographic Locations";
        yAxisLabel = "Degree";
        TableName = "ice27years";
        //inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);


        chartTitle = "Degree Distribution for entire 1979-2005, r>0.90";
        xAxisLabel = "Geographic Locations";
        yAxisLabel = "Degree";
        TableName = "ice27years";
        //inputPlotParameters(chartTitle, xAxisLabel, yAxisLabel, TableName);



    }


}
