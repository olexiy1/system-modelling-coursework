package bank_model.Experiments;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.JFrame;

public class ChartDrawer extends JFrame {

    public ChartDrawer(String title,
                       java.util.List<Double> time,
                       java.util.List<Double> values) {

        super(title);

        XYSeries series = new XYSeries("Mean Queue Length");

        for (int i = 0; i < time.size(); i++) {
            series.add(time.get(i), values.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Mean Queue vs Time",
                "Time (tcurr)",
                "Mean Queue Length",
                dataset
        );

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }
}
