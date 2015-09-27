package modules;

import beans.MysqlConnector;
import enums.MySqlConfig;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PlotGraphs extends JFrame {
    String chartTitle = "";
    String xAxisLabel = "";
    String yAxisLabel = "";

    public PlotGraphs(float[] data1,float[] data2, String chartTitle, String xAxisLabel, String yAxisLabel) {
        super("CEN Project Graph");
        this.chartTitle =chartTitle;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel =yAxisLabel;
        JPanel chartPanel = createChartPanel(data1, data2);
        add(chartPanel, BorderLayout.CENTER);
        setSize(900, 900);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createChartPanel(float[] data1, float[] data2 ) {
        //String chartTitle = "Degree Distribution Graph";
        //String xAxisLabel = "Area Location";
        //String yAxisLabel = "Degree";

        XYDataset dataset = createDataset(data1, data2);
        JFreeChart chart = ChartFactory.createXYLineChart(this.chartTitle,
                this.xAxisLabel, this.yAxisLabel, dataset);
        return new ChartPanel(chart);
    }

    private XYDataset createDataset(float[] data1, float[] data2) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Nodes");
        XYSeries series2 = new XYSeries("SuperNodes");

        for(int i=0;i<data1.length;i++){
            series1.add((float)i, data1[i]);
        }
        for(int i=0;i<data2.length;i++){
            series2.add((float)i, data2[i]);
        }
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;
    }

}