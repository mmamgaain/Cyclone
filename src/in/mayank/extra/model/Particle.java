package in.mayank.extra.model;

import org.joml.Vector3f;

import in.mayank.extra.core.Core;
import in.mayank.extra.utils.Maths;

public class Particle {
	
	private Vector3f position, velocity, scale;
	private float gravityEffect, lifeLength, rotation, elapsedTime = 0F;
	
	private Vector3f color = new Vector3f(1F);
	
	public Particle(Vector3f position, Vector3f velocity, Vector3f scale, float gravityEffect, float lifeLength, float rotation) {
		this.position = position;
		this.velocity = velocity;
		this.scale = scale;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
	}
	
	public Particle setColor(Vector3f color) { this.color.set(color); return this; }
	
	public Particle setColor(float red, float green, float blue) { color.set(red, green, blue); return this; }
	
	public Vector3f getColor() { return color; }
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public boolean update() {
		velocity.y -= Maths.GRAVITY_3D.y * gravityEffect * (float)Core.getDeltaTimeInSeconds();
		Vector3f changes = new Vector3f(velocity);
		changes.mul((float)Core.getDeltaTimeInSeconds());
		position.add(changes);
		elapsedTime += (float)Core.getDeltaTimeInSeconds();
		
		return elapsedTime < lifeLength;
	}
	
}
