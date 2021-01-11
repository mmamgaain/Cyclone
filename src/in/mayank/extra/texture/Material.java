package in.mayank.extra.texture;

import java.util.Objects;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import in.mayank.extra.utils.Maths;

public class Material {
	
	public static final int NO_TEXTURE = 0;
	
	private final Vector3f diffuse = new Vector3f(1), ambient = new Vector3f(1), specular = new Vector3f(1);
	private int texture = NO_TEXTURE, normalMap = NO_TEXTURE, specularMap = NO_TEXTURE;
	private float shineDamper = 1, specularReflectivity = 0, transparency = 0F, enviroRefractivity = 0.5F, fresnelPower = 2;
	private boolean isDoubleSided = false, hasFresnel = true;
	private final Vector2f rows = new Vector2f(1);
	
	public Material(final Vector3f diffuse, final Vector3f ambient, final Vector3f specular) {
		this.diffuse.set(diffuse);
		this.ambient.set(ambient);
		this.specular.set(specular);
	}
	
	public Material(final Vector3i diffuse, final Vector3i ambient, final Vector3i specular) {
		float scale = 1F / 255;
		this.diffuse.set(diffuse).mul(scale);
		this.ambient.set(ambient).mul(scale);
		this.specular.set(specular).mul(scale);
	}
	
	public Material(final int texture) { this.texture = texture; }
	
	public Material(final int texture, final Vector2f rows) {
		this.texture = texture;
		this.rows.set(rows);
	}
	
	public Material(final Material material) { set(material); }
	
	public void set(Material mat) {
		diffuse.set(mat.diffuse);
		ambient.set(mat.ambient);
		specular.set(mat.specular);
		texture = mat.texture;
		normalMap = mat.normalMap;
		specularMap = mat.specularMap;
		shineDamper = mat.shineDamper;
		specularReflectivity = mat.specularReflectivity;
		transparency = mat.transparency;
		enviroRefractivity = mat.enviroRefractivity;
		isDoubleSided = mat.isDoubleSided;
		rows.set(mat.rows);
	}
	
	/** Sets the transparency of this material. If this value is set to zero(default),
	 * then this material is designated as completely opaque.
	 * 
	 * @param transparency  */
	public Material setTransparency(final float transparency) { this.transparency = Maths.clamp(transparency, 0, 1); return this; }
	
	/** Gets the level of transparency for this material.
	 * 
	 * @return The value of this material's transparency represented from
	 * 0(completely opaque - default) to 1(completely transparent). */
	public float getTransparency() { return transparency; }
	
	public boolean hasTransparency() { return transparency > 0F; }
	
	public Material setShineValues(final float shineDamper, final float reflectivity) {
		this.shineDamper = shineDamper;
		this.specularReflectivity = reflectivity;
		return this;
	}
	
	public Material changeShineValues(final float shineDamper, final float reflectivity) {
		this.shineDamper += shineDamper;
		this.specularReflectivity += reflectivity;
		return this;
	}
	
	public Material setDiffuseColor(final Vector3f diffuse) { this.diffuse.set(diffuse); return this; }
	
	public Vector3f getDiffuseColor() { return diffuse; }
	
	public Material setAmbientColor(final Vector3f ambient) { this.ambient.set(ambient); return this; }
	
	public Vector3f getAmbientColor() { return ambient; }
	
	public Material setSpecularColor(final Vector3f specular) { this.specular.set(specular); return this; }
	
	public Vector3f getSpecularColor() { return specular; }
	
	public Material setColor(final Vector3f ambient, final Vector3f diffuse, final Vector3f specular) {
		this.ambient.set(ambient);
		this.diffuse.set(diffuse);
		this.specular.set(specular);
		return this;
	}
	
	public Material setSpecularMap(final int specular) { this.specularMap = specular; return this; }
	
	public int getSpecularMap() { return specularMap; }
	
	public boolean hasSpecularMap() { return specularMap > NO_TEXTURE; }
	
	public Material setNormalMap(final int normal) { this.normalMap = normal; return this; }
	
	public int getNormalMap() { return normalMap; }
	
	public boolean hasNormalMap() { return normalMap > NO_TEXTURE; }
	
	public float getShineDamper() { return shineDamper; }
	
	public float getSpecularReflectivity() { return specularReflectivity; }
	
	/** Sets the reflectivity over refractivity ratio for a transparent material.
	 * If this material has not been designated as reflective, this value doesn't matter.
	 * 
	 * @param refractivity reflectivity over refractivity ratio for a transparent material.
	 * The default value is 1. The effect of this value changes exponentially. A value between
	 * zero and one favours refractivity and greater values favour reflectivity.
	 * 
	 * @see #setTransparency(float) */
	public Material setEnviroRefractivity(final float refractivity) { enviroRefractivity = Maths.max(refractivity, 0); return this; }
	
	public float getEnviroRefractivity() { return enviroRefractivity; }
	
	public Material setIsDoubleSided(final boolean doubleSided) { this.isDoubleSided = doubleSided; return this; }
	
	public Material setDiffuseTexture(final int texture) { this.texture = texture; return this; }
	
	public boolean hasDiffuseTexture() { return texture > NO_TEXTURE; }
	
	public boolean isDoubleSided() { return isDoubleSided; }
	
	public int getDiffuseTexture() { return texture; }
	
	public Vector2f getNumberOfRows() { return rows; }
	
	public float getFresnelPower() { return fresnelPower; }
	
	public Material setFresnelPower(final float fresnelPower) { this.fresnelPower = fresnelPower; return this; }
	
	public boolean hasFresnel() { return hasFresnel; }
	
	public Material setHasFresnel(final boolean hasFresnel) { this.hasFresnel = hasFresnel; return this; }
	
	@Override
	public int hashCode() { return Objects.hash(ambient, diffuse, isDoubleSided, normalMap, rows, specular, specularMap, texture, shineDamper, specularReflectivity, enviroRefractivity); }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Material)) return false;
		Material other = (Material) obj;
		return Objects.equals(ambient, other.ambient) && Objects.equals(diffuse, other.diffuse)
				&& isDoubleSided == other.isDoubleSided && normalMap == other.normalMap && rows == other.rows
				&& Objects.equals(specular, other.specular) && specularMap == other.specularMap
				&& texture == other.texture && shineDamper == other.shineDamper && specularReflectivity == other.specularReflectivity
				&& enviroRefractivity == other.enviroRefractivity;
	}
	
}
