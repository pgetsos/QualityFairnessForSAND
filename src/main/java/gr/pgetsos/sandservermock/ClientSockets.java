package gr.pgetsos.sandservermock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientSockets {
	private static final Logger logger = LogManager.getLogger("gr.pgetsos.SANDServerMock.ClientSockets");
	private static final String RECEIVED = "Received";

	private ConcurrentHashMap<String, ClientInfo> clients = new ConcurrentHashMap<>(3);
	private float calculatedBandwidth;
	private boolean stableMode;
	private boolean fake;

	private ServerSocket serverSocket;

	public ClientSockets(float calculatedBandwidth, boolean stableMode) {
		this.calculatedBandwidth = calculatedBandwidth;
		this.stableMode = stableMode;
	}

	public void start(int port) {
		try {
			logger.debug("Starting socket for incoming connections");
			serverSocket = new ServerSocket(port);
			//noinspection InfiniteLoopStatement
			while (true) {
				new EchoClientHandler(this, serverSocket.accept()).start();
				logger.debug("New connection!");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public ConcurrentHashMap<String, ClientInfo> getClients() {
		return clients;
	}

	public void setClients(ConcurrentHashMap<String, ClientInfo> clients) {
		this.clients = clients;
	}

	public float getCalculatedBandwidth() {
		return calculatedBandwidth;
	}

	public void setCalculatedBandwidth(float calculatedBandwidth) {
		this.calculatedBandwidth = calculatedBandwidth;
	}

	public boolean isStableMode() {
		return stableMode;
	}

	public void setStableMode(boolean stableMode) {
		this.stableMode = stableMode;
	}

	public boolean isFake() {
		return fake;
	}

	public void setFake(boolean fake) {
		this.fake = fake;
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
					} else if (inputLine.startsWith("Bandwidth")) {
						onBandwidthReceived(inputLine);
					} else if (inputLine.startsWith("Level")) {
						onLevelReceived(inputLine);
					} else if (inputLine.startsWith("Stalling")) {
						onStallingReceived(inputLine);
					} else if (inputLine.equals("Requesting bandwidth")) {
						onBandwidthRequest();
					}
					logger.debug(inputLine);
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
			}
			out.println(RECEIVED);
		}

		private void onBandwidthReceived (String line) {
			float bandwidth = Float.parseFloat(line.split(":")[1].trim());
			parent.getClients().get(clientIP).setLastMeasuredBandwidth(bandwidth);
			out.println(RECEIVED);
		}

		private void onLevelReceived (String line) {
			float level = Float.parseFloat(line.split(":")[1].trim());
			parent.getClients().get(clientIP).setLastLevel(level);
			out.println(RECEIVED);
		}

		private void onStallingReceived (String line) {
			String stallString = line.split(":")[1].trim();
			boolean stalled = stallString.equals("true");
			parent.getClients().get(clientIP).setStalled(stalled);
			out.println(RECEIVED);
		}

		private void onBandwidthRequest() {
			if (parent.isFake()) {
				switch (clientIP) {
					case "192.168.1.1":
						out.println(RECEIVED + 200);
						break;
					case "192.168.1.2":
						out.println(RECEIVED + 600);
						break;
					default:
						out.println(RECEIVED + 1200);
						break;
				}
				return;
			}
			if (parent.isStableMode()) {
				float maxAllowed = parent.getCalculatedBandwidth() / parent.getClients().size();
				out.println(RECEIVED + maxAllowed);
			} else {
				recalculateBandwidth();
			}
		}

		private void recalculateBandwidth() {
			float max = parent.getCalculatedBandwidth();
			float clientMax = 0;
			ClientInfo client = parent.getClients().get(clientIP);
			for (ClientInfo clientInfo : parent.getClients().values()) {
				clientMax += clientInfo.getLastMeasuredBandwidth();
			}
			if (clientMax > max) {
				max = clientMax;
				parent.setCalculatedBandwidth(max);
				float maxAllowed = parent.getCalculatedBandwidth() / parent.getClients().size();
				out.println(maxAllowed);
			} else if (clientMax < max) {
				if (client.getLastLevel() > client.getLastMeasuredBandwidth()) {
					float maxAllowed = (client.getLastLevel() + client.getLastMeasuredBandwidth()) / 2;
					out.println(maxAllowed);
				} else {
					float maxAllowed = parent.getCalculatedBandwidth() / parent.getClients().size();
					out.println(maxAllowed * 1.05);
				}
			}
		}
	}
}
