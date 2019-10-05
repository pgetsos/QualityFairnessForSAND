
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

public class LogReader {
	private static final Logger logger = LogManager.getLogger("CSVLoader");
	private static final String SPLITTER = "Action=Writing,Bitrate=";
	private static final String SEC_SPLITTER = "Action=StillPlaying,Bitrate=";

	public Entry readEntry(String file, String entryName){
		ArrayList<Integer> bitrates = new ArrayList<>(100);
		ArrayList<Integer> buffer = new ArrayList<>(100);
		ArrayList<Double> qoeMetrics = new ArrayList<>(100);
		Map<Integer, Double> qoe = Map.ofEntries(Map.entry(45652, 0.742), Map.entry(89283, 0.876), Map.entry(131087, 0.916), Map.entry(178351, 0.914), Map.entry(221600, 0.930), Map.entry(262537, 0.940), Map.entry(334349, 0.951), Map.entry(396126, 0.958), Map.entry(522286, 0.954), Map.entry(595491, 0.959), Map.entry(791182, 0.946), Map.entry(1032682, 0.957), Map.entry(1244778, 0.963), Map.entry(1546902, 0.969), Map.entry(2133691, 0.959), Map.entry(2484135, 0.964), Map.entry(3078587, 0.970), Map.entry(3526922, 0.973), Map.entry(3840360, 0.975), Map.entry(4219897, 0.978));
		Map<Integer, Integer> bufferPerSecond = new HashMap<>(100);

		try {
			Path path = Paths.get(this.getClass().getResource(file).toURI());
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
