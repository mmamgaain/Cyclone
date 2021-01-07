package in.mayank.extra.utils;

import java.util.Random;

public class PerlinHeightGenerator {
	
	private float amplitude, roughness = 0.3F;
	private int octaves = 3;
	
	private Random random;
	private int seed;
	
	public PerlinHeightGenerator(float amplitude) {
		this.amplitude = amplitude;
		random = new Random();
		seed = random.nextInt(1000000000);
	}
	
	public float getHeight(int x, int z) {
		return getNoise(x, z) * amplitude;
	}
	
	public float getSmoothHeight(int x, int z) {
		return getSmoothNoise(x, z) * amplitude;
	}
	
	public float getInterpolatedHeight(int x, int z) {
		float total = 0,
			  d = (float)Math.pow(2, octaves - 1);
		for(int i = 0; i < octaves; i++) {
			float freq = (float)(Math.pow(2, i) / d),
				  amp = (float)Math.pow(roughness, i) * amplitude;
			total += getInterpolatedNoise(x * freq, z * freq) * amp;
		}
		
		return total;
	}
	
	public float getRoughness() {
		return roughness;
	}
	
	public void setRoughness(float roughness) {
		this.roughness = roughness;
	}
	
	public int getOctaves() {
		return octaves;
	}
	
	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}
	
	private float getInterpolatedNoise(float x, float z) {
		int intX = (int)x,
			intZ = (int)z;
		float fracX = x - intX,
			  fracZ = z - intZ,
			  v1 = getSmoothNoise(intX, intZ),
			  v2 = getSmoothNoise(intX + 1, intZ),
			  v3 = getSmoothNoise(intX, intZ + 1),
			  v4 = getSmoothNoise(intX + 1, intZ + 1),
			  i1 = interpolate(v1, v2, fracX),
			  i2 = interpolate(v3, v4, fracX);
		
		return interpolate(i1, i2, fracZ);
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float)(1F - Math.cos(theta)) * 0.5F;
		return a * (1F - f) + b * f;
	}
	
	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1))/16F,
			  sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1))/8F,
			  center = getNoise(x, z)/4F;
		
		return corners + sides + center;
	}
	
	private float getNoise(int x, int z) {
		random.setSeed(x * 49632 + z * 32517 + seed);
		return random.nextFloat() * 2F - 1F;
	}
	
}
