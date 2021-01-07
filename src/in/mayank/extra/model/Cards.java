package in.mayank.extra.model;

import org.joml.Vector2f;

import in.mayank.extra.utils.Loader;

public class Cards {
	
	private static RawModel model = null;
	private Vector2f position, scale;
	private int texture;
	
	public Cards(Loader loader) {
		this(new Vector2f(), new Vector2f(1F), loader);
	}
	
	public Cards(Vector2f position, Vector2f scale, Loader loader) {
		this.position = position;
		this.scale = scale;
		if(model == null) model = loader.loadToVAO(new float[] {-1, 1, -1, -1, 1, 1, 1, -1}, 2);
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Cards setPosition(Vector2f position) {
		this.position.set(position);
		return this;
	}
	
	public Cards setPosition(float x, float y) {
		this.position.set(x, y);
		return this;
	}
	
	public static RawModel getModel() {
		return model;
	}
	
	public int getTexture() {
		return texture;
	}
	
	public Cards setTexture(int texture) {
		this.texture = texture;
		return this;
	}
	
	public Vector2f getScale() {
		return scale;
	}
	
	public Cards setScale(Vector2f scale) {
		this.scale.set(scale);
		return this;
	}
	
	public Cards setScale(float x, float y) {
		this.scale.set(x, y);
		return this;
	}
	
}
