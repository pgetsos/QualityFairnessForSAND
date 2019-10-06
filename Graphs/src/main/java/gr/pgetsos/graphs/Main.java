package gr.pgetsos.graphs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gr.pgetsos.graphs.Helpers.LogRenamer;


public class Main {
	private static final Logger logger = LogManager.getLogger("gr.pgetsos.graphs.Main");

	public static void main(String[] args) {
		LogRenamer.renameAllLogs("");
		LogRenamer.renameAllLogs("1.3");
		LogRenamer.renameAllLogs("6");
		LogRenamer.renameAllLogs("1.3 10s");

		ExperimentAnalyzer analyzer = new ExperimentAnalyzer();

		analyzer.runAll();
	}
}
