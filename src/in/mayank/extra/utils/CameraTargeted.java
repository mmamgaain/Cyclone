package in.mayank.extra.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraTargeted {
	
	private static final Vector3f UP = new Vector3f(0, 1, 0);
	
	private Vector3f position, direction, up, right;
	
	public CameraTargeted(Vector3f position, Vector3f target) {
		this.position = position;
		
		// Direction calculation
		direction = new Vector3f();
		this.position.sub(target, direction);
		
		// Right calculation
		right = new Vector3f();
		
		// Up calculation
		up = new Vector3f();
	}
	
	public void update() {
		direction.normalize();
		
		UP.cross(direction, right);
		right.normalize();
		
		direction.cross(right, up);
	}
	
	/*public Matrix4f getViewMatrix() {
		Matrix4f result = new Matrix4f(),
				 a = new Matrix4f(right.x, 	   right.y, 	right.z, 	 0,
						 		  up.x,    	   up.y, 		up.z, 	 	 0,
						 		  direction.x, direction.y, direction.z, 0,
						 		  0, 		   0, 			0, 			 1),
				 b = new Matrix4f();
		
		return result;
	}*/
	
	public Matrix4f getViewMatrix() {
		return new Matrix4f().lookAt(position, direction, up);
	}
	
}
