package gr.pgetsos.sandservermock;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
	private static final Logger logger = LogManager.getLogger("Main");

	public static void main(String[] args) {
		logger.info("Hello, World! Starting DANE....");

		float kbps = Helpers.caclculateInitialCapacity();

		boolean stableMode = Boolean.parseBoolean(args[0]);
		String runningMode = stableMode ? "STABLE" : "UNSTABLE";

		logger.info("Calculated total initial bandwidth {} kbps", () -> kbps);
		logger.info("Running in {} mode", () -> runningMode);

		ClientSockets sockets = new ClientSockets(kbps, stableMode);
		sockets.start(3535);

	}
}