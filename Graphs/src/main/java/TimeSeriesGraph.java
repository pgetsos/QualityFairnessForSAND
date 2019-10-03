import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class TimeSeriesGraph extends JFrame {

	private static final long serialVersionUID = 1L;

	public TimeSeriesGraph(String title, List<Entry> entries) {
		super(title);
		XYDataset dataset = createDataset(entries);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Time Series Chart",
				"Segment",
				"Bitrate level",
				dataset);

		//Changes background color
		XYPlot plot = (XYPlot)chart.getPlot();
//		plot.setBackgroundPaint(new Color(255,228,196));

		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private XYDataset createDataset(List<Entry> entries) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		List<Integer> bitrates = List.of(50, 100, 150, 200, 250, 300, 400, 500, 600, 700, 900, 1200, 1500, 2000, 2500, 3000, 4000, 5000, 6000, 8000);

		for (Entry entry : entries) {
			TimeSeries series = new TimeSeries("Series1");
			int counter = 1;
			for (Integer integer : entry.getPlayingBitrate()) {
//				series.add(new Second(2), 50);
//				dataset.addValue((Number)bitrates.indexOf(integer/1000), "series1", counter++);
			}
		}
		TimeSeries series1 = new TimeSeries("Series1");
		series1.add(new Day(1, 1, 2017), 50);
		series1.add(new Day(2, 1, 2017), 40);
		series1.add(new Day(3, 1, 2017), 45);
		series1.add(new Day(4, 1, 2017), 30);
		series1.add(new Day(5, 1, 2017), 50);
		series1.add(new Day(6, 1, 2017), 45);
		series1.add(new Day(7, 1, 2017), 60);
		series1.add(new Day(8, 1, 2017), 45);
		series1.add(new Day(9, 1, 2017), 55);
		series1.add(new Day(10, 1, 2017), 48);
		series1.add(new Day(11, 1, 2017), 60);
		series1.add(new Day(12, 1, 2017), 45);
		series1.add(new Day(13, 1, 2017), 65);
		series1.add(new Day(14, 1, 2017), 45);
		series1.add(new Day(15, 1, 2017), 55);
		dataset.addSeries(series1);

		TimeSeries series2 = new TimeSeries("Series2");
		series2.add(new Day(1, 1, 2017), 40);
		series2.add(new Day(2, 1, 2017), 35);
		series2.add(new Day(3, 1, 2017), 26);
		series2.add(new Day(4, 1, 2017), 45);
		series2.add(new Day(5, 1, 2017), 40);
		series2.add(new Day(6, 1, 2017), 35);
		series2.add(new Day(7, 1, 2017), 45);
		series2.add(new Day(8, 1, 2017), 48);
		series2.add(new Day(9, 1, 2017), 31);
		series2.add(new Day(10, 1, 2017), 32);
		series2.add(new Day(11, 1, 2017), 21);
		series2.add(new Day(12, 1, 2017), 35);
		series2.add(new Day(13, 1, 2017), 10);
		series2.add(new Day(14, 1, 2017), 25);
		series2.add(new Day(15, 1, 2017), 15);
		dataset.addSeries(series2);


		return dataset;
	}
}