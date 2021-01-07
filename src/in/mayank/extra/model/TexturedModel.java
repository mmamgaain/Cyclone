package in.mayank.extra.model;

import java.util.Objects;

import org.joml.Vector2f;

import in.mayank.extra.texture.Material;

public class TexturedModel extends RawModel {
	
	protected Material material;
	protected int textureIndex;
	
	public TexturedModel(RawModel model, Material material) {
		this(model, material, 0);
	}
	
	public TexturedModel(RawModel model, Material material, int textureIndex) {
		super(model.vaoID, model.vertexCount);
		this.material = material;
		this.textureIndex = textureIndex < 0 ? 0 : textureIndex;
	}
	
	public Vector2f getTextureOffset() {
		Vector2f rows = material.getNumberOfRows();
		return new Vector2f(((float)(textureIndex % rows.x) / rows.x), ((float)Math.floor(textureIndex / rows.y) / rows.y));
	}
	
	public float getTextureXOffset() {
		float rows = material.getNumberOfRows().x;
		return ((textureIndex % rows) / rows);
	}
	
	public float getTextureYOffset() {
		float rows = material.getNumberOfRows().y;
		return ((textureIndex / rows) / rows);
	}
	
	public float getShineDamper() {
		return material.getShineDamper();
	}
	
	public float getReflectivity() {
		return material.getSpecularReflectivity();
	}
	
	public int getTexture() {
		return material.getDiffuseTexture();
	}
	
	public int getNormalMap() {
		return material.getNormalMap();
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public RawModel getModel() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(material);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof TexturedModel))
			return false;
		TexturedModel other = (TexturedModel) obj;
		return Objects.equals(material, other.material);
	}
	
}