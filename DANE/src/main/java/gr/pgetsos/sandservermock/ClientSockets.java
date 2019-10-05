package gr.pgetsos.sandservermock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ClientSockets {
	private static final Logger logger = LogManager.getLogger("gr.pgetsos.SANDServerMock.ClientSockets");
	private static final String RECEIVED = "Received";

	private ConcurrentHashMap<String, ClientInfo> clients = new ConcurrentHashMap<>(3);
	private int calculatedBandwidth;
	private boolean stableMode;

	private ServerSocket serverSocket;

	ClientSockets(int calculatedBandwidth, boolean stableMode) {
		this.calculatedBandwidth = calculatedBandwidth;
		this.stableMode = stableMode;
	}

	void start() {
		try {
			logger.debug("Starting socket for incoming connections");
			serverSocket = new ServerSocket(3535);
			//noinspection InfiniteLoopStatement
			while (true) {
				new EchoClientHandler(this, serverSocket.accept()).start();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private ConcurrentHashMap<String, ClientInfo> getClients() {
		return clients;
	}

	void setClients(ConcurrentHashMap<String, ClientInfo> clients) {
		this.clients = clients;
	}

	private int getCalculatedBandwidth() {
		return calculatedBandwidth;
	}

	private void setCalculatedBandwidth(int calculatedBandwidth) {
		this.calculatedBandwidth = calculatedBandwidth;
	}

	private boolean isStableMode() {
		return stableMode;
	}

	void setStableMode(boolean stableMode) {
		this.stableMode = stableMode;
	}

	private static class EchoClientHandler extends Thread {
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		private String clientIP;
		ClientSockets parent;

		EchoClientHandler(ClientSockets parent, Socket socket) {
			this.parent = parent;
			this.clientSocket = socket;
		}

		@Override
		public void run() {
			try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String inputLine = "";
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.startsWith("IP")) {
						onIPReceived(inputLine);
					} else if (inputLine.startsWith("QLIST")) {
						onQualityListReceived(inputLine);
					} else if (inputLine.startsWith("Bandwidth")) {
						onBandwidthReceived(inputLine);
					} else if (inputLine.startsWith("Level")) {
						onLevelReceived(inputLine);
					} else if (inputLine.startsWith("Buffer")) {
						onBufferReceived(inputLine);
					} else if (inputLine.startsWith("Stalling")) {
						onStallingReceived(inputLine);
					} else if (inputLine.startsWith("Quality")) {
						onQualityReceived(inputLine);
					} else if (inputLine.equals("Requesting bandwidth")) {
						onBandwidthRequest();
					} else if (inputLine.equals("Requesting fairness")) {
						onFairnessRequest();
					} else if (inputLine.equals("LastOver")) {
						onClientFinished();
					}
				}
				in.close();
				out.close();
				clientSocket.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}

		private void onIPReceived (String line) {
			clientIP = line.split(":")[1].trim();
			if (!parent.getClients().containsKey(clientIP)) {
				ClientInfo newClient = new ClientInfo();
				newClient.setIpAddress(clientIP);
				parent.getClients().put(clientIP, newClient);
				logger.info("New registration! "+clientIP);
			}
			out.println(RECEIVED);
		}

		private void onQualityListReceived (String line) {
			Map<Integer, Double> qualityMap = new HashMap<>();
			String[] values = line.split(" NEW", 2)[1].split("NEW");
			parent.clients.get(clientIP).setMinimumBandwidth(Integer.parseInt(values[0].split(":")[0]));
			for (String value : values) {
				Integer bitrate = Integer.parseInt(value.split(":")[0]);
				double quality = Double.parseDouble(value.split(":")[1]);
				qualityMap.put(bitrate, quality);
			}
			parent.getClients().get(clientIP).setQualityList(qualityMap);
			out.println(RECEIVED);
		}

		private void onBandwidthReceived (String line) {
			int bandwidth = (int) Double.parseDouble(line.split(":")[1].trim());
			parent.getClients().get(clientIP).setLastMeasuredBandwidth(bandwidth);
			out.println(RECEIVED);
		}

		private void onLevelReceived (String line) {
			int level = Integer.parseInt(line.split(":")[1].trim());
			parent.getClients().get(clientIP).setLastLevel(level);
			out.println(RECEIVED);
		}

		private void onQualityReceived (String line) {
			double score = Double.parseDouble(line.split(":")[1].trim());
			parent.getClients().get(clientIP).setQualityScore(score);
			out.println(RECEIVED);
		}

		private void onBufferReceived (String line) {
			String bufferString = line.split(":")[1].trim();
			int buffer = Integer.parseInt(bufferString);
			parent.getClients().get(clientIP).setBuffer(buffer);
			out.println(RECEIVED);
		}

		private void onStallingReceived (String line) {
			String stallString = line.split(":")[1].trim();
			boolean stalled = stallString.equals("true");
			parent.getClients().get(clientIP).setStalled(stalled);
			out.println(RECEIVED);
		}

		private void onClientFinished () {
			parent.getClients().remove(clientIP);
		}

		private void onBandwidthRequest() {
			if (parent.isStableMode()) {
				double maxAllowed = parent.getCalculatedBandwidth() / parent.getClients().size();
				out.println(RECEIVED + maxAllowed);
			} else {
				double recalculated = recalculateBandwidth();
				parent.setCalculatedBandwidth((int) recalculated*parent.getClients().size());
				out.println(RECEIVED + recalculated);
				logger.info("Recalculating... " + parent.getCalculatedBandwidth());
			}
		}

		private void onFairnessRequest() {
			if (parent.isStableMode()) {
				int qualityBased = bruteForceQuality();
				out.println(RECEIVED + qualityBased);
			} else {
				double recalculated = recalculateBandwidth();
				parent.setCalculatedBandwidth((int) recalculated*parent.getClients().size());
				int qualityBased = bruteForceQuality();
				out.println(RECEIVED + qualityBased);
				logger.info("Recalculating... " + parent.getCalculatedBandwidth());
			}
		}

		private double recalculateBandwidth() {
			int max = parent.getCalculatedBandwidth();
			int clientMax = 0;
			ClientInfo client = parent.getClients().get(clientIP);
			if (client.getLastMeasuredBandwidth() == -1) {
				return parent.getCalculatedBandwidth() / parent.getClients().size();
			}
			for (ClientInfo clientInfo : parent.getClients().values()) {
				clientMax += clientInfo.getLastMeasuredBandwidth();
			}
			if (clientMax >= max*1.05) {
				max = (int) (clientMax*0.9);
				parent.setCalculatedBandwidth(max);
				return parent.getCalculatedBandwidth() / parent.getClients().size();
			} else {
				if (client.getLastLevel() > client.getLastMeasuredBandwidth()) {
					if (client.getBuffer() > 2) {
						return client.getLastLevel();
					} else {
						return  (client.getLastLevel() + client.getLastMeasuredBandwidth()) / 2;
					}
				} else {
					double maxAllowed = ((parent.getCalculatedBandwidth() / parent.getClients().size()) + client.getLastMeasuredBandwidth()) / 2;
					return  (maxAllowed);
					}
				}
			}
		}

		private int bruteForceQuality() {
			double minimumBandwidth = 0;
			int maximumBandwidth = parent.getCalculatedBandwidth();
			List<Map<Integer, Double>> lists = new ArrayList<>();
			for (ClientInfo entry : parent.clients.values()) {
				minimumBandwidth += entry.getMinimumBandwidth();
				if (!entry.getIpAddress().equals(clientIP)) {
					lists.add(entry.qualityList);
				}
			}
			if (maximumBandwidth < minimumBandwidth) {
				return maximumBandwidth / parent.getClients().size();
			}

			Map<Integer, Double> clientMap = parent.getClients().get(clientIP).getQualityList();

			double maxQoE = 0;
			int optimalBitrate = 0;
			for (Integer bitrate : clientMap.keySet()) {
				if (bitrate > maximumBandwidth) {
					continue;
				}
				int rest = maximumBandwidth - bitrate;
				double tempQoe = bruteForceLoop(lists, rest, clientMap.get(bitrate));
				if (tempQoe == -1) {
					continue;
				}
				if (lists.isEmpty()) {
					tempQoe = clientMap.get(bitrate);
				}
				if (tempQoe > maxQoE) {
					maxQoE = tempQoe;
					optimalBitrate = bitrate;
				}
			}
			logger.info("Optimal bitrate for total QoE Fairness of " + maxQoE + " is " + optimalBitrate);
			return optimalBitrate;
		}

		private double bruteForceLoop(List<Map<Integer, Double>> lists, int maximumBandwidth, double... qoe) {
			double maxFairness = 0;
			if (lists.isEmpty()) {
				return 0;
			}
			for (Integer bitrate : lists.get(0).keySet()) {
				double tempFairness;
				if (bitrate > maximumBandwidth) {
					continue;
				}
				if (lists.size() > 1) {
					double[] temp = ArrayUtils.addAll(qoe, lists.get(0).get(bitrate));
					tempFairness = bruteForceLoop(lists.subList(1, lists.size()), maximumBandwidth-bitrate, temp);
				} else {
					double[] temp = ArrayUtils.addAll(qoe, lists.get(0).get(bitrate));
					tempFairness = getFairness(temp);
				}

				if (tempFairness > maxFairness) {
					maxFairness = tempFairness;
				}
			}
			return maxFairness;
		}

		double getFairness(double... qoe) {
			DescriptiveStatistics data = new DescriptiveStatistics();
			for (double v : qoe) {
				data.addValue(v);
			}
			//double std = data.getStandardDeviation(); // This is for sample std - wrong results
			double std = Math.sqrt(data.getPopulationVariance());
			double h = 0.978;
			double l = 0.742;

			return 0.5 * (1 - ((2*std)/(h-l))) + 0.5 * (data.getSum()/(h*data.getN()));
		}
	}
}
