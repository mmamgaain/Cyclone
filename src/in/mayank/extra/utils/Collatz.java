package in.mayank.extra.utils;

import java.util.List;

public class Collatz {
	
	private long startingValue, maxValue;
	private List<Long> values;
	
	public Collatz(List<Long> values, long value, long maxValue) {
		this.values = values;
		this.startingValue = value;
		this.maxValue = maxValue;
	}
	
	public long getStartingValue() {
		return startingValue;
	}
	
	public long getMaxValue() {
		return maxValue;
	}
	
	public List<Long> getValues(){
		return values;
	}
	
	public int getNumValues() {
		return values.size();
	}
	
}
