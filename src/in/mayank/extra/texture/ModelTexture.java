package in.mayank.extra.texture;

import java.util.Objects;

public class ModelTexture {
	
	private int texture, rows;
	private float shineDamper, reflectivity;
	private boolean hasTransparency;
	
	public ModelTexture(int texture) {
		this(texture, 1);
	}
	
	public ModelTexture(int texture, int rows) {
		this.texture = texture;
		this.rows = rows;
		shineDamper = 1;
		reflectivity = 0;
		hasTransparency = false;
	}
	
	public ModelTexture setShineValues(float shineDamper, float reflectivity) {
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
		return this;
	}
	
	public ModelTexture changeShineValues(float shineDamper, float reflectivity) {
		this.shineDamper += shineDamper;
		this.reflectivity += reflectivity;
		return this;
	}
	
	public float getShineDamper() {
		return shineDamper;
	}
	
	public float getReflectivity() {
		return reflectivity;
	}
	
	public ModelTexture hasTransparency(boolean transparency) {
		hasTransparency = transparency;
		return this;
	}
	
	public boolean isHasTransparency() {
		return hasTransparency;
	}
	
	public int getID() {
		return texture;
	}
	
	public int getNumberOfRows() {
		return rows;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(hasTransparency, reflectivity, rows, shineDamper, texture);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ModelTexture))
			return false;
		ModelTexture other = (ModelTexture) obj;
		return hasTransparency == other.hasTransparency
				&& Float.floatToIntBits(reflectivity) == Float.floatToIntBits(other.reflectivity) && rows == other.rows
				&& Float.floatToIntBits(shineDamper) == Float.floatToIntBits(other.shineDamper)
				&& texture == other.texture;
	}
	
}
