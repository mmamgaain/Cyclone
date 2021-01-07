package in.mayank.extra.texture;

public class MaterialTerrain {
	
	private int diffuse = 0, red = -1, green = -1, blue = -1, blendMap = -1;
	private float shineDamper, reflectivity;
	
	public MaterialTerrain(int diffuse) {
		this(diffuse, -1, -1, -1, -1);
	}
	
	public MaterialTerrain(int background, int red, int green, int blue, int blendMap) {
		this.diffuse = background;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.blendMap = blendMap;
		shineDamper = 1;
		reflectivity = 0;
	}
	
	public float getShineDamper() {
		return shineDamper;
	}
	
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	
	public float getReflectivity() {
		return reflectivity;
	}
	
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	public boolean isMultitextured() {
		return red != -1 || green != -1 || blue != -1 || blendMap != -1;
	}
	
	public int getDiffuseTexture() {
		return diffuse;
	}
	
	public MaterialTerrain setDiffuseTexture(int background) {
		this.diffuse = background;
		return this;
	}
	
	public int getRedTexture() {
		return red;
	}
	
	public MaterialTerrain setRedTexture(int red) {
		this.red = red;
		return this;
	}
	
	public int getGreenTexture() {
		return green;
	}
	
	public MaterialTerrain setGreenTexture(int green) {
		this.green = green;
		return this;
	}
	
	public int getBlueTexture() {
		return blue;
	}
	
	public MaterialTerrain setBlueTexture(int blue) {
		this.blue = blue;
		return this;
	}
	
	public int getBlendMapTexture() {
		return blendMap;
	}
	
	public MaterialTerrain setBlendMapTexture(int blendMap) {
		this.blendMap = blendMap;
		return this;
	}
	
}
