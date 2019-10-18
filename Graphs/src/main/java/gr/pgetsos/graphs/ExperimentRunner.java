package gr.pgetsos.graphs;

public class ExperimentRunner {
	private ExperimentAnalyzer analyzer = new ExperimentAnalyzer();
	
	private static final String FOLDER_LOW = "1.3 total";
	private static final String FOLDER_HIGH = "6 total";
	private static final String FOLDER_LOW_ALT = "1.3 10s";
	private static final String FOLDER_HIGH_ALT = "6 10s";
	private static final String MBPS_LOW = "1.3";
	private static final String MBPS_HIGH = "6";
	private static final String MODE_SYNC = "sync";
	private static final String MODE_ASYNC = "async";
	private static final int LIMIT = 302;
	private static final int LIMIT_ALT = 62;

	public void runAll() {
		analyzer.analyzeAdjustedQoE(FOLDER_LOW, FOLDER_LOW_ALT, 2, MODE_SYNC, MBPS_LOW);
		analyzer.analyzeAdjustedQoE(FOLDER_LOW, FOLDER_LOW_ALT, 3, MODE_SYNC, MBPS_LOW);
		analyzer.analyzeAdjustedQoE(FOLDER_HIGH, FOLDER_HIGH_ALT, 2, MODE_SYNC, MBPS_HIGH);
		analyzer.analyzeAdjustedQoE(FOLDER_HIGH, FOLDER_HIGH_ALT, 3, MODE_SYNC, MBPS_HIGH);

		if (0==0) return;
		analyzer.analyzeSingle(FOLDER_LOW, LIMIT_ALT, MBPS_LOW);
		analyzer.analyzeSingle(FOLDER_LOW_ALT, LIMIT_ALT, MBPS_LOW);
		analyzer.analyzeSingle(FOLDER_HIGH, LIMIT_ALT, MBPS_HIGH);
		analyzer.analyzeSingle(FOLDER_HIGH_ALT, LIMIT_ALT, MBPS_HIGH);

		runAllMultiple(FOLDER_LOW, MBPS_LOW, MODE_SYNC, LIMIT);
		runAllMultiple(FOLDER_LOW_ALT, MBPS_LOW, MODE_SYNC, LIMIT_ALT);
		runAllMultiple(FOLDER_HIGH, MBPS_HIGH, MODE_SYNC, LIMIT);
		runAllMultiple(FOLDER_HIGH_ALT, MBPS_HIGH, MODE_SYNC, LIMIT_ALT);

		runAllTotal(MODE_SYNC);
		runAllMean(MODE_SYNC);

		/*runAllMultiple(FOLDER_LOW, MBPS_LOW, MODE_ASYNC, LIMIT);
		runAllMultiple(FOLDER_LOW_ALT, MBPS_LOW, MODE_ASYNC, LIMIT_ALT);
		runAllMultiple(FOLDER_HIGH, MBPS_HIGH, MODE_ASYNC, LIMIT);
		runAllMultiple(FOLDER_HIGH_ALT, MBPS_HIGH, MODE_ASYNC, LIMIT_ALT);

		runAllTotal(MODE_ASYNC);
		runAllMean(MODE_ASYNC);*/
	}

	public void runAllMultiple(String folder, String mbps, String mode, int limit) {
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, mode, "basic", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, mode, "netflix", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, mode, "sara", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, mode, "sandqoe", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, mode, "sandbanddiv", limit, mbps);

		analyzer.analyzeMultiplePerAlgorithm(folder, 3, mode, "basic", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, mode, "netflix", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, mode, "sara", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, mode, "sandqoe", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, mode, "sandbanddiv", limit, mbps);
	}

	public void runAllTotal(String mode) {
		analyzer.analyzeTotals(FOLDER_LOW, 2, mode, LIMIT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_LOW, 3, mode, LIMIT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_LOW_ALT, 2, mode, LIMIT_ALT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_LOW_ALT, 3, mode, LIMIT_ALT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_HIGH, 2, mode, LIMIT, MBPS_HIGH);
		analyzer.analyzeTotals(FOLDER_HIGH, 3, mode, LIMIT, MBPS_HIGH);
		analyzer.analyzeTotals(FOLDER_HIGH_ALT, 2, mode, LIMIT_ALT, MBPS_HIGH);
		analyzer.analyzeTotals(FOLDER_HIGH_ALT, 3, mode, LIMIT_ALT, MBPS_HIGH);
	}

	public void runAllMean(String mode) {

	}
}
