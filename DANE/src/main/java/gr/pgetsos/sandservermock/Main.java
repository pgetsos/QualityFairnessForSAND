package gr.pgetsos.sandservermock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
	private static final Logger logger = LogManager.getLogger("Main");

	public static void main(String[] args) {
		logger.info("Hello, World! Starting DANE....");

		int bps = Helpers.calculateInitialCapacityIPerf();

		boolean stableMode = args.length <= 0 || "stable".equals(args[0]);
		String runningMode = stableMode ? "STABLE" : "UNSTABLE";

		logger.info("Calculated total initial bandwidth {} bps", () -> bps);
		logger.info("Running in {} mode", () -> runningMode);

		ClientSockets sockets = new ClientSockets(bps, stableMode);

		sockets.start();
	}
}