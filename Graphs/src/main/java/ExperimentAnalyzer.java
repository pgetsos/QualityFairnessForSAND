import org.jfree.chart.ChartUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.WindowConstants;

public class ExperimentAnalyzer {
	private LogReader logReader = new LogReader();
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
//		analyzeSingle("18");

//		analyzeMultiplePerAlgorithm("18", 2, "sync", "basic");
//		analyzeMultiplePerAlgorithm("6", 3, "sync", "basic");
//		analyzeMultiplePerAlgorithm("18", 2, "sync", "netflix");
//		analyzeMultiplePerAlgorithm("18", 3, "sync", "netflix");
//		analyzeMultiplePerAlgorithm("18", 2, "sync", "sara");
//		analyzeMultiplePerAlgorithm("1.3", 3, "sync", "sara");
//		analyzeMultiplePerAlgorithm("6", 2, "sync", "sandqoe");
//				analyzeMultiplePerAlgorithm("1.3", 3, "sync", "sandqoe");
		//		analyzeMultiplePerAlgorithm("6", 2, "sync", "sandbanddiv");
		//		analyzeMultiplePerAlgorithm("6", 3, "sync", "sandbanddiv");
		//		analyzeMultiplePSDN("12", 3, "stp", "netflix");
		//		analyzeMultiplePSDN("12", 3, "stp", "sara");
		analyzeTotalPerAlgorithm("1.3", 2, "sync");
		analyzeTotalPerAlgorithm("1.3", 3, "sync");
		analyzeTotalPerAlgorithm("6", 2, "sync");
		analyzeTotalPerAlgorithm("6", 3, "sync");
	}

	private void analyzeSingle(String folder) {
		Entry basic = logReader.readEntry(folder + "/1clientbasic_c1.log", "Basic");
		Entry netflix = logReader.readEntry(folder + "/1clientnetflix_c1.log", "Buffer Based");
		Entry sara = logReader.readEntry(folder + "/1clientsara_c1.log", "Sara");
		Entry sandbd = logReader.readEntry(folder + "/1clientsandbanddiv_c1.log", "SAND - BW Division");
		Entry sandqoe = logReader.readEntry(folder + "/1clientsandqoe_c1.log", "SAND - QoE Fairness");

		List<Entry> entries = List.of(basic, netflix, sara, sandbd, sandqoe);
		String segmentTitle = String.format("Bandwidth per segment - %sMbps Total Link Capacity - 1 client", folder);
		String bufferTitle = String.format("Buffer per segment - %sMbps Total Link Capacity - 1 client", folder);
		String qoeTitle = String.format("QoE per segment - %sMbps Total Link Capacity - 1 client", folder);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, 100, 10);
		LineGraph bufferPerSegment = chartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, 100, 10);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, bufferPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	private void analyzeMultiplePerAlgorithm(String folder, int clients, String mode, String algorithm) {
		List<Entry> entries = getEntries(folder, clients, mode, algorithm);

		String segmentTitle = String.format("Bandwidth per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String bufferTitle = String.format("Buffer per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String qoeTitle = String.format("QoE per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, 100, 10);
		LineGraph bufferPerSegment = chartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, 100, 10);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, bufferPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	private void analyzeMeanPerAlgorithm(String folder, int clients, String mode) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> entries = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEntries = getEntries(folder, clients, mode, algorithm);
			Entry meanEntry = getMeanEntry(tempEntries);
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

	private void analyzeTotalPerAlgorithm(String folder, int clients, String mode) {
		List<String> algorithms = List.of("basic", "netflix", "sara", "sandbanddiv", "sandqoe");
		List<Entry> entries = new ArrayList<>(5);
		for (String algorithm : algorithms) {
			List<Entry> tempEntries = getEntries(folder, clients, mode, algorithm);
			Entry meanEntry = getTotalEntry(tempEntries);
			meanEntry.setName(algorithm);
			entries.add(meanEntry);
		}

		String segmentTitle = String.format("Total Bandwidth per segment\n%sMbps Total Link Capacity - %d clients", folder, clients);
		String qoeTitle = String.format("Total QoE per segment\n%sMbps Total Link Capacity - %d clients", folder, clients);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, 100, 10);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, 100, 10, clients);

		saveChartS(folder, bwPerSegment);
		saveChartS(folder, qoePerSegment);
	}

	private void analyzeMultiplePSDN(String folder, int clients, String mode, String algorithm) {
		Entry client1 = logReader.readEntry(folder + "/"+ mode + algorithm+ "_c1.log", "Client1");
		Entry client2 = logReader.readEntry(folder + "/"+ mode + algorithm+ "_c2.log", "Client2");
		List<Entry> entries = new java.util.ArrayList<>(List.of(client1, client2));
		if (clients == 3) {
			Entry client3 = logReader.readEntry(folder + "/" + mode + algorithm+ "_c3.log", "Client3");
			entries.add(client3);
		}

		String segmentTitle = String.format("Bandwidth per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String bufferTitle = String.format("Buffer per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String qoeTitle = String.format("QoE per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);

		LineGraph bwPerSegment = chartCreator(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, 100, 10);
		LineGraph bufferPerSegment = chartCreator(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = chartCreator(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, 100, 10);
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

	private List<Entry> getEntries(String folder, int clients, String mode, String algorithm) {
		Entry client1 = logReader.readEntry(folder + "/" + clients + "clients" + mode + algorithm+ "_c1.log", "Client1");
		Entry client2 = logReader.readEntry(folder + "/" + clients + "clients" + mode + algorithm+ "_c2.log", "Client2");
		List<Entry> entries = new java.util.ArrayList<>(List.of(client1, client2));
		if (clients == 3) {
			Entry client3 = logReader.readEntry(folder + "/" + clients + "clients" + mode + algorithm+ "_c3.log", "Client3");
			entries.add(client3);
		}
		return entries;
	}

	private Entry getMeanEntry(List<Entry> entries) {
		ArrayList<Integer> bitrates = new ArrayList<>(100);
		ArrayList<Double> qoeMetrics = new ArrayList<>(100);
		Map<Integer, Integer> bufferPerSecond = new HashMap<>(100);
		Map<Integer, Double> qoe = Map.ofEntries(Map.entry(45652, 0.742), Map.entry(89283, 0.876), Map.entry(131087, 0.916), Map.entry(178351, 0.914), Map.entry(221600, 0.930), Map.entry(262537, 0.940), Map.entry(334349, 0.951), Map.entry(396126, 0.958), Map.entry(522286, 0.954), Map.entry(595491, 0.959), Map.entry(791182, 0.946), Map.entry(1032682, 0.957), Map.entry(1244778, 0.963), Map.entry(1546902, 0.969), Map.entry(2133691, 0.959), Map.entry(2484135, 0.964), Map.entry(3078587, 0.970), Map.entry(3526922, 0.973), Map.entry(3840360, 0.975), Map.entry(4219897, 0.978));

		for (int i = 1; i < entries.get(0).getPlayingBitrate().size(); i++) {
			int totalBitrate = 0;
			int totalBufferPerSecond = 0;
			double tempQoE = 0;
			for (Entry entry : entries) {
				totalBitrate += entry.getPlayingBitrate().get(i);
				tempQoE += qoe.get(entry.getPlayingBitrate().get(i));
			}
			bitrates.add(totalBitrate/entries.size());
			qoeMetrics.add(tempQoE/entries.size());
			bufferPerSecond.put((i-1)*2,  totalBufferPerSecond/entries.size());
		}
		Entry newEntry = new Entry();
		newEntry.setPlayingBitrate(bitrates);
		newEntry.setBufferPerSecond(bufferPerSecond);
		newEntry.setQoeMetrics(qoeMetrics);
		return newEntry;
	}

	private Entry getTotalEntry(List<Entry> entries) {
		ArrayList<Integer> bitrates = new ArrayList<>(100);
		ArrayList<Double> qoeMetrics = new ArrayList<>(100);
		Map<Integer, Double> qoe = Map.ofEntries(Map.entry(45652, 0.742), Map.entry(89283, 0.876), Map.entry(131087, 0.916), Map.entry(178351, 0.914), Map.entry(221600, 0.930), Map.entry(262537, 0.940), Map.entry(334349, 0.951), Map.entry(396126, 0.958), Map.entry(522286, 0.954), Map.entry(595491, 0.959), Map.entry(791182, 0.946), Map.entry(1032682, 0.957), Map.entry(1244778, 0.963), Map.entry(1546902, 0.969), Map.entry(2133691, 0.959), Map.entry(2484135, 0.964), Map.entry(3078587, 0.970), Map.entry(3526922, 0.973), Map.entry(3840360, 0.975), Map.entry(4219897, 0.978));

		for (int i = 0; i < entries.get(0).getPlayingBitrate().size(); i++) {
			int totalBitrate = 0;
			double tempQoE = 0;
			for (Entry entry : entries) {
				totalBitrate += entry.getPlayingBitrate().get(i);
				tempQoE += qoe.get(entry.getPlayingBitrate().get(i));
			}
			bitrates.add(totalBitrate);
			qoeMetrics.add(tempQoE);
		}
		Entry newEntry = new Entry();
		newEntry.setPlayingBitrate(bitrates);
		newEntry.setQoeMetrics(qoeMetrics);
		return newEntry;
	}

	private void saveChartS(String folder, LineGraph graph) {
		Path resourceDirectoryPath = Paths.get("src","main", "resources");
		File resourceDirectory = new File(resourceDirectoryPath.toUri());
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
