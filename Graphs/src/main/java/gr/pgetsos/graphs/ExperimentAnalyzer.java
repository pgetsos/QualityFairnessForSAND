package gr.pgetsos.graphs;

import gr.pgetsos.graphs.Helpers.GraphImpl;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.WindowConstants;

import gr.pgetsos.graphs.Helpers.EntryCreator;
import gr.pgetsos.graphs.Helpers.LogReader;

public class ExperimentAnalyzer {
	private LogReader logReader = new LogReader();
	private EntryCreator entryCreator = new EntryCreator();

	private static final Boolean VISIBLE = false;
	private static final String SEGMENT = "Segments";
	private static final String BUFFER = "Buffer";
	private static final String QOE = "QoE";
	private static final String SEGMENT_SHORT_X_AXIS = "Segment (2s per segment)";
	private static final String SEGMENT_LONG_X_AXIS = "Segment (10s per segment)";
	private static final String ADJUSTED_X_AXIS = "Segment size - QoE measurement";
	private static final String TIME_X_AXIS = "Time (in seconds)";
	private static final String BITRATE_Y_AXIS = "Bitrate";
	private static final String BUFFER_Y_AXIS = "Buffer Size";

	void analyzeSingle(String folder, int limit, String mbps) {
		Entry basic = logReader.readEntry(folder, "1clientbasic_c1.log", "Basic");
		Entry netflix = logReader.readEntry(folder, "1clientnetflix_c1.log", "Buffer Based");
		Entry sara = logReader.readEntry(folder, "1clientsara_c1.log", "Sara");
		Entry sandbd = logReader.readEntry(folder, "1clientsandbanddiv_c1.log", "SAND - BW Division");
		Entry sandqoe = logReader.readEntry(folder, "1clientsandqoe_c1.log", "SAND - QoE Fairness");

		List<Entry> entries = List.of(basic, netflix, sara, sandbd, sandqoe);
		String segmentTitle = String.format("Bandwidth per segment - %sMbps Total Link Capacity - 1 client", mbps);
		String bufferTitle = String.format("Buffer per segment - %sMbps Total Link Capacity - 1 client", mbps);
		String qoeTitle = String.format("QoE per segment - %sMbps Total Link Capacity - 1 client", mbps);

		String segmentXAxis = folder.contains("10s") ? SEGMENT_LONG_X_AXIS : SEGMENT_SHORT_X_AXIS;

		LineGraph bwPerSegment = lineChartCreator(SEGMENT, entries, segmentTitle, segmentXAxis, BITRATE_Y_AXIS, limit, 10);
		LineGraph bufferPerSegment = lineChartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = lineChartCreator(QOE, entries, qoeTitle, segmentXAxis, QOE, limit, 10, 1);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, bufferPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	void analyzeMultiplePerAlgorithm(String folder, int clients, String mode, String algorithm, int limit, String mbps) {
		List<Entry> entries = entryCreator.getEntries(folder, clients, mode, algorithm);

		String segmentTitle = String.format("Bandwidth per segment%n%sMbps Total Link Capacity - %d clients - %s", mbps, clients, algorithm);
		String bufferTitle = String.format("Buffer per segment%n%sMbps Total Link Capacity - %d clients - %s", mbps, clients, algorithm);
		String qoeTitle = String.format("QoE per segment%n%sMbps Total Link Capacity - %d clients - %s", mbps, clients, algorithm);

		String segmentXAxis = folder.contains("10s") ? SEGMENT_LONG_X_AXIS : SEGMENT_SHORT_X_AXIS;

		LineGraph bwPerSegment = lineChartCreator(SEGMENT, entries, segmentTitle, segmentXAxis, BITRATE_Y_AXIS, limit, 10);
		LineGraph bufferPerSegment = lineChartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = lineChartCreator(QOE, entries, qoeTitle, segmentXAxis, QOE, limit, 10, 1);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, bufferPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	void analyzeTotals(String folder, int clients, String mode, int limit, String mbps) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> totalEntries = new ArrayList<>(5);
		List<Entry> meanEntries = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEntries = entryCreator.getEntries(folder, clients, mode, algorithm);
			Entry totalEntry = entryCreator.getTotalEntry(tempEntries, folder);
			Entry meanEntry = entryCreator.getMeanEntry(tempEntries, folder);
			totalEntry.setName(algorithm);
			meanEntry.setName(algorithm);
			totalEntries.add(totalEntry);
			meanEntries.add(meanEntry);
		}

		String segmentTitle = String.format("Total Bandwidth per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);
		String qoeTitle = String.format("Total QoE per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);
		String meanSegmentTitle = String.format("Average Bandwidth per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);
		String meanQoeTitle = String.format("Average QoE per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);

		String segmentXAxis = folder.contains("10s") ? SEGMENT_LONG_X_AXIS : SEGMENT_SHORT_X_AXIS;

		LineGraph bwPerSegment = lineChartCreator(SEGMENT, totalEntries, segmentTitle, segmentXAxis, BITRATE_Y_AXIS, limit, 10);
		LineGraph qoePerSegment = lineChartCreator(QOE, totalEntries, qoeTitle, segmentXAxis, QOE, limit, 10, clients);
		LineGraph meanBwPerSegment = lineChartCreator(SEGMENT, meanEntries, meanSegmentTitle, segmentXAxis, BITRATE_Y_AXIS, limit, 10);
		LineGraph meanQoePerSegment = lineChartCreator(QOE, meanEntries, meanQoeTitle, segmentXAxis, QOE, limit, 10, clients);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, qoePerSegment);
		saveChartS(folder, meanBwPerSegment);
		saveChartS(folder, meanQoePerSegment);
	}

	void analyzeAdjustedQoE(String folder, String folderAlt, int clients, String mode, String mbps) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> entries = new ArrayList<>(5);
		List<Entry> entriesAlt = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEnties = entryCreator.getEntries(folder, clients, mode, algorithm);
			List<Entry> tempEntriesAlt = entryCreator.getEntries(folderAlt, clients, mode, algorithm);
			getAdjustedQoE(tempEnties);
			getAdjustedQoE(tempEntriesAlt);
			Entry meanEntry = entryCreator.getMeanEntry(tempEnties, folder);
			Entry meanEntryAlt = entryCreator.getMeanEntry(tempEntriesAlt, folderAlt);
			meanEntry.setName(algorithm);
			meanEntryAlt.setName(algorithm);
			entries.add(meanEntry);
			entriesAlt.add(meanEntryAlt);
		}

		String title = String.format("Average QoE%n%sMbps Total Link Capacity - %d clients", mbps, clients);

		BarGraph graph = barChartCreator(QOE, entries, entriesAlt, title, ADJUSTED_X_AXIS, QOE, 10);
		saveChartS(folder+folderAlt, graph);
	}

	private LineGraph lineChartCreator(String chartType, List<Entry> entries, String title, String xAxis, String yAxis, int limit, int tick, int... clients) {
		LineGraph graph = new LineGraph(title, entries);
		graph.chart(chartType, xAxis, yAxis, limit, tick, clients);
		graph.setSize(1920, 1080);
		graph.setLocationRelativeTo(null);
		graph.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		graph.setVisible(VISIBLE);

		return graph;
	}

	private BarGraph barChartCreator(String chartType, List<Entry> entries, List<Entry> entriesAlt, String title, String xAxis, String yAxis, int tick) {
		BarGraph graph = new BarGraph(title, entries, entriesAlt);
		graph.chart(chartType, xAxis, yAxis, tick);
		graph.setSize(1920, 1080);
		graph.setLocationRelativeTo(null);
		graph.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		graph.setVisible(VISIBLE);

		return graph;
	}

	private void saveChartS(String folder, GraphImpl graph) {
		Path resourceDirectoryPath = Paths.get("charts");
		File resourceDirectory = new File(resourceDirectoryPath.toUri());
		resourceDirectory.mkdir();
		try {
			File chartFolder = new File(resourceDirectory.getAbsoluteFile() + "\\" + folder + "_charts");
			chartFolder.mkdirs();
			File chart1 = new File(chartFolder.getAbsolutePath() + "\\" + removeLineFeeds(graph.getTitle()) + ".png");
			System.out.println(chart1.getAbsolutePath());

			ChartUtils.saveChartAsPNG(chart1, graph.getChart(), 1920, 1080 );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String removeLineFeeds (String line) {
		return line.replace("\n", "").replace("\r", "");
	}

	private void getAdjustedQoE(List<Entry> entries) {
		for (Entry entry : entries) {
			getAdjustedQoE(entry);
		}
	}

	private void getAdjustedQoE(Entry entry) {
		double tempQoE = 0;
		double prevQoE = 0;
		double convergencePoint = 0.954;
		int counter = 0;
		int convergenceTime = 0;
		for (Double qoe : entry.getQoeMetrics()) {
			if (qoe < prevQoE) {
				tempQoE += qoe - (prevQoE - qoe) * 2;
			} else {
				tempQoE += qoe;
			}

			if (qoe > convergencePoint && convergenceTime == 0) {
				convergenceTime = counter;
			}
			prevQoE = qoe;
			counter++;
		}

		int segments = entry.getQoeMetrics().size();

		double negativeQoE = (Math.max(entry.getNumberOfShortInterruptions() - 1, 0)) * 0.05 + entry.getNumberOfLongInterruptions() * 0.2; // One small interruption gets ignored usually
		negativeQoE = negativeQoE > 1 ? 1 : negativeQoE;

		double finalQoE = 0.7 * (tempQoE/segments) + 0.15 * (1 - (double)convergenceTime/segments) + 0.15 * (1 - negativeQoE);
		entry.setAdjustedQoE(finalQoE);
	}

	private double getHossIndex(double... qoe) {
		DescriptiveStatistics data = new DescriptiveStatistics();
		for (double v : qoe) {
			data.addValue(v);
		}
		//double std = data.getStandardDeviation(); // This is for sample std - wrong results
		double std = Math.sqrt(data.getPopulationVariance());
		double h = 0.978;
		double l = 0.742;

		return 0.5 * (1 - ((2*std)/(h-l))) + 0.5 * (data.getSum()/(h*data.getN()));
	}
}
