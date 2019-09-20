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
					} else if (inputLine.startsWith("Stalling")) {
						onStallingReceived(inputLine);
					} else if (inputLine.startsWith("Quality")) {
						onQualityReceived(inputLine);
					} else if (inputLine.equals("Requesting bandwidth")) {
						onBandwidthRequest();
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

		private void onStallingReceived (String line) {
			String stallString = line.split(":")[1].trim();
			boolean stalled = stallString.equals("true");
			parent.getClients().get(clientIP).setStalled(stalled);
			out.println(RECEIVED);
		}

		private void onBandwidthRequest() {
			if (parent.isStableMode()) {
				double maxAllowed = parent.getCalculatedBandwidth() / parent.getClients().size();
				int qualityBased = bruteForceQuality();
				out.println(RECEIVED + qualityBased);
			} else {
				recalculateBandwidth();
			}
		}

		private void recalculateBandwidth() {
			int max = parent.getCalculatedBandwidth();
			int clientMax = 0;
			ClientInfo client = parent.getClients().get(clientIP);
			for (ClientInfo clientInfo : parent.getClients().values()) {
				clientMax += clientInfo.getLastMeasuredBandwidth();
			}
			if (clientMax > max) {
				max = clientMax;
				parent.setCalculatedBandwidth(max);
				double maxAllowed = parent.getCalculatedBandwidth() / parent.getClients().size();
				out.println(maxAllowed);
			} else if (clientMax < max) {
				if (client.getLastLevel() > client.getLastMeasuredBandwidth()) {
					double maxAllowed = (client.getLastLevel() + client.getLastMeasuredBandwidth()) / 2;
					out.println(maxAllowed);
				} else {
					double maxAllowed = ((parent.getCalculatedBandwidth() / parent.getClients().size()) + client.getLastMeasuredBandwidth()) / 2;
					out.println(maxAllowed * 1.05);
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
				double tempQoe = bruteForceLoop(lists, rest);
				if (tempQoe == -1) {
					continue;
				}
				double t = tempQoe;
				tempQoe += clientMap.get(bitrate);
				if (tempQoe > maxQoE) {
					maxQoE = tempQoe;
					optimalBitrate = bitrate;
				}
			}
			logger.info("Optimal bitrate for total QoE of " + maxQoE + " is " + optimalBitrate);
			return optimalBitrate;
		}

		private double bruteForceLoop(List<Map<Integer, Double>> lists, int maximumBandwidth) {
			double maxQoE = 0;
			if (lists.isEmpty()) {
				return 0;
			}
			for (Integer bitrate : lists.get(0).keySet()) {
				double tempQoE = 0;
				if (bitrate > maximumBandwidth) {
					continue;
				}
				if (lists.size() > 1) {
					double returnedQoE = bruteForceLoop(lists.subList(1, lists.size()), maximumBandwidth-bitrate);
					if (returnedQoE == -1) {
						continue;
					}
					tempQoE = returnedQoE;
				}
				tempQoE += lists.get(0).get(bitrate);
				if (tempQoE > maxQoE) {
					maxQoE = tempQoE;
				}
			}
			return maxQoE;
		}
	}
}
