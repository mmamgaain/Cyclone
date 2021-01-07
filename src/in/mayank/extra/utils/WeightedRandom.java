package in.mayank.extra.utils;

import java.util.ArrayList;
import java.util.List;

public class WeightedRandom {
	
	private List<Prize> prizes;
	private float total = 0F;
	
	public WeightedRandom() {
		prizes = new ArrayList<>();
	}
	
	public void addSegment(float amount) {
		prizes.add(new Prize(amount));
		total += amount;
	}
	
	public void addSegment(String name, float amount) {
		prizes.add(new Prize(name, amount));
		total += amount;
	}
	
	public boolean removeSegment(float amount) {
		int index = 0;
		for(; index < prizes.size(); index++) { if(prizes.get(index).equalsAmount(amount)) break; if(index == prizes.size() - 1) return false; }
		prizes.remove(index);
		return true;
	}
	
	public boolean removeSegment(String name) {
		int index = 0;
		for(; index < prizes.size(); index++) { if(prizes.get(index).equalsName(name)) break; if(index == prizes.size() - 1) return false; }
		prizes.remove(index);
		return true;
	}
	
	public int getRandomIndex() {
		float random = Maths.getRandom(0, total);
		float amount = 0;
		for(int i = 0; i < prizes.size(); i++) {
			amount = prizes.get(i).amount;
			if(random < amount) return i;
			random -= amount;
		}
		return prizes.size() - 1;
	}
	
	public String getRandomName() {
		float random = Maths.getRandom(0, total);
		float amount = 0;
		for(int i = 0; i < prizes.size(); i++) {
			amount = prizes.get(i).amount;
			if(random < amount) return prizes.get(i).name;
			random -= amount;
		}
		return "";
	}
	
}

class Prize {
	
	String name = null;
	float amount = 0;
	
	Prize(float amount) {
		this.amount = amount;
	}
	
	Prize(String name, float amount) {
		this.name = name;
		this.amount = amount;
	}
	
	boolean hasName() {
		return name != null;
	}
	
	boolean equalsName(String name) {
		return !hasName() ? false : this.name.equals(name);
	}
	
	boolean equalsAmount(float amount) {
		return this.amount == amount;
	}
	
}
