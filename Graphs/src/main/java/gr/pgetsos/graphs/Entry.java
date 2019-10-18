package gr.pgetsos.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entry {
	private String name;
	private List<Integer> playingBitrate = new ArrayList<>();
	private List<Integer> bufferState = new ArrayList<>();
	private List<Double> qoeMetrics = new ArrayList<>();
	private Map<Integer, Integer> bufferPerSecond = new HashMap<>();
	private int numberOfInterruptions;
	private int numberOfShortInterruptions;
	private int numberOfLongInterruptions;
	private double meanQoE;
	private double adjustedQoE;

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

	public List<Double> getQoeMetrics() {
		return qoeMetrics;
	}

	public void setQoeMetrics(List<Double> qoeMetrics) {
		this.qoeMetrics = qoeMetrics;
	}

	public int getNumberOfInterruptions() {
		return numberOfInterruptions;
	}

	public void setNumberOfInterruptions(int numberOfInterruptions) {
		this.numberOfInterruptions = numberOfInterruptions;
	}

	public int getNumberOfShortInterruptions() {
		return numberOfShortInterruptions;
	}

	public void setNumberOfShortInterruptions(int numberOfShortInterruptions) {
		this.numberOfShortInterruptions = numberOfShortInterruptions;
	}

	public int getNumberOfLongInterruptions() {
		return numberOfLongInterruptions;
	}

	public void setNumberOfLongInterruptions(int numberOfLongInterruptions) {
		this.numberOfLongInterruptions = numberOfLongInterruptions;
	}

	public double getMeanQoE() {
		return meanQoE;
	}

	public void setMeanQoE(double meanQoE) {
		this.meanQoE = meanQoE;
	}

	public double getAdjustedQoE() {
		return adjustedQoE;
	}

	public void setAdjustedQoE(double adjustedQoE) {
		this.adjustedQoE = adjustedQoE;
	}
}
