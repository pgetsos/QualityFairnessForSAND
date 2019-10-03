import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entry {
	private String name;
	private List<Integer> playingBitrate = new ArrayList<>();
	private List<Integer> bufferState = new ArrayList<>();
	private Map<Integer, Integer> bufferPerSecond = new HashMap<>();

	public List<Integer> getPlayingBitrate() {
		return playingBitrate;
	}

	public void setPlayingBitrate(List<Integer> playingBitrate) {
		this.playingBitrate = playingBitrate;
	}

	public List<Integer> getBufferState() {
		return bufferState;
	}

	public void setBufferState(List<Integer> bufferState) {
		this.bufferState = bufferState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Integer, Integer> getBufferPerSecond() {
		return bufferPerSecond;
	}

	public void setBufferPerSecond(Map<Integer, Integer> bufferPerSecond) {
		this.bufferPerSecond = bufferPerSecond;
	}
}
