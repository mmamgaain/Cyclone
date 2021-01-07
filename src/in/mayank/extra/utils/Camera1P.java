package in.mayank.extra.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera1P {
	
	private Vector3f position = new Vector3f();
	private float yaw, roll, pitch;
	
	public Camera1P() {
		this(new Vector3f(), 0, 0, 0);
	}
	
	public Camera1P(Vector3f position, float pitch, float yaw, float roll) {
		this.position.set(position);
		this.yaw = yaw;
		this.roll = roll;
		this.pitch = pitch;
	}
	
	public Camera1P changePosition(Vector3f deltaPos) {
		position.add(deltaPos);
		return this;
	}
	
	public Camera1P changePosition(float deltaX, float deltaY, float deltaZ) {
		position.add(deltaX, deltaY, deltaZ);
		return this;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Camera1P setPosition(Vector3f position) {
		this.position.set(position);
		return this;
	}
	
	public Camera1P setPosition(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float invertYaw() {
		return yaw = -yaw;
	}
	
	public float getInvertYaw() {
		return -yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float invertPitch() {
		return pitch = -pitch;
	}
	
	public float getInvertPitch() {
		return -pitch;
	}

	public float getRoll() {
		return roll;
	}
	
	public float invertRoll() {
		return roll = -roll;
	}
	
	public float getInvertRoll() {
		return -roll;
	}

	public Camera1P setYaw(float yaw) {
		this.yaw = yaw;
		return this;
	}
	
	public Camera1P changeYaw(float deltaYaw) {
		yaw += deltaYaw;
		return this;
	}

	public Camera1P setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}
	
	public Camera1P changePitch(float deltaPitch) {
		pitch += deltaPitch;
		return this;
	}

	public Camera1P setRoll(float roll) {
		this.roll = roll;
		return this;
	}
	
	public Camera1P changeRoll(float deltaRoll) {
		roll += deltaRoll;
		return this;
	}
	
	public Camera1P setRotation(float pitch, float yaw, float roll) {
		this.yaw = yaw;
		this.roll = roll;
		this.pitch = pitch;
		return this;
	}
	
	public Camera1P changeRotation(float deltaPitch, float deltaYaw, float deltaRoll) {
		yaw += deltaYaw;
		roll += deltaRoll;
		pitch += deltaPitch;
		return this;
	}
	
	public Camera1P moveForward(float factor) {
		position.x += (factor * Math.sin(yaw));
		position.z -= (factor * Math.cos(yaw));
		return this;
	}
	
	public Camera1P moveBackward(float factor) {
		position.x -= (factor * Math.sin(yaw));
		position.z += (factor * Math.cos(yaw));
		return this;
	}
	
	public Camera1P moveLeft(float factor) {
		position.x -= (factor * Math.cos(yaw));
		position.z -= (factor * Math.sin(yaw));
		return this;
	}
	
	public Camera1P moveRight(float factor) {
		position.x += (factor * Math.cos(yaw));
		position.z += (factor * Math.sin(yaw));
		return this;
	}
	
	public Matrix4f getViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();
		
		viewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
		viewMatrix.rotate(yaw, new Vector3f(0, 1, 0));
		viewMatrix.rotate(roll, new Vector3f(0, 0, 1));
		viewMatrix.translate(-position.x, -position.y, -position.z);
		
		return viewMatrix;
	}
	
	public Matrix4f getViewMatrix(Matrix4f view) {
		view.rotate(pitch, new Vector3f(1, 0, 0));
		view.rotate(yaw, new Vector3f(0, 1, 0));
		view.rotate(roll, new Vector3f(0, 0, 1));
		view.translate(-position.x, -position.y, -position.z);
		
		return view;
	}
	
}
