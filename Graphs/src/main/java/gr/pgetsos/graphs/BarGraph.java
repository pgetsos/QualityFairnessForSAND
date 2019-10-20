package gr.pgetsos.graphs;

import gr.pgetsos.graphs.Helpers.GraphImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BarGraph extends JFrame implements GraphImpl {

	private static final long serialVersionUID = 1L;
	private String title;
	private List<Entry> entries;
	private List<Entry> entriesAlt;
	private JFreeChart chart;

	public BarGraph(String title, List<Entry> entries) {
		super(title);
		this.title = title;
		this.entries = entries;
	}

	public BarGraph(String title, List<Entry> entries, List<Entry> entriesAlt) {
		super(title);
		this.title = title;
		this.entries = entries;
		this.entriesAlt = entriesAlt;
	}

	public void chart(String mode, String xAxisLabel, String yAxisLabel, int tick){
		switch (mode) {
			case "QoE":
				chartForAdjustedQoE(xAxisLabel, yAxisLabel, tick);
				break;
			case "Fair":
				chartForFairness(xAxisLabel, yAxisLabel, tick);
				break;
		}
	}


	public void chartForAdjustedQoE(String xAxisLabel, String yAxisLabel, int tick) {
		CategoryDataset dataset = createDatasetQoE();
		chart = ChartFactory.createBarChart(
				title,
				"",
				yAxisLabel,
				dataset);

		CategoryPlot plot = chartProperties();
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setRange(0.74, 1);
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	public void chartForFairness(String xAxisLabel, String yAxisLabel, int tick) {
		CategoryDataset dataset = createDatasetFair();
		chart = ChartFactory.createBarChart(
				title,
				xAxisLabel,
				yAxisLabel,
				dataset);

		CategoryPlot plot = chartProperties();
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setRange(0.74, 1);
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private CategoryDataset createDatasetQoE() {
		DefaultCategoryDataset   dataset = new DefaultCategoryDataset();
		for (Entry entry : entries) {
			dataset.addValue(entry.getMeanQoE(), entry.getName(), "2s segments\nVideo-based QoE");
			dataset.addValue(entry.getAdjustedQoE(), entry.getName(), "2s segments\nAdjusted QoE");
		}
		for (Entry entry : entriesAlt) {
			dataset.addValue(entry.getMeanQoE(), entry.getName(), "10s segments\nVideo-based QoE");
			dataset.addValue(entry.getAdjustedQoE(), entry.getName(), "10s segments\nAdjusted QoE");
		}
		return dataset;
	}

	private CategoryDataset createDatasetFair() {
		DefaultCategoryDataset   dataset = new DefaultCategoryDataset();
		for (Entry entry : entries) {
			dataset.addValue(entry.getMeanFairness(), entry.getName(), "2s segments");
		}
		for (Entry entry : entriesAlt) {
			dataset.addValue(entry.getMeanFairness(), entry.getName(), "10s segments");
		}
		return dataset;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	private CategoryPlot chartProperties () {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
//		((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
		BarRenderer.setDefaultBarPainter(new StandardBarPainter());
		((BarRenderer) plot.getRenderer()).setBarPainter(new GradientBarPainter());
//		plot.setBackgroundPaint(new Color(205,205,218));
		plot.getRenderer().setSeriesPaint(0, Color.red);
		plot.getRenderer().setSeriesPaint(1, Color.BLUE);
		plot.getRenderer().setSeriesPaint(2, new Color(252,250,14));
		plot.getRenderer().setSeriesPaint(3, Color.black);
		plot.getRenderer().setSeriesPaint(4, Color.MAGENTA);

		LegendTitle standardlegend =  ((JFreeChart) chart).getLegend();
		CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();
		ValueAxis yAxis = (ValueAxis) plot.getRangeAxis();
		xAxis.setMaximumCategoryLabelLines(2);
		plot.getChart().getTitle().setFont(new Font("Arial", Font.PLAIN, 40));
		((AbstractRenderer)plot.getRenderer()).setDefaultLegendShape(new Rectangle(20,20));
		standardlegend.setItemFont(new Font("Arial", Font.PLAIN, 32));
		xAxis.setLabelFont(new Font("Arial", Font.PLAIN, 35));
		yAxis.setLabelFont(new Font("Arial", Font.PLAIN, 35));
		xAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 28));
		yAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 25));
		return plot;
	}
}