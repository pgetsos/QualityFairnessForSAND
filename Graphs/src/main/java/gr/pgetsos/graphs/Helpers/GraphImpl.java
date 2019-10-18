package gr.pgetsos.graphs.Helpers;

import org.jfree.chart.JFreeChart;

public interface GraphImpl {
	public JFreeChart getChart();

	public void setChart(JFreeChart chart);

	public String getTitle();
}
