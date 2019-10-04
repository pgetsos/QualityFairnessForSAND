package gr.pgetsos.sandservermock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Helpers {
	private static final Logger logger = LogManager.getLogger("Helpers");

	static float caclculateInitialCapacity() {
		long totalDownload = 0;
		final int BUFFER_SIZE = 8192;
		byte[] data = new byte[BUFFER_SIZE];
		long startTime = 0;
		try (BufferedInputStream in = new BufferedInputStream(new URL("ftp://speedtest:speedtest@ftp.otenet.gr/test100Mb.db").openStream())) {
			int dataRead = 0;
			startTime = System.nanoTime();
			while ((dataRead = in.read(data, 0, 8192)) > 0) {
				totalDownload += dataRead;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		float bytesPerSec = totalDownload / ((System.nanoTime() - startTime) / (float) 1000000000);
		return bytesPerSec / (1024) * 8;
	}

	static int calculateInitialCapacityIPerf() {
		try {
			Process process = Runtime.getRuntime().exec("iperf -c 10.0.0.7 -t 10 -w 2048");

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String input_line = null;
			try {
				while ((input_line = input.readLine()) != null && !input_line.equals("")) {
					int bits = parseIperfLine(input_line);
					if (bits != -1) {
						return bits;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		} catch (IOException e) {
			logger.error(e);
		}
		return 0;
	}

	public static int parseIperfLine(String line) {
		int bits = -1;
		if (line.contains("Bytes") && line.contains("bits")) {
			String bitsString = line.split("ytes")[1].strip().split(" ")[0];
			double dbits = Double.parseDouble(bitsString);
			bits = line.contains("Kbits") ? (int) (dbits*1000) : (int) dbits;
			bits = line.contains("Mbits") ? (int) (dbits*1000*1000) : (int) dbits;
		}
		return bits;
	}
}
