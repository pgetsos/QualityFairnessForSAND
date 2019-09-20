package gr.pgetsos.sandservermock;

import java.util.Map;

public class ClientInfo {
	String ipAddress;
	int lastMeasuredBandwidth;
	int lastLevel;
	double qualityScore;
	int minimumBandwidth;
	int sharingWith;
	boolean stalled;
	Map<Integer, Double> qualityList;

	public ClientInfo() { }

	public ClientInfo(String ipAddress, int lastMeasuredBandwidth, int lastLevel, double qualityScore, int minimumBandwidth, int sharingWith, boolean stalled, Map<Integer, Double> qualityList) {
		this.ipAddress = ipAddress;
		this.lastMeasuredBandwidth = lastMeasuredBandwidth;
		this.lastLevel = lastLevel;
		this.qualityScore = qualityScore;
		this.minimumBandwidth = minimumBandwidth;
		this.sharingWith = sharingWith;
		this.stalled = stalled;
		this.qualityList = qualityList;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getLastMeasuredBandwidth() {
		return lastMeasuredBandwidth;
	}

	public void setLastMeasuredBandwidth(int lastMeasuredBandwidth) {
		this.lastMeasuredBandwidth = lastMeasuredBandwidth;
	}

	public int getLastLevel() {
		return lastLevel;
	}

	public void setLastLevel(int lastLevel) {
		this.lastLevel = lastLevel;
	}

	public double getQualityScore() {
		return qualityScore;
	}

	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
	}

	public int getMinimumBandwidth() {
		return minimumBandwidth;
	}

	public void setMinimumBandwidth(int minimumBandwidth) {
		this.minimumBandwidth = minimumBandwidth;
	}

	public int getSharingWith() {
		return sharingWith;
	}

	public void setSharingWith(int sharingWith) {
		this.sharingWith = sharingWith;
	}

	public boolean isStalled() {
		return stalled;
	}

	public void setStalled(boolean stalled) {
		this.stalled = stalled;
	}

	public Map<Integer, Double> getQualityList() {
		return qualityList;
	}

	public void setQualityList(Map<Integer, Double> qualityList) {
		this.qualityList = qualityList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ClientInfo that = (ClientInfo) o;

		return ipAddress.equals(that.ipAddress);
	}

	@Override
	public int hashCode() {
		return ipAddress.hashCode();
	}
}
