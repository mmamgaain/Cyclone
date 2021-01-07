package in.mayank.extra.shadow;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import in.mayank.extra.core.Core;
import in.mayank.extra.utils.Camera1P;

/** Represents the 3D cuboidal area of the world in which objects will cast
 * shadows (basically represents the orthographic projection area for the shadow
 * render pass). It is updated each frame to optimise the area, making it as
 * small as possible (to allow for optimal shadow map resolution) while not
 * being too small to avoid objects not having shadows when they should.
 * Everything inside the cuboidal area represented by this object will be
 * rendered to the shadow map in the shadow render pass. Everything outside the
 * area won't be. */
public class ShadowBox {
	
	private static final float OFFSET = 15,
							   SHADOW_DISTANCE = 150;
	private static final Vector4f UP = new Vector4f(0, 1, 0, 0),
								  FORWARD = new Vector4f(0, 0, -1, 0);
	
	private float minX, maxX, minY, maxY, minZ, maxZ, farHeight, farWidth, nearHeight, nearWidth;
	private Matrix4f lightViewMatrix;
	private Camera1P camera;
	
	 /** Creates a new shadow box and calculates some initial values relating to
     * the camera's view frustum, namely the width and height of the near plane
     * and (possibly adjusted) far plane.
     * 
     * @param lightViewMatrix Basically the "view matrix" of the light. Can be used to
     *            transform a point from world space into "light" space (i.e.
     *            changes a point's coordinates from being in relation to the
     *            world's axis to being in terms of the light's local axis).
     * @param camera The in-game camera. */
	public ShadowBox(Matrix4f lightViewMatrix, Camera1P camera) {
		this.lightViewMatrix = lightViewMatrix;
		this.camera = camera;
		calculateWidthsAndHeights();
	}
	
	/** Updates the bounds of the shadow box based on the light direction and the
     * camera's view frustum, to make sure that the box covers the smallest area
     * possible while still ensuring that everything inside the camera's view
     * (within a certain range) will cast shadows. */
	public void update() {
		Matrix4f rotation = calculateCameraRotationMatrix();
		Vector4f forward = new Vector4f();
		rotation.transform(FORWARD, forward);
		Vector3f forwardVector = new Vector3f(forward.x, forward.y, forward.z);
		
		Vector3f toFar = new Vector3f(forwardVector),
				 toNear = new Vector3f(forwardVector);
		toFar.normalize(SHADOW_DISTANCE);
		toNear.normalize(Core.getNearValue());
		/*toFar.mul(SHADOW_DISTANCE);
		toNear.mul(Core.getNearValue());*/
		Vector3f centerNear = new Vector3f(),
				 centerFar = new Vector3f();
		toNear.add(camera.getPosition(), centerNear);
		toFar.add(camera.getPosition(), centerFar);
		
		Vector4f[] points = calculateFrustrumVertices(rotation, forwardVector, centerNear, centerFar);
		
		boolean first = true;
		for(Vector4f point : points) {
			if(first) {
				minX = point.x;
				maxX = point.x;
				minY = point.y;
				maxY = point.y;
				minZ = point.z;
				maxZ = point.z;
				first = false;
				continue;
			}
			if(point.x > maxX) maxX = point.x;
			else if (point.x < minX) minX = point.x;
			
			if(point.y > maxY) maxY = point.y;
			else if (point.y < minY) minY = point.y;
			
			if(point.z > maxZ) maxZ = point.z;
			else if (point.z < minZ) minZ = point.z;
		}
		maxZ += OFFSET;
	}
	
	 /** Calculates the center of the "view cuboid" in light space first, and then
     * converts this to world space using the inverse light's view matrix.
     * 
     * @return The center of the "view cuboid" in world space. */
	public Vector3f getCenter() {
		float x = (minX + maxX) / 2f,
			  y = (minY + maxY) / 2f,
			  z = (minZ + maxZ) / 2f;
		Vector4f cen = new Vector4f(x, y, z, 1);
		Matrix4f invertedLight = new Matrix4f();
		lightViewMatrix.invert(invertedLight);
		Vector4f center = invertedLight.transform(cen);
		return new Vector3f(center.x, center.y, center.z);
	}
	
	/** @return The width of the "view cuboid" (orthographic projection area). */
	public float getWidth() {
		return maxX - minX;
	}
	
	/** @return The height of the "view cuboid" (orthographic projection area). */
	public float getHeight() {
		return maxY - minY;
	}
	
	/** @return The length of the "view cuboid" (orthographic projection area). */
	public float getLength() {
		return maxZ - minZ;
	}
	
	/***/
	public float getShadowDistance() {
		return SHADOW_DISTANCE;
	}
	
	 /** Calculates the position of the vertex at each corner of the view frustum
     * in light space (8 vertices in total, so this returns 8 positions).
     * 
     * @param rotation Camera's rotation.
     * @param forwardVector The direction that the camera is aiming, and thus the
     *            direction of the frustum.
     * @param centerNear The center point of the frustum's near plane.
     * @param centerFar The center point of the frustum's (possibly adjusted) far
     *            plane.
     * @return The positions of the vertices of the frustum in light space. */
	private Vector4f[] calculateFrustrumVertices(Matrix4f rotation, Vector3f forwardVector, Vector3f centerNear, Vector3f centerFar) {
		Vector4f up = new Vector4f();
		rotation.transform(UP, up);
		Vector3f upVector = new Vector3f(up.x, up.y, up.z),
				 rightVector = forwardVector.cross(upVector),
				 downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z),
				 leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z),
				 farTop = new Vector3f(),
				 farBottom = new Vector3f(),
				 nearTop = new Vector3f(),
				 nearBottom = new Vector3f();
		/*upVector.negate(downVector);
		rightVector.negate(leftVector);*/
		centerFar.add(new Vector3f(upVector.x * farHeight, upVector.y * farHeight, upVector.z * farHeight), farTop);
		centerFar.add(new Vector3f(downVector.x * farHeight, downVector.y * farHeight, downVector.z * farHeight), farBottom);
		centerNear.add(new Vector3f(upVector.x * nearHeight, upVector.y * nearHeight, upVector.z * nearHeight), nearTop);
		centerNear.add(new Vector3f(downVector.x * nearHeight, downVector.y * nearHeight, downVector.z * nearHeight), nearBottom);
		Vector4f[] points = new Vector4f[8];
		points[0] = calculateLightSpaceFrustrumCorner(farTop, rightVector, farWidth);
		points[1] = calculateLightSpaceFrustrumCorner(farTop, leftVector, farWidth);
		points[2] = calculateLightSpaceFrustrumCorner(farBottom, rightVector, farWidth);
		points[3] = calculateLightSpaceFrustrumCorner(farBottom, leftVector, farWidth);
		points[4] = calculateLightSpaceFrustrumCorner(nearTop, rightVector, nearWidth);
		points[5] = calculateLightSpaceFrustrumCorner(nearTop, leftVector, nearWidth);
		points[6] = calculateLightSpaceFrustrumCorner(nearBottom, rightVector, nearWidth);
		points[7] = calculateLightSpaceFrustrumCorner(nearBottom, leftVector, nearWidth);
		return points;
	}
	
	/** Calculates one of the corner vertices of the view frustum in world space
     * and converts it to light space.
     * 
     * @param startPoint The starting center point on the view frustum.
     * @param direction The direction of the corner from the start point.
     * @param width The distance of the corner from the start point.
     * @return The relevant corner vertex of the view frustum in light space. */
	private Vector4f calculateLightSpaceFrustrumCorner(Vector3f startPoint, Vector3f direction, float width) {
		Vector3f point = new Vector3f();
		startPoint.add(new Vector3f(direction.x * width, direction.y * width, direction.z * width), point);
		Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1);
		lightViewMatrix.transform(point4f);
		return point4f;
	}
	
	/** @return The rotation of the camera represented as a matrix. */
	private Matrix4f calculateCameraRotationMatrix() {
		return new Matrix4f()
				.rotate(camera.getInvertPitch(), new Vector3f(1, 0, 0))
				.rotate(camera.getInvertYaw(),	 new Vector3f(0, 1, 0))
				.rotate(camera.getInvertRoll(),  new Vector3f(0, 0, 1));
	}
	
	/** Calculates the width and height of the near and far planes of the
     * camera's view frustum. However, this doesn't have to use the "actual" far
     * plane of the view frustum. It can use a shortened view frustum if desired
     * by bringing the far-plane closer, which would increase shadow resolution
     * but means that distant objects wouldn't cast shadows. */
	private void calculateWidthsAndHeights() {
		float fov = (float)Math.tan(Math.toRadians(Core.getFieldOfViewValue()));
		farWidth = (float)(SHADOW_DISTANCE * fov);
		nearWidth = (float)(Core.getNearValue() * fov);
		farHeight = farWidth / getAspectRatio();
		nearHeight = nearWidth / getAspectRatio();
	}
	
	/** @return The aspect ratio of the display (width:height ratio). */
	private float getAspectRatio() {
		return Core.getWidth() / Core.getHeight();
	}
	
}
