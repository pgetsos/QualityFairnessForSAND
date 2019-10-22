package gr.pgetsos.graphs;

import gr.pgetsos.graphs.Helpers.EntryCreator;
import gr.pgetsos.graphs.Helpers.GraphImpl;
import gr.pgetsos.graphs.Helpers.Helpers;
import gr.pgetsos.graphs.Helpers.LogReader;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExperimentAnalyzerAsync {
	private LogReader logReader = new LogReader();
	private EntryCreator entryCreator = new EntryCreator();

	private static final Boolean VISIBLE = false;
	private static final String SEGMENT = "Segments";
	private static final String BUFFER = "Buffer";
	private static final String QOE = "QoE";
	private static final String FAIRNESS = "Fair Index";
	private static final String SEGMENT_SHORT_X_AXIS = "Segment (2s per segment)";
	private static final String SEGMENT_LONG_X_AXIS = "Segment (10s per segment)";
	private static final String ADJUSTED_X_AXIS = "Segment size - QoE measurement";
	private static final String SEGMENT_X_AXIS = "Segment size";
	private static final String TIME_X_AXIS = "Time (in seconds)";
	private static final String BITRATE_Y_AXIS = "Bitrate";
	private static final String BUFFER_Y_AXIS = "Buffer Size";

	void analyzeMultiplePerAlgorithm(String folder, int clients, String algorithm, int limit, String mbps) {
		List<Entry> entries = entryCreator.getEntries(folder, clients, "async", algorithm);

		String segmentTitle = String.format("Bitrate per segment%n%sMbps Total Link Capacity - %d clients - %s", mbps, clients, Helpers.getAlgorithmName(algorithm));
		String bufferTitle = String.format("Buffer per segment%n%sMbps Total Link Capacity - %d clients - %s", mbps, clients, Helpers.getAlgorithmName(algorithm));
		String qoeTitle = String.format("QoE per segment%n%sMbps Total Link Capacity - %d clients - %s", mbps, clients, Helpers.getAlgorithmName(algorithm));

		String segmentXAxis = SEGMENT_SHORT_X_AXIS;
		int segmentOffset = 30; // Segment offset for Async tests. Change to your offset
		if (folder.contains("10s")) {
			segmentXAxis = SEGMENT_LONG_X_AXIS;
			segmentOffset = 6;
		}

		LineGraph bwPerSegment = lineChartCreator(SEGMENT, entries, segmentTitle, segmentXAxis, BITRATE_Y_AXIS, limit, 10, segmentOffset, clients);
		LineGraph bufferPerSegment = lineChartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20, segmentOffset, clients);
		LineGraph qoePerSegment = lineChartCreator(QOE, entries, qoeTitle, segmentXAxis, QOE, limit, 10, segmentOffset, clients);

		saveChartS(folder, bwPerSegment);
//		saveChartS(folder, bufferPerSegment);
//		saveChartS(folder, qoePerSegment);
	}

	void analyzeTotals(String folder, int clients, String mode, int limit, String mbps) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> totalEntries = new ArrayList<>(5);
		List<Entry> meanEntries = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEntries = entryCreator.getEntries(folder, clients, mode, algorithm);
			Entry totalEntry = entryCreator.getTotalEntry(tempEntries, folder);
			Entry meanEntry = entryCreator.getMeanEntry(tempEntries, folder);
			totalEntry.setName(Helpers.getAlgorithmName(algorithm));
			meanEntry.setName(Helpers.getAlgorithmName(algorithm));
			totalEntries.add(totalEntry);
			meanEntries.add(meanEntry);
		}

		String segmentTitle = String.format("Total Bitrate per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);
		String qoeTitle = String.format("Total QoE per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);
		String meanSegmentTitle = String.format("Average Bitrate per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);
		String meanQoeTitle = String.format("Average QoE per segment%n%sMbps Total Link Capacity - %d clients", mbps, clients);

		String segmentXAxis = SEGMENT_SHORT_X_AXIS;
		int segmentOffset = 30; // Segment offset for Async tests. Change to your offset
		if (folder.contains("10s")) {
			segmentXAxis = SEGMENT_LONG_X_AXIS;
			segmentOffset = 6;
		}

		LineGraph bwPerSegment = lineChartCreator(SEGMENT, totalEntries, segmentTitle, segmentXAxis, BITRATE_Y_AXIS, limit, 10, segmentOffset, clients);
		LineGraph qoePerSegment = lineChartCreator(QOE, totalEntries, qoeTitle, segmentXAxis, QOE, limit, 10, segmentOffset, clients);
		LineGraph meanBwPerSegment = lineChartCreator(SEGMENT, meanEntries, meanSegmentTitle, segmentXAxis, BITRATE_Y_AXIS, limit, 10, segmentOffset, clients);
		LineGraph meanQoePerSegment = lineChartCreator(QOE, meanEntries, meanQoeTitle, segmentXAxis, QOE, limit, 10, segmentOffset, clients);

		saveChartS(folder, bwPerSegment);
//		saveChartS(folder, qoePerSegment);
//		saveChartS(folder, meanBwPerSegment);
//		saveChartS(folder, meanQoePerSegment);
	}

	void analyzeAdjustedQoE(String folder, String folderAlt, int clients, String mode, String mbps) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> entries = new ArrayList<>(5);
		List<Entry> entriesAlt = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEntries = entryCreator.getEntries(folder, clients, mode, algorithm);
			List<Entry> tempEntriesAlt = entryCreator.getEntries(folderAlt, clients, mode, algorithm);
			getAdjustedQoE(tempEntries);
			getAdjustedQoE(tempEntriesAlt);
			Entry meanEntry = entryCreator.getMeanEntry(tempEntries, folder);
			Entry meanEntryAlt = entryCreator.getMeanEntry(tempEntriesAlt, folderAlt);
			meanEntry.setName(Helpers.getAlgorithmName(algorithm));
			meanEntryAlt.setName(Helpers.getAlgorithmName(algorithm));
			entries.add(meanEntry);
			entriesAlt.add(meanEntryAlt);
		}

		String title = String.format("Average QoE%n%sMbps Total Link Capacity - %d clients", mbps, clients);
		String titleFair = String.format("Average Fairness%n%sMbps Total Link Capacity - %d clients", mbps, clients);

		BarGraph graph = barChartCreator(QOE, entries, entriesAlt, title, ADJUSTED_X_AXIS, "QoE", 10);
		BarGraph graph2 = barChartCreator("Fair", entries, entriesAlt, titleFair, SEGMENT_X_AXIS, FAIRNESS, 10);
		saveChartS(folder+folderAlt, graph);
		saveChartS(folder+folderAlt, graph2);
	}

	private LineGraph lineChartCreator(String chartType, List<Entry> entries, String title, String xAxis, String yAxis, int limit, int tick, int segmentOffset, int clients) {
		LineGraph graph = new LineGraph(title, entries);
		graph.chartForAsync(chartType, xAxis, yAxis, limit, tick, segmentOffset, clients);
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
			File chartFolder = new File(resourceDirectory.getAbsoluteFile() + "\\" + "async_" + folder + "_charts");
			chartFolder.mkdirs();
			File chart1 = new File(chartFolder.getAbsolutePath() + "\\" + removeLineFeeds(graph.getTitle()).replaceAll("\\.", "").replaceAll(" ", "-") + ".png");
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
				tempQoE += qoe - (prevQoE - qoe) * 5;
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

		double negativeQoE = (Math.max(entry.getNumberOfShortInterruptions() - 1, 0)) * 0.05 + entry.getNumberOfLongInterruptions() * 0.2; //One small interruption gets ignored usually
		negativeQoE = negativeQoE > 2 ? 2 : negativeQoE;

		double finalQoE = 0.7 * (tempQoE/segments) + 0.15 * (1 - (double)convergenceTime/segments) + 0.15 * (1 - negativeQoE);
		entry.setAdjustedQoE(finalQoE);
	}
}
