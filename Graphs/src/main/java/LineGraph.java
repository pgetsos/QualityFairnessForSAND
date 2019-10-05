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

	public void chartForSegments(String xAxisLabel, String yAxisLabel) {
		chartForSegments(xAxisLabel, yAxisLabel, 100, 10);
	}

	public void chartForSegments(String xAxisLabel, String yAxisLabel, int limit) {
		chartForSegments(xAxisLabel, yAxisLabel, limit, 10);
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
		Map<Integer, Double> qoe10s = Map.ofEntries(Map.entry(45652000, 0.742), Map.entry(89283000, 0.876), Map.entry(131087000, 0.916), Map.entry(178351000, 0.914), Map.entry(221600000, 0.930), Map.entry(262537000, 0.940), Map.entry(334349000, 0.951), Map.entry(396126000, 0.958), Map.entry(522286000, 0.954), Map.entry(595491000, 0.959), Map.entry(791182000, 0.946), Map.entry(1032682000, 0.957), Map.entry(1244778000, 0.963), Map.entry(1546902000, 0.969), Map.entry(2133691000, 0.959), Map.entry(-1810832296, 0.964), Map.entry(-1216380296, 0.970), Map.entry(-768045296, 0.973), Map.entry(-454607296, 0.975), Map.entry(-75070296, 0.978));

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