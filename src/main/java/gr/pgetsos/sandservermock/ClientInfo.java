package gr.pgetsos.sandservermock;

public class ClientInfo {
	String ipAddress;
	float lastMeasuredBandwidth;
	float lastLevel;
	int sharingWith;
	boolean stalled;

	public ClientInfo() { }

	public ClientInfo(String ipAddress, float lastMeasuredBandwidth, float lastLevel, int sharingWith, boolean stalled) {
		this.ipAddress = ipAddress;
		this.lastMeasuredBandwidth = lastMeasuredBandwidth;
		this.lastLevel = lastLevel;
		this.sharingWith = sharingWith;
		this.stalled = stalled;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public float getLastMeasuredBandwidth() {
		return lastMeasuredBandwidth;
	}

	public void setLastMeasuredBandwidth(float lastMeasuredBandwidth) {
		this.lastMeasuredBandwidth = lastMeasuredBandwidth;
	}

	public float getLastLevel() {
		return lastLevel;
	}

	public void setLastLevel(float lastLevel) {
		this.lastLevel = lastLevel;
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
