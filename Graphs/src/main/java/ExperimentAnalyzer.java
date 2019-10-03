import java.util.List;

import javax.swing.WindowConstants;

public class ExperimentAnalyzer {
	private LogReader logReader = new LogReader();
	private static final String SEGMENT = "Segments";
	private static final String BUFFER = "Buffer";
	private static final String QOE = "QoE";
	private static final String SEGMENT_SHORT_X_AXIS = "Segment (2s per segment)";
	private static final String TIME_X_AXIS = "Time (in seconds)";
	private static final String BITRATE_Y_AXIS = "Bitrate";
	private static final String BUFFER_Y_AXIS = "Buffer Size";

	public void runAll() {
//		analyzeSingle("1.3");
//		analyzeSingle("18");

		analyzeMultiplePerAlgorithm("18", 2, "sync", "basic");
		analyzeMultiplePerAlgorithm("18", 3, "sync", "basic");
		analyzeMultiplePerAlgorithm("18", 2, "sync", "netflix");
		analyzeMultiplePerAlgorithm("18", 3, "sync", "netflix");
		analyzeMultiplePerAlgorithm("18", 2, "sync", "sara");
		analyzeMultiplePerAlgorithm("18", 3, "sync", "sara");
		analyzeMultiplePerAlgorithm("18", 2, "sync", "sandqoe");
		analyzeMultiplePerAlgorithm("18", 3, "sync", "sandqoe");
		analyzeMultiplePerAlgorithm("18", 2, "sync", "sandbanddiv");
		analyzeMultiplePerAlgorithm("18", 3, "sync", "sandbanddiv");
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

		LineGraph bwPerSegment = chartCreatorSingle(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, 100, 10);
		LineGraph bufferPerSegment = chartCreatorSingle(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = chartCreatorSingle(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, 100, 10);

		bwPerSegment.setVisible(true);
		bufferPerSegment.setVisible(true);
		qoePerSegment.setVisible(true);
	}

	private void analyzeMultiplePerAlgorithm(String folder, int clients, String mode, String algorithm) {
		Entry client1 = logReader.readEntry(folder + "/" + clients + "clients" + mode + algorithm+ "_c1.log", "Client1");
		Entry client2 = logReader.readEntry(folder + "/" + clients + "clients" + mode + algorithm+ "_c2.log", "Client2");
		List<Entry> entries = new java.util.ArrayList<>(List.of(client1, client2));
		if (clients == 3) {
			Entry client3 = logReader.readEntry(folder + "/" + clients + "clients" + mode + algorithm+ "_c3.log", "Client3");
			entries.add(client3);
		}

		String segmentTitle = String.format("Bandwidth per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String bufferTitle = String.format("Buffer per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);
		String qoeTitle = String.format("QoE per segment\n%sMbps Total Link Capacity - %d clients - %s", folder, clients, algorithm);

		LineGraph bwPerSegment = chartCreatorSingle(SEGMENT, entries, segmentTitle, SEGMENT_SHORT_X_AXIS, BITRATE_Y_AXIS, 100, 10);
		LineGraph bufferPerSegment = chartCreatorSingle(BUFFER, entries, bufferTitle, TIME_X_AXIS, BUFFER_Y_AXIS, 600, 20);
		LineGraph qoePerSegment = chartCreatorSingle(QOE, entries, qoeTitle, SEGMENT_SHORT_X_AXIS, QOE, 100, 10);

		bwPerSegment.setVisible(true);
		bufferPerSegment.setVisible(true);
		qoePerSegment.setVisible(true);
	}

	private LineGraph chartCreatorSingle(String chartType, List<Entry> entries, String title, String xAxis, String yAxis, int limit, int tick) {
		LineGraph graph = new LineGraph(title, entries);
		graph.chart(chartType, xAxis, yAxis, limit, tick);
		graph.setSize(1920, 1080);
		graph.setLocationRelativeTo(null);
		graph.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		return graph;
	}
}
