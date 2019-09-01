package gr.pgetsos.sandservermock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class Helpers {
	private static final Logger logger = LogManager.getLogger("Helpers");

	static float caclculateInitialCapacity() {
		long totalDownload = 0;
		final int BUFFER_SIZE = 1024;
		byte[] data = new byte[BUFFER_SIZE];
		long startTime = 0;
		try (BufferedInputStream in = new BufferedInputStream(new URL("ftp://speedtest:speedtest@ftp.otenet.gr/test10Mb.db").openStream())) {
			int dataRead = 0;
			startTime = System.nanoTime();
			while ((dataRead = in.read(data, 0, 1024)) > 0) {
				totalDownload += dataRead;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		float bytesPerSec = totalDownload / ((System.nanoTime() - startTime) / (float) 1000000000);
		return bytesPerSec / (1024);
	}
}
