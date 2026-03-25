package bank_model.Experiments;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ChardDrawerMultiple extends JFrame {

    public ChardDrawerMultiple(String title,
                               ArrayList<ArrayList<Double>> allTime,
                               ArrayList<ArrayList<Double>> allValues) {

        super(title);

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int run = 0; run < allTime.size(); run++) {
            XYSeries series = new XYSeries("Run " + (run + 1));

            List<Double> time = allTime.get(run);
            List<Double> values = allValues.get(run);

            for (int i = 0; i < time.size(); i++) {
                series.add(time.get(i), values.get(i));
            }

            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Mean Queue vs Time",
                "Time",
                "Mean Queue",
                dataset
        );

        setContentPane(new ChartPanel(chart));
    }
}
