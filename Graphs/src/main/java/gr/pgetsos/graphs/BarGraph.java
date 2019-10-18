package gr.pgetsos.graphs;

import gr.pgetsos.graphs.Helpers.GraphImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
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
		}
	}


	public void chartForAdjustedQoE(String xAxisLabel, String yAxisLabel, int tick) {
		CategoryDataset dataset = createDatasetQoE(entries);
		chart = ChartFactory.createBarChart(
				title,
				xAxisLabel,
				yAxisLabel,
				dataset);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		//plot.setBackgroundPaint(new Color(255,228,196));

		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setRange(0.74, 1);
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private CategoryDataset createDatasetQoE(List<Entry> entries) {
		DefaultCategoryDataset   dataset = new DefaultCategoryDataset();
		for (Entry entry : entries) {
			dataset.addValue(entry.getMeanQoE(), entry.getName(), "2s segments - Video-based QoE");
			dataset.addValue(entry.getAdjustedQoE(), entry.getName(), "2s segments - Adjusted QoE");
		}
		for (Entry entry : entriesAlt) {
			dataset.addValue(entry.getMeanQoE(), entry.getName(), "10s segments - Video-based QoE");
			dataset.addValue(entry.getAdjustedQoE(), entry.getName(), "10s segments - Adjusted QoE");
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