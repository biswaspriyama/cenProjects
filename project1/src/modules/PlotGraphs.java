package modules;

/**
 * Created by yugarsi on 9/23/15.
 */

import java.util.logging.Logger;


import static com.googlecode.charts4j.Color.*;
import static com.googlecode.charts4j.UrlUtil.normalize;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import beans.MysqlConnector;
import com.googlecode.charts4j.*;
import enums.Configurations;
import enums.MySqlConfig;
import org.junit.BeforeClass;
import org.junit.Test;

public class PlotGraphs {

//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//        Logger.global.setLevel(Level.ALL);
//    }

    @Test
    //Defining a very simple chart.
    public void example1() {
        // EXAMPLE CODE START
        //Plot plot = Plots.newPlot(Data.newData(50,50,50,50,50,50,50,50,50));
        double a[] ={1,2,3,4,5};
        Plot plot = Plots.newPlot(Data.newData(a));
        LineChart chart = GCharts.newLineChart(plot);
        String url = chart.toURLString();
        // EXAMPLE CODE END. Use this url string in your web or
        // Internet application.
        System.out.print(url);
//        String expectedString = "http://chart.apis.google.com/chart?chd=e:AAqnVU..&chs=200x125&cht=lc";
//        assertEquals("Junit error", normalize(expectedString), normalize(url));
    }
    public void plotHistogram(double[] array) {

        Plot plot = Plots.newPlot(Data.newData(array));
        LineChart chart = GCharts.newLineChart(plot);
        chart.setSize(400, 400);
        //chart.addHorizontalRangeMarker(33.3, 66.6, LIGHTBLUE);
        //chart.setGrid(33.3, 33.3, 3, 3);
        //chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels("Satellite Locations", 50.0));
        //chart.addYAxisLabels(AxisLabelsFactory.newNumericAxisLabels(0, 20,200));
        //chart.addYAxisLabels(AxisLabelsFactory.newAxisLabels("Degree", 50.0));
        String url = chart.toURLString();
        // EXAMPLE CODE END. Use this url string in your web or
        // Internet application.
        System.out.print(url);
    }

    public void plotDegreeDistribution(){
        MysqlConnector sqlObj= new MysqlConnector();
        //sqlObj.readStringRows(MySqlConfig.tableName);
        double[] edgeCount = sqlObj.edgeCount(MySqlConfig.tableName);
        plotHistogram(edgeCount);
    }


}
