package in.mayank.extra.model;

import org.joml.Vector3f;

public class Entity extends TexturedModel {
	
	private Vector3f position, rotation, scale;
	private Vector3f dPosition, dRotation, dScale;
	
	public Entity(TexturedModel model) {
		this(model, new Vector3f(), new Vector3f(), new Vector3f(1));
	}
	
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(model.getModel(), model.material, model.textureIndex);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		dPosition = new Vector3f();
		dRotation = new Vector3f();
		dScale = new Vector3f();
	}
	
	public Entity update() {
		position.add(dPosition);
		rotation.add(dRotation);
		scale.add(dScale);
		return this;
	}
	
	public Entity setNormalMap(int normal) {
		material.setNormalMap(normal);
		return this;
	}
	
	public int getNormalMap() {
		return material.getNormalMap();
	}
	
	public boolean hasNormalMap() {
		return material.hasNormalMap();
	}
	
	public Entity setTextureIndex(int index) {
		textureIndex = index;
		return this;
	}
	
	public TexturedModel getTexturedModel() {
		return (TexturedModel)this;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public Entity setPosition(Vector3f position) {
		this.position.set(position);
		return this;
	}
	
	public Entity setPosition(float deltaX, float deltaY, float deltaZ) {
		position.set(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Entity increasePosition(Vector3f position) {
		this.position.add(position);
		return this;
	}
	
	public Entity increasePosition(float deltaX, float deltaY, float deltaZ) {
		position.add(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Entity addPosition(Vector3f position) {
		dPosition.add(position);
		return this;
	}
	
	public Entity addPosition(float dx, float dy, float dz) {
		dPosition.add(dx, dy, dz);
		return this;
	}
	
	public Entity setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
		return this;
	}
	
	public Entity setRotation(float deltaX, float deltaY, float deltaZ) {
		rotation.set(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Entity increaseRotation(Vector3f rotation) {
		this.rotation.add(rotation);
		return this;
	}
	
	public Entity increaseRotation(float deltaX, float deltaY, float deltaZ) {
		rotation.add(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Entity addRotation(Vector3f rotation) {
		dRotation.add(rotation);
		return this;
	}
	
	public Entity addRotation(float dx, float dy, float dz) {
		dRotation.add(dx, dy, dz);
		return this;
	}
	
	public Entity setScale(Vector3f scale) {
		this.scale.set(scale);
		return this;
	}
	
	public Entity setScale(float deltaX, float deltaY, float deltaZ) {
		scale.set(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Entity increaseScale(Vector3f scale) {
		this.scale.add(scale);
		return this;
	}
	
	public Entity increaseScale(float deltaX, float deltaY, float deltaZ) {
		scale.add(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Entity addScale(Vector3f scale) {
		dScale.add(scale);
		return this;
	}
	
	public Entity addScale(float dx, float dy, float dz) {
		dScale.add(dx, dy, dz);
		return this;
	}
	
}
