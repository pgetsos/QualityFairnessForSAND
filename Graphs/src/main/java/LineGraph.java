import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

public class LineGraph extends JFrame {

	private static final long serialVersionUID = 1L;
	private String title;
	private List<Entry> entries;

	public LineGraph(String title, List<Entry> entries) {
		super(title);
		this.title = title;
		this.entries = entries;
	}

	public void chart(String mode, String xAxisLabel, String yAxisLabel, int limit, int tick){
		switch (mode) {
			case "Segments":
				chartForSegments(xAxisLabel, yAxisLabel, limit, tick);
				break;
			case "Buffer":
				chartForBuffer(xAxisLabel, yAxisLabel, limit, tick);
				break;
			case "QoE":
				chartForSegmentsQoE(xAxisLabel, yAxisLabel, limit, tick);
				break;
		}
	}

	public void chartForSegments(String xAxisLabel, String yAxisLabel) {
		chartForSegments(xAxisLabel, yAxisLabel, 100, 10);
	}

	public void chartForSegments(String xAxisLabel, String yAxisLabel, int limit) {
		chartForSegments(xAxisLabel, yAxisLabel, limit, 10);
	}

	public void chartForSegments(String xAxisLabel, String yAxisLabel, int limit, int tick) {
		XYDataset  dataset = createDatasetBitrates(entries);
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,
				xAxisLabel,
				yAxisLabel,
				dataset);

		XYPlot plot = (XYPlot) chart.getPlot();
		//plot.setBackgroundPaint(new Color(255,228,196));
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setDefaultShapesVisible(true);
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setRange(0, limit);
		xAxis.setTickUnit(new NumberTickUnit(tick));
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	public void chartForBuffer(String xAxisLabel, String yAxisLabel, int limit, int tick) {
		XYDataset  dataset = createDatasetBuffer(entries);
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,
				xAxisLabel,
				yAxisLabel,
				dataset);

		XYPlot plot = (XYPlot) chart.getPlot();
		//plot.setBackgroundPaint(new Color(255,228,196));
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setDefaultShapesVisible(true);
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setRange(0, limit);
		xAxis.setTickUnit(new NumberTickUnit(tick));
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	public void chartForSegmentsQoE(String xAxisLabel, String yAxisLabel, int limit, int tick) {
		XYDataset  dataset = createDatasetQoE(entries);
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,
				xAxisLabel,
				yAxisLabel,
				dataset);

		XYPlot plot = (XYPlot) chart.getPlot();
		//plot.setBackgroundPaint(new Color(255,228,196));
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setDefaultShapesVisible(true);
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		xAxis.setRange(0, limit);
		yAxis.setRange(0.75, 1);
		xAxis.setTickUnit(new NumberTickUnit(tick));
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private XYDataset createDatasetBitrates(List<Entry> entries) {
		XYSeriesCollection   dataset = new XYSeriesCollection();

		List<Integer> bitrates = List.of(50, 100, 150, 200, 250, 300, 400, 500, 600, 700, 900, 1200, 1500, 2000, 2500, 3000, 4000, 5000, 6000, 8000); //To be used for Levels

		for (Entry entry : entries) {
			XYSeries series = new XYSeries(entry.getName());
			int counter = 1;
			for (Integer integer : entry.getPlayingBitrate()) {
				Number t1 = integer/1000;
				int t2 = counter++;
				series.add(t2, t1);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	private XYDataset createDatasetBuffer(List<Entry> entries) {
		XYSeriesCollection   dataset = new XYSeriesCollection();

		for (Entry entry : entries) {
			XYSeries series = new XYSeries(entry.getName());
			int counter = 1;
			for (Map.Entry<Integer, Integer> entryBuffer : entry.getBufferPerSecond().entrySet()) {
				series.add(entryBuffer.getKey(), entryBuffer.getValue());
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	private XYDataset createDatasetQoE(List<Entry> entries) {
		XYSeriesCollection   dataset = new XYSeriesCollection();

		List<Integer> bitrates = List.of(50, 100, 150, 200, 250, 300, 400, 500, 600, 700, 900, 1200, 1500, 2000, 2500, 3000, 4000, 5000, 6000, 8000);
		Map<Integer, Double> qoe = Map.ofEntries(Map.entry(50000, 0.77), Map.entry(100000, 0.89), Map.entry(150000, 0.93), Map.entry(200000, 0.92), Map.entry(250000, 0.94), Map.entry(300000, 0.95), Map.entry(400000, 0.96), Map.entry(500000, 0.97), Map.entry(600000, 0.96), Map.entry(700000, 0.96), Map.entry(900000, 0.95), Map.entry(1200000, 0.96), Map.entry(1500000, 0.97), Map.entry(2000000, 0.98), Map.entry(2500000, 0.96), Map.entry(3000000, 0.97), Map.entry(4000000, 0.98), Map.entry(5000000, 0.98), Map.entry(6000000, 0.98), Map.entry(8000000, 0.99));
		for (Entry entry : entries) {
			XYSeries series = new XYSeries(entry.getName());
			int counter = 1;
			for (Integer integer : entry.getPlayingBitrate()) {
				double t1 = qoe.get(integer);
				int t2 = counter++;
				series.add(t2, t1);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}
}