package gr.pgetsos.graphs.Helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import gr.pgetsos.graphs.Entry;

public class LogReader {
	private static final Logger logger = LogManager.getLogger("CSVLoader");
	private static final String SPLITTER = "Action=Writing,Bitrate=";
	private static final String SEC_SPLITTER = "Action=StillPlaying,Bitrate=";

	public Entry readEntry(String folder, String file, String entryName){
		ArrayList<Integer> bitrates = new ArrayList<>(100);
		ArrayList<Integer> buffer = new ArrayList<>(100);
		ArrayList<Double> qoeMetrics = new ArrayList<>(100);
		Map<Integer, Double> qoe = Helpers.getQoEMap(folder);
		Map<Integer, Integer> bufferPerSecond = new HashMap<>(100);
		try {
			Path path = Paths.get(this.getClass().getClassLoader().getResource(folder + "/" + file).toURI());
			try (Stream<String> lines = Files.lines(path)) {
				lines.forEachOrdered(line -> {
					if (line.contains(SPLITTER)) {
						String bitrateString = line.split(SPLITTER)[1].strip();
						int bitrate = Integer.parseInt(bitrateString.split("\\.")[0]);
						bitrates.add(bitrate);
						qoeMetrics.add(qoe.get(bitrate));
						String buffered = line.split("CurrentBufferSize=")[1].split(",")[0];
						buffer.add(Integer.parseInt(buffered));
					} else if (line.contains(SEC_SPLITTER)) {
						String time = line.split("EpochTime=")[1].split("\\.")[0];
						String buffered = line.split("CurrentBufferSize=")[1].split(",")[0];
						bufferPerSecond.put(Integer.parseInt(time), Integer.parseInt(buffered));
					}
				});
			}
		}catch (IOException ex) {
			// do something or re-throw...
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		Entry entry = new Entry();
		entry.setName(entryName);
		entry.setPlayingBitrate(bitrates);
		entry.setBufferState(buffer);
		entry.setBufferPerSecond(bufferPerSecond);
		entry.setQoeMetrics(qoeMetrics);
		return entry;
	}
}
