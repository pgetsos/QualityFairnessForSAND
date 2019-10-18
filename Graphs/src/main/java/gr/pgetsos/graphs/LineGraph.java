package gr.pgetsos.graphs;

import gr.pgetsos.graphs.Helpers.GraphImpl;
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

import gr.pgetsos.graphs.Entry;

public class LineGraph extends JFrame implements GraphImpl {

	private static final long serialVersionUID = 1L;
	private String title;
	private List<Entry> entries;
	private JFreeChart chart;

	public LineGraph(String title, List<Entry> entries) {
		super(title);
		this.title = title;
		this.entries = entries;
	}

	public void chart(String mode, String xAxisLabel, String yAxisLabel, int limit, int tick, int... clients){
		switch (mode) {
			case "Segments":
				chartForSegments(xAxisLabel, yAxisLabel, limit, tick);
				break;
			case "Buffer":
				chartForBuffer(xAxisLabel, yAxisLabel, limit, tick);
				break;
			case "QoE":
				chartForSegmentsQoE(xAxisLabel, yAxisLabel, limit, tick, clients[0]);
				break;
		}
	}

	public void chartForSegments(String xAxisLabel, String yAxisLabel, int limit, int tick) {
		XYDataset  dataset = createDatasetBitrates(entries);
		chart = ChartFactory.createXYLineChart(
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
		chart = ChartFactory.createXYLineChart(
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
		xAxis.setTickUnit(new NumberTickUnit(tick));
		yAxis.setTickUnit(new NumberTickUnit(1));
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	public void chartForSegmentsQoE(String xAxisLabel, String yAxisLabel, int limit, int tick, int clients) {
		XYDataset  dataset = createDatasetQoE(entries);
		chart = ChartFactory.createXYLineChart(
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
		yAxis.setRange(0.74*clients, clients);
		xAxis.setTickUnit(new NumberTickUnit(tick));
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private XYDataset createDatasetBitrates(List<Entry> entries) {
		XYSeriesCollection   dataset = new XYSeriesCollection();
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
		for (Entry entry : entries) {
			XYSeries series = new XYSeries(entry.getName());
			int counter = 1;
			for (Double qoe : entry.getQoeMetrics()) {
				int count = counter++;
				series.add(count, qoe);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}
}