package in.mayank.extra.model;

import org.joml.Vector2f;
import org.joml.Vector3f;

import in.mayank.extra.core.Core;

public class WaterTile {
	
	private float x, z, height, sizeX, sizeZ, oss = 0;
	private int distortionMap = 0, normalMap = 0;
	private float waveStrength = 1, clarity = 1, shineDamper = 1, reflectivity = 0, maxDistance = 5, tiling = 6, chaos = 0.1F;
	private Vector2f waveSpeed = new Vector2f(), moveFactor = new Vector2f();
	private Vector3f waterColor = new Vector3f();
	
	public WaterTile(float centerX, float centerZ, float height) {
		x = centerX;
		z = centerZ;
		sizeX = 60;
		sizeZ = 60;
		this.height = height;
	}
	
	public WaterTile(float centerX, float centerZ, float sizeX, float sizeZ, float height) {
		x = centerX;
		z = centerZ;
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		this.height = height;
	}
	
	public WaterTile(float centerX, float centerZ, float sizeX, float sizeZ, float height, float oscillations) {
		x = centerX;
		z = centerZ;
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		this.height = height;
		oss = oscillations;
	}
	
	public WaterTile update() {
		moveFactor.fma((float)Core.getDeltaTimeInSeconds(), waveSpeed);
		moveFactor.x %= 1;
		moveFactor.y %= 1;
		height += Math.sin(Core.getElapsedTimeInSeconds()) * oss;
		return this;
	}
	
	public Vector3f getWaterColor() {
		return waterColor;
	}
	
	public WaterTile setWaterColor(Vector3f color) {
		waterColor.set(color);
		return this;
	}
	
	public WaterTile setDistortionMap(int distortionMap) {
		this.distortionMap = distortionMap;
		return this;
	}
	
	public int getDistortionMap() {
		return distortionMap;
	}
	
	public boolean hasDistortionMap() {
		return distortionMap > 0;
	}
	
	public WaterTile setNormalMap(int normalMap) {
		this.normalMap = normalMap;
		return this;
	}
	
	public int getNormalMap() {
		return normalMap;
	}
	
	public boolean hasNormalMap() {
		return normalMap > 0;
	}
	
	public WaterTile setTiling(float tiling) {
		this.tiling = tiling;
		return this;
	}
	
	public float getTiling() {
		return tiling;
	}
	
	public float getSizeX() {
		return sizeX;
	}
	
	public WaterTile setSizeX(float sizeX) {
		this.sizeX = sizeX;
		return this;
	}
	
	public float getSizeZ() {
		return sizeZ;
	}
	
	public WaterTile setSizeZ(float sizeZ) {
		this.sizeZ = sizeZ;
		return this;
	}
	
	public float getX() {
		return x;
	}
	
	public float getZ() {
		return z;
	}
	
	public float getHeight() {
		return height;
	}
	
	public WaterTile changeHeight(float change) {
		height += change;
		return this;
	}
	
	public float getOsscilation() {
		return oss;
	}
	
	public WaterTile setOsscilation(float oss) {
		this.oss = oss;
		return this;
	}

	public float getWaveStrength() {
		return waveStrength;
	}

	public WaterTile setWaveVariables(float waveStrength, Vector2f waveSpeed, float chaos) {
		this.waveStrength = waveStrength;
		this.chaos = chaos;
		this.waveSpeed.set(waveSpeed);
		return this;
	}
	
	public float getDistortionChaos() {
		return chaos;
	}
	
	public Vector2f getWaveSpeed() {
		return waveSpeed;
	}

	public float getClarity() {
		return clarity;
	}

	public WaterTile setClarity(float clarity) {
		this.clarity = clarity;
		return this;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public WaterTile setShineVariables(float shineDamper, float reflectivity) {
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
		return this;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public WaterTile setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
		return this;
	}

	public Vector2f getMoveFactor() {
		return moveFactor;
	}

	public WaterTile setHeight(float height) {
		this.height = height;
		return this;
	}
	
}
