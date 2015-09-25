package modules;

import beans.MysqlConnector;
import enums.MySqlConfig;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yugarsi on 9/25/15.
 */
public class PlotGraphs extends JFrame {
    String chartTitle = "";
    String xAxisLabel = "";
    String yAxisLabel = "";

    public PlotGraphs(float[] data, String chartTitle, String xAxisLabel, String yAxisLabel) {
        super("CEN Project Graph");
        this.chartTitle =chartTitle;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel =yAxisLabel;
        JPanel chartPanel = createChartPanel(data);
        add(chartPanel, BorderLayout.CENTER);
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createChartPanel(float[] data) {
        String chartTitle = "Degree Distribution Graph";
        String xAxisLabel = "Area Location";
        String yAxisLabel = "Degree";

        XYDataset dataset = createDataset(data);
        JFreeChart chart = ChartFactory.createXYLineChart(this.chartTitle,
                this.xAxisLabel, this.yAxisLabel, dataset);
        return new ChartPanel(chart);
    }

    private XYDataset createDataset(float[] data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Object 1");

        for(int i=0;i<data.length;i++){
            series1.add((float)i, data[i]);
        }
        dataset.addSeries(series1);
        return dataset;
    }

}