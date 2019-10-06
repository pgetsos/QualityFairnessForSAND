package gr.pgetsos.graphs;

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
	private static final String TIME_X_AXIS = "Time (in seconds)";
	private static final String BITRATE_Y_AXIS = "Bitrate";
	private static final String BUFFER_Y_AXIS = "Buffer Size";

	public void runAll() {
//		analyzeSingle("1.3");
//		analyzeSingle("6");
		analyzeSingle("1.3 10s", 60);

		analyzeMultiplePerAlgorithm("1.3 10s", 2, "sync", "basic", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 2, "sync", "netflix", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 2, "sync", "sara", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 2, "sync", "sandqoe", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 2, "sync", "sandbanddiv", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 3, "sync", "basic", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 3, "sync", "netflix", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 3, "sync", "sara", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 3, "sync", "sandqoe", 60);
		analyzeMultiplePerAlgorithm("1.3 10s", 3, "sync", "sandbanddiv", 60);
		analyzeTotalPerAlgorithm("1.3 10s", 2, "sync", 60);
		analyzeTotalPerAlgorithm("1.3 10s", 3, "sync", 60);
		//		analyzeMultiplePerAlgorithm("6", 3, "sync", "basic");
		//		analyzeMultiplePerAlgorithm("18", 3, "sync", "netflix");
		//		analyzeMultiplePerAlgorithm("1.3", 3, "sync", "sara");
		//		analyzeMultiplePerAlgorithm("6", 2, "sync", "sandqoe");
		//		analyzeMultiplePerAlgorithm("6", 3, "sync", "sandbanddiv");
		//		analyzeMultiplePSDN("12", 3, "stp", "netflix");
		//		analyzeMultiplePSDN("12", 3, "stp", "sara");
		//		analyzeTotalPerAlgorithm("1.3", 2, "sync");
		//		analyzeTotalPerAlgorithm("1.3", 3, "sync");
		//		analyzeTotalPerAlgorithm("6", 2, "sync");
//		analyzeTotalPerAlgorithm("6", 3, "sync");
	}

	private void analyzeSingle(String folder, int limit) {
		Entry basic = logReader.readEntry(folder, "1clientbasic_c1.log", "Basic");
		Entry netflix = logReader.readEntry(folder, "1clientnetflix_c1.log", "Buffer Based");
		Entry sara = logReader.readEntry(folder, "1clientsara_c1.log", "Sara");
		Entry sandbd = logReader.readEntry(folder, "1clientsandbanddiv_c1.log", "SAND - BW Division");
		Entry sandqoe = logReader.readEntry(folder, "1clientsandqoe_c1.log", "SAND - QoE Fairness");

		List<Entry> entries = List.of(basic, netflix, sara, sandbd, sandqoe);
		String segmentTitle = String.format("Bandwidth per segment - %sMbps Total Link Capacity - 1 client", folder);
		String bufferTitle = String.format("Buffer per segment - %sMbps Total Link Capacity - 1 client", folder);
		String qoeTitle = String.format("QoE per segment - %sMbps Total Link Capacity - 1 client", folder);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, limit, 10);
		LineGraph bufferPerSegment = chartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, limit, 10, 1);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, bufferPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	private void analyzeMultiplePerAlgorithm(String folder, int clients, String mode, String algorithm, int limit) {
		List<Entry> entries = entryCreator.getEntries(folder, clients, mode, algorithm);

		String segmentTitle = String.format("Bandwidth per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String bufferTitle = String.format("Buffer per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String qoeTitle = String.format("QoE per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, limit, 10);
		LineGraph bufferPerSegment = chartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, limit, 10, clients);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, bufferPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	private void analyzeMeanPerAlgorithm(String folder, int clients, String mode) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> entries = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEntries = entryCreator.getEntries(folder, clients, mode, algorithm);
			Entry meanEntry = entryCreator.getMeanEntry(tempEntries, folder);
			meanEntry.setName(algorithm);
			entries.add(meanEntry);
		}

		String segmentTitle = String.format("Bandwidth per segment\n%sMbps Total Link Capacity - %d clients", folder, clients);
		String qoeTitle = String.format("QoE per segment\n%sMbps Total Link Capacity - %d clients", folder, clients);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, 100, 10);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, 100, 10);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	private void analyzeTotalPerAlgorithm(String folder, int clients, String mode, int limit) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> entries = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEntries = entryCreator.getEntries(folder, clients, mode, algorithm);
			Entry meanEntry = entryCreator.getTotalEntry(tempEntries, folder);
			meanEntry.setName(algorithm);
			entries.add(meanEntry);
		}

		String segmentTitle = String.format("Total Bandwidth per segment\n%sMbps Total Link Capacity - %d clients", folder, clients);
		String qoeTitle = String.format("Total QoE per segment\n%sMbps Total Link Capacity - %d clients", folder, clients);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, limit, 10);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, limit, 10, clients);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	private LineGraph chartCreator(String chartType, List<Entry> entries, String title, String xAxis, String yAxis, int limit, int tick, int... clients) {
		LineGraph graph = new LineGraph(title, entries);
		graph.chart(chartType, xAxis, yAxis, limit, tick, clients);
		graph.setSize(1920, 1080);
		graph.setLocationRelativeTo(null);
		graph.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		graph.setVisible(VISIBLE);

		return graph;
	}

	private void saveChartS(String folder, LineGraph graph) {
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
}
