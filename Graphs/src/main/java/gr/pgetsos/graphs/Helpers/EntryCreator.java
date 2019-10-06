package gr.pgetsos.graphs.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.pgetsos.graphs.Entry;

public class EntryCreator {
	public List<Entry> getEntries(String folder, int clients, String mode, String algorithm) {
		LogReader logReader = new LogReader();
		Entry client1 = logReader.readEntry(folder, clients + "clients" + mode + algorithm+ "_c1.log", "Client1");
		Entry client2 = logReader.readEntry(folder, clients + "clients" + mode + algorithm+ "_c2.log", "Client2");
		List<Entry> entries = new java.util.ArrayList<>(List.of(client1, client2));
		if (clients == 3) {
			Entry client3 = logReader.readEntry(folder, clients + "clients" + mode + algorithm+ "_c3.log", "Client3");
			entries.add(client3);
		}
		return entries;
	}

	public Entry getMeanEntry(List<Entry> entries, String folder) {
		ArrayList<Integer> bitrates = new ArrayList<>(100);
		ArrayList<Double> qoeMetrics = new ArrayList<>(100);
		Map<Integer, Integer> bufferPerSecond = new HashMap<>(100);
		Map<Integer, Double> qoe = Helpers.getQoEMap(folder);

		for (int i = 1; i < entries.get(0).getPlayingBitrate().size(); i++) {
			int totalBitrate = 0;
			int totalBufferPerSecond = 0;
			double tempQoE = 0;
			for (Entry entry : entries) {
				totalBitrate += entry.getPlayingBitrate().get(i);
				tempQoE += qoe.get(entry.getPlayingBitrate().get(i));
			}
			bitrates.add(totalBitrate/entries.size());
			qoeMetrics.add(tempQoE/entries.size());
			bufferPerSecond.put((i-1)*2,  totalBufferPerSecond/entries.size());
		}
		Entry newEntry = new Entry();
		newEntry.setPlayingBitrate(bitrates);
		newEntry.setBufferPerSecond(bufferPerSecond);
		newEntry.setQoeMetrics(qoeMetrics);
		return newEntry;
	}

	public Entry getTotalEntry(List<Entry> entries, String folder) {
		ArrayList<Integer> bitrates = new ArrayList<>(100);
		ArrayList<Double> qoeMetrics = new ArrayList<>(100);
		Map<Integer, Double> qoe = Helpers.getQoEMap(folder);

		for (int i = 0; i < entries.get(0).getPlayingBitrate().size(); i++) {
			int totalBitrate = 0;
			double tempQoE = 0;
			for (Entry entry : entries) {
				totalBitrate += entry.getPlayingBitrate().get(i);
				tempQoE += qoe.get(entry.getPlayingBitrate().get(i));
			}
			bitrates.add(totalBitrate);
			qoeMetrics.add(tempQoE);
		}
		Entry newEntry = new Entry();
		newEntry.setPlayingBitrate(bitrates);
		newEntry.setQoeMetrics(qoeMetrics);
		return newEntry;
	}
}
