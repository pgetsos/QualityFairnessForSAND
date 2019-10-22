package gr.pgetsos.graphs;

public class ExperimentRunner {
	private ExperimentAnalyzer analyzer = new ExperimentAnalyzer();
	private ExperimentAnalyzerAsync asyncAnalyzer = new ExperimentAnalyzerAsync();

	private static final String FOLDER_LOW = "1.3 total";
	private static final String FOLDER_HIGH = "6 total";
	private static final String FOLDER_LOW_ALT = "1.3 10s";
	private static final String FOLDER_HIGH_ALT = "6 10s";
	private static final String MBPS_LOW = "1.3";
	private static final String MBPS_HIGH = "6";
	private static final int LIMIT = 101;
	private static final int LIMIT_ALT = 61;

	public void runAll() {
		analyzer.analyzeAdjustedQoE(FOLDER_LOW, FOLDER_LOW_ALT, 2, MBPS_LOW);
		analyzer.analyzeAdjustedQoE(FOLDER_LOW, FOLDER_LOW_ALT, 3, MBPS_LOW);
		analyzer.analyzeAdjustedQoE(FOLDER_HIGH, FOLDER_HIGH_ALT, 2, MBPS_HIGH);
		analyzer.analyzeAdjustedQoE(FOLDER_HIGH, FOLDER_HIGH_ALT, 3, MBPS_HIGH);

		analyzer.analyzeSingleAdjusted(FOLDER_LOW, FOLDER_LOW_ALT, MBPS_LOW);
		analyzer.analyzeSingleAdjusted(FOLDER_LOW, FOLDER_LOW_ALT, MBPS_LOW);
		analyzer.analyzeSingleAdjusted(FOLDER_HIGH, FOLDER_HIGH_ALT, MBPS_HIGH);
		analyzer.analyzeSingleAdjusted(FOLDER_HIGH, FOLDER_HIGH_ALT, MBPS_HIGH);

		analyzer.analyzeSingle(FOLDER_LOW, LIMIT, MBPS_LOW);
		analyzer.analyzeSingle(FOLDER_LOW_ALT, LIMIT_ALT, MBPS_LOW);
		analyzer.analyzeSingle(FOLDER_HIGH, LIMIT, MBPS_HIGH);
		analyzer.analyzeSingle(FOLDER_HIGH_ALT, LIMIT_ALT, MBPS_HIGH);

		runAllMultiple(FOLDER_LOW, MBPS_LOW, LIMIT);
		runAllMultiple(FOLDER_LOW_ALT, MBPS_LOW, LIMIT_ALT);
		runAllMultiple(FOLDER_HIGH, MBPS_HIGH, LIMIT);
		runAllMultiple(FOLDER_HIGH_ALT, MBPS_HIGH, LIMIT_ALT);

		runAllTotal();
		runAllMean();

		/*runAllMultiple(FOLDER_LOW, MBPS_LOW_ASYNC, LIMIT);
		runAllMultiple(FOLDER_LOW_ALT, MBPS_LOW_ASYNC, LIMIT_ALT);
		runAllMultiple(FOLDER_HIGH, MBPS_HIGH_ASYNC, LIMIT);
		runAllMultiple(FOLDER_HIGH_ALT, MBPS_HIGH_ASYNC, LIMIT_ALT);

		runAllTotal(MODE_ASYNC);
		runAllMean(MODE_ASYNC);*/
	}

	public void runAllMultiple(String folder, String mbps, int limit) {
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, "basic", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, "netflix", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, "sara", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, "sandqoe", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 2, "sandbanddiv", limit, mbps);

		analyzer.analyzeMultiplePerAlgorithm(folder, 3, "basic", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, "netflix", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, "sara", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, "sandqoe", limit, mbps);
		analyzer.analyzeMultiplePerAlgorithm(folder, 3, "sandbanddiv", limit, mbps);

		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 2, "basic", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 2, "netflix", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 2, "sara", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 2, "sandqoe", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 2, "sandbanddiv", limit, mbps);

		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 3, "basic", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 3, "netflix", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 3, "sara", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 3, "sandqoe", limit, mbps);
		asyncAnalyzer.analyzeMultiplePerAlgorithm(folder, 3, "sandbanddiv", limit, mbps);
	}

	public void runAllTotal() {
		analyzer.analyzeTotals(FOLDER_LOW, 2, LIMIT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_LOW, 3, LIMIT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_LOW_ALT, 2, LIMIT_ALT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_LOW_ALT, 3, LIMIT_ALT, MBPS_LOW);
		analyzer.analyzeTotals(FOLDER_HIGH, 2, LIMIT, MBPS_HIGH);
		analyzer.analyzeTotals(FOLDER_HIGH, 3, LIMIT, MBPS_HIGH);
		analyzer.analyzeTotals(FOLDER_HIGH_ALT, 2, LIMIT_ALT, MBPS_HIGH);
		analyzer.analyzeTotals(FOLDER_HIGH_ALT, 3, LIMIT_ALT, MBPS_HIGH);
	}

	public void runAllMean() {

	}
}
