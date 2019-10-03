import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
	private static final Logger logger = LogManager.getLogger("Main");

	public static void main(String[] args) {
		LogRenamer.renameAllLogs("");
		LogRenamer.renameAllLogs("1.3");
		LogRenamer.renameAllLogs("18");

		ExperimentAnalyzer analyzer = new ExperimentAnalyzer();

		analyzer.runAll();
	}
}