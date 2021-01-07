package in.mayank.extra.utils;

import org.joml.Vector3f;

public class Light {
	
	private Vector3f position, color, attenuation;
	
	/** Creates a point light source. */
	public Light() {
		this(new Vector3f(0), new Vector3f(1));
	}
	
	public Light(Vector3f position, Vector3f color) {
		this(position, color, new Vector3f(1, 0, 0));
	}
	
	/** Creates a point light source.
	 * 
	 * @param position The world position of the light.
	 * @param color The color of the light in (R, G, B) scheme. The value are expected to be
	 * 				between zero and one.
	 * @param attenuation The way this light depletes over a distance. A value of (1, 0, 0) produces
	 * 					  no attenuation, i.e., infinite range. The three values correspond to:
	 * <ol>
	 * 	<li>A constant light amount over entire range. The value ranges from 0F to 1F. Supplying a value
	 * 		of under 1F is not generally advisable.</li>
	 * 	<li>A linearly diminishing light amount. The value ranges from 0F to 1F.</li>
	 * 	<li>A quadratically diminishing light amount. The value ranges from 0F to 1F.</li>
	 * </ol> */
	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this.position = position;
		this.color = color;
		this.attenuation = attenuation;
	}
	
	public Light set(Light light) {
		return setPosition(light.position).setColor(light.color).setAttenuation(light.attenuation);
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Light setPosition(Vector3f position) {
		this.position.set(position);
		return this;
	}
	
	public Light setPosition(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}
	
	public Light increasePosition(Vector3f position) {
		this.position.add(position);
		return this;
	}
	
	public Light increasePosition(float x, float y, float z) {
		position.add(x, y, z);
		return this;
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public Light setColor(Vector3f color) {
		this.color.set(color);
		return this;
	}
	
	public Light setColor(float x, float y, float z) {
		color.set(x, y, z);
		return this;
	}
	
	public Light increaseColor(Vector3f color) {
		this.color.add(color);
		return this;
	}
	
	public Light increaseColor(float x, float y, float z) {
		color.add(x, y, z);
		return this;
	}
	
	public Vector3f getAttenuation() {
		return attenuation;
	}
	
	public Light setAttenuation(Vector3f attenuation) {
		this.attenuation.set(attenuation);
		return this;
	}
	
	public Light setAttenuation(float attX, float attY, float attZ) {
		attenuation.set(attX, attY, attZ);
		return this;
	}
	
	public Light increaseAttenuation(Vector3f deltaAtt) {
		this.attenuation.add(deltaAtt);
		return this;
	}
	
	public Light increaseAttenuation(float x, float y, float z) {
		attenuation.add(x, y, z);
		return this;
	}
	
}
