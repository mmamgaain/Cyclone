package in.mayank.extra.utils;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import in.mayank.extra.core.Core;

/** The Maths class provides methods not contained in the java.lang.Math or
 * java.lang.StrictMath classes. */
public class Maths {
	
	public static final Vector3f GRAVITY_3D = new Vector3f(0, 9.81F, 0), HIGH_GRAVITY_3D = new Vector3f(0, 19.62F, 0), RIGHT_3D = new Vector3f(1, 0, 0), OUT_OF_SCREEN_3D = new Vector3f(0, 0, 1), X_3D = new Vector3f(0, 1, 0), Y_3D = new Vector3f(1, 0, 0), Z_3D = new Vector3f(0, 0, 1), UP_3D = new Vector3f(0, -1, 0);
	public static final Vector2f GRAVITY_2D = new Vector2f(0, 9.81F), HIGH_GRAVITY_2D = new Vector2f(0, 19.62F), RIGHT_2D = new Vector2f(1, 0), OUT_OF_SCREEN_2D = new Vector2f(0, 0), X_2D = new Vector2f(0, 1), Y_2D = new Vector2f(1, 0), Z_2D = new Vector2f(0, 0), UP_2D = new Vector2f(0, -1);
	
	public static float SLEEP_EPSILON = 0.3F;
	public static final float TWO_PI = (float)(2 * Math.PI), HALF_PI = (float)(Math.PI * 0.5F);
	
	/** Returns the sign of the number. Returns -1 for negative, 1 for positive and 0 otherwise. */
	public static int sign(short v){
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}
	
	/** Returns the sign of the number. Returns -1 for negative, 1 for positive and 0 otherwise. */
	public static int sign(int v){
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}
	
	/** Returns the sign of the number. Returns -1 for negative, 1 for positive and 0 otherwise. */
	public static int sign(long v){
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}
	
	/** Returns the sign of the number. Returns -1 for negative, 1 for positive and 0 otherwise. */
	public static int sign(float v){
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}
	
	/** Returns the sign of the number. Returns -1 for negative, 1 for positive and 0 otherwise. */
	public static int sign(double v){
		return (v > 0) ? 1 : (v < 0) ? -1 : 0;
	}
	
	/** It's faster than Math class ceil method as it takes a float and return an int instead of
	 * a double and doesn't consider special cases. */
	public static int ceil(double f){
		int intF = (int)f;
		return (f < 0 || (f - intF == 0F)) ? intF : intF + 1;
	}
	
	/** It's faster than Math class floor method as it takes a float and return an int instead of
	 * a double and doesn't consider special cases. */
	public static int floor(double f){
		int intF = (int)f;
		return (f < 0 && (f - intF != 0)) ? intF - 1 : intF;
	}
	
	/** Return a {@link Vector2fc} whose value of every axis is between 0 and the
	 * respective axis value of the supplied vector.
	 * 
	 * @param v The vector that serves as the upper-limit. */
	public static Vector2fc getRandomVector(Vector2fc v){
		return new Vector2f((float)(Math.random() * v.x()), (float)(Math.random() * v.y()));
	}
	
	/** Return a {@link Vector3fc} whose value of every axis is between 0 and the
	 * respective axis value of the supplied vector.
	 * 
	 * @param v The vector that serves as the upper-limit. */
	public static Vector3fc getRandomVector(Vector3fc v){
		return new Vector3f((float)(Math.random() * v.x()), (float)(Math.random() * v.y()), (float)(Math.random() * v.z()));
	}
	
	/** Returns the factorial of the supplied number. If the value is zero
	 * or a negative number, zero is returned.
	 * 
	 * @param n The value of which the factorial is to be calculated. */
	public static int factorial(int n) {
		return n <= 0 ? 0 : (n == 1 ? 1 : n * factorial(n - 1));
	}
	
	/** Return true if the specified number is a power of 2.
	 * 
	 * @param value The number that needs to be checked.
	 * 
	 * @return true if the value is a power of two, otherwise false. */
	public static boolean isPowerOfTwo(int value){
		return ((value & (value - 1)) == 0);
	}
	
	/** Gets the number of "on" bits in an integer. */
	public static int getBitCounts(int n){
		int count = 0;
		for(; n > 0; n >>= 1) count += (n & 1);
		
		return count;
	}
	
	/** Sets a new value of {@link #SLEEP_EPSILON}. Its default value is 0.3F.
	 * 
	 *  @param sleepEpsilon The new value of {@link #SLEEP_EPSILON}. */
	public static void setSleepEpsilon(float sleepEpsilon){
		SLEEP_EPSILON = sleepEpsilon;
	}
	
	/** This method returns a {@link Vector2f} representing the two solutions
	 * of a quadratic equation. A standard quadratic equation looks like:
	 * 
	 * <p><code>ax<sup>2</sup> + bx + c = 0</code></p>
	 * 
	 * @param a The coefficient of the x<sup>2</sup> value in the quadratic
	 * equation.
	 * @param b The coefficient of the x value in the quadratic equation.
	 * @param c The scalar quantity with no variable attached to it.
	 * 
	 * @see #getBetaQuadrant(double, double, double) */
	public static Vector2f getQuadraticResult(double a, double b, double c){
		double d = Math.sqrt(b*b - 4*a*c)/(2*a);
		return new Vector2f((float)(-b + d), (float)(-b - d));
	}
	
	/** Returns the value in megabyte of the value passed in gigabytes.
	 * 
	 * @param gb The value passed in gigabytes.
	 * 
	 * @return The equivalent value in megabytes. */
	public static double getMBFromGB(double gb){
		return gb*1024;
	}
	
	/** Returns the value in megabyte of the value passed in kilobytes.
	 * 
	 * @param kb The value passed in kilobytes.
	 * 
	 * @return The equivalent value in kilobytes. */
	public static double getMBFromKB(double kb){
		return kb/1024;
	}
	
	/** Returns the value in kilobyte of the value passed in megabytes.
	 * 
	 * @param kb The value passed in megabytes.
	 * 
	 * @return The equivalent value in megabytes. */
	public static double getKBFromMB(double kb){
		return kb*1024;
	}
	
	/** Returns the value in kilobyte of the value passed in megabytes.
	 * 
	 * @param mb The value passed in gigabytes.
	 * 
	 * @return The equivalent value in megabytes. */
	public static double getGBFromMB(double mb){
		return mb/1024;
	}
	
	/** Returns the value in terabyte of the value passed in gigabytes.
	 * 
	 * @param gb The value passed in gigabytes.
	 * 
	 * @return The equivalent value in megabytes. */
	public static double getTBFromGB(double tb){
		return tb/1024;
	}
	
	/***/
	public static double getGBFromTB(double tb){
		return tb*1024;
	}
	
	/** Pseudo-randomly returns a number lying between the range
	 * provided. Only the lower limit of the range is included.
	 * 
	 * @param min The lower limit of the range.
	 * @param max The upper limit of the range. */
	public static float getRandom(float min, float max){
		return (float)(min + Math.random() * (max - min));
	}
	
	/** Pseudo-randomly returns a <code>int</code> lying between the range
	 * provided. Both the limits of the ranges are included.
	 * 
	 * @param min The starting limit of the range.
	 * @param max The last limit of the range. */
	public static int getRandomInt(int min, int max){
		return floor(min + (float)Math.random() * (max - min + 1));
	}
	
	/** Checks if the number supplied is an even number or not.<br><br>
	 * 
	 * <b><u>NOTE</u> :</b> <i>This method will also categorize zero as
	 * an even number. If you don't want that, check the supplied value
	 * to make sure it's a non-zero value.</i>
	 * 
	 * @param num The number that needs to be checked.
	 * 
	 * @return <code>true</code> if the number is even, otherwise <code>false</code>. */
	public static boolean isEven(int num){
		return ((num & 1) == 0);
	}
	
	public static boolean isEven(long num){
		return ((num & 1) == 0);
	}
	
	/** Pseudo-randomly returns a vector lying between the range provided.
	 * Only the lower limit of the range is included.
	 * 
	 * @param v1 The vector from which the received vector should be equal
	 * to or greater.
	 * @param v2 The vector from which the received vector should be equal
	 * to or less. */
	public static Vector3f getRandomVector(Vector3fc v1, Vector3fc v2){
		return new Vector3f(getRandom(v1.x(), v2.x()), getRandom(v1.y(), v2.y()), getRandom(v1.z(), v2.z()));
	}
	
	/** Pseudo-randomly returns a vector lying between the range provided.
	 * Only the lower limit of the range is included.
	 * 
	 * @param v1 The vector from which the received vector should be equal
	 * to or greater.
	 * @param v2 The vector from which the received vector should be equal
	 * to or less. */
	public static Vector2f getRandomVector(Vector2fc v1, Vector2fc v2){
		return new Vector2f(getRandom(v1.x(), v2.x()), getRandom(v1.y(), v2.y()));
	}
	
	/***/
	public static float distanceSquared(Vector2f pos1, Vector2f pos2) {
		float dx = pos1.x - pos2.x, dy = pos1.y - pos2.y;
		return dx * dx + dy * dy;
	}
	
	/***/
	public static float distance(Vector2f pos1, Vector2f pos2) {
		return (float) Math.sqrt(distanceSquared(pos1, pos2));
	}
	
	/***/
	public static float min(float num0, float num1) {
		return num0 > num1 ? num1 : num0;
	}
	
	/***/
	public static float max(float num0, float num1) {
		return num0 < num1 ? num1 : num0;
	}
	
	/***/
	public static float abs(float value) {
		return value >= 0F ? value : -value;
	}
	
	/***/
	public static int round(float num) {
		return num - (int)num >= 0.5F ? ceil(num) : floor(num);
	}
	
	/***/
	public static boolean circlePointCollision(Vector2f pos1, Vector2f pos2, float radius) {
		return radius <= distance(pos1, pos2);
	}
	
	/***/
	public static boolean circleCircleCollision(Vector2f pos1, Vector2f pos2, float radius1, float radius2) {
		return (radius1 + radius2 <= distance(pos1, pos2));
	}
	
	/** Checks if a value is within a range or not.
	 * 
	 * <b><u>NOTE</u> :</b> <i>This method would still work if the range was inverted
	 * (i.e., if min was greater than max).</i>
	 * 
	 * @param value The value to be checked.
	 * @param min The minimum value of the range.
	 * @param max The maximum value of the range. */
	public static boolean inRange(float value, float min, float max) {
		return value >= min(min, max) && value <= max(min, max);
	}
	
	/***/
	public static boolean inRange(float value, Vector2f range) {
		return value >= min(range.x, range.y) && value <= max(range.x, range.y);
	}
	
	/***/
	public static boolean inRanges(float min1, float max1, float min2, float max2) {
		return inRange(min1, min2, max2) || inRange(max1, min2, max2);
	}
	
	/***/
	public static boolean inRanges(Vector2f range1, Vector2f range2) {
		return inRange(range1.x, range2) || inRange(range1.y, range2);
	}
	
	/***/
	public static boolean rectPointCollision(Vector2f rectPos, Vector2f pos, float width, float height) {
		return inRange(pos.x, rectPos.x, rectPos.x + width) && inRange(pos.y, rectPos.y, rectPos.y + height);
	}
	
	/***/
	public static boolean rectRectCollision(Vector2f rectPos1, Vector2f rectPos2, float width1, float height1, float width2, float height2) {
		return inRanges(rectPos1.x, rectPos1.x + width1, rectPos2.x, rectPos2.x + width2) && inRanges(rectPos1.y, rectPos1.y + height1, rectPos2.y, rectPos2.y + height2);
	}
	
	/** Untested */
	public static boolean circleRectCollision(Vector2f circlePos, Vector2f rectPos, float radius, float width, float height) {
		Vector2f rectCenter = new Vector2f(rectPos.x + width/2F, rectPos.y + height/2F),
				 distanceBetweenCenters = new Vector2f(abs(circlePos.x - rectCenter.x), abs(circlePos.y - rectCenter.y)),
				 closestSurfacePoint = new Vector2f(clamp(distanceBetweenCenters.x, rectPos.x, rectPos.x + width), clamp(distanceBetweenCenters.y, rectPos.y, rectPos.y + height));
		return circlePointCollision(circlePos, closestSurfacePoint, radius);
	}
	
	/***/
	public static boolean circleRectAABBCollision(Vector2f circlePos, Vector2f rectPos, float radius, float width, float height) {
		Vector2f closestSurfacePoint = new Vector2f(clamp(circlePos.x, rectPos.x, rectPos.x + width), clamp(circlePos.y, rectPos.y, rectPos.y + height));
		return circlePointCollision(circlePos, closestSurfacePoint, radius);
	}
	
	/** Restricts the given value between the minimum and maximum range.
	 * Both minimum and maximum values are included in the range.
	 * <br><br>
	 * <b><u>NOTE</u> : </b><i>This method would also work if the range was inverted
	 * (i.e., if min was greater than max).</i>
	 * 
	 * @param value The value to be checked for being in range.
	 * @param min The minimum limit of the range.
	 * @param max The maximum limit of the range.
	 * 
	 * @return The clamped value from <code>min</code> (included) to <code>max</code> (included). */
	public static float clamp(float value, float min, float max){
		float finalMin = min(min, max), finalMax = max(min, max);
		return (value > finalMin ? (value < finalMax ? value : finalMax) : finalMin);
	}
	
	/** Restricts the outcome to the number of decimal <code>places</code>
	 * mentioned. If the resulting fraction was to have more decimal places
	 * than the one specified, the result is rounded-off at the desired
	 * number of places.
	 * 
	 * @param value The value to be modified.
	 * @param places The maximum number of decimal places to be returned in the
	 * value. If the <code>value</code> contains fewer decimals points than
	 * <code>places</code>, the returned value is not expanded to accommodate for
	 * it. */
	public static float roundToPlaces(float value, int places){
		float mult = (float)Math.pow(10, places);
		return (round(value * mult) / mult);
	}
	
	/** Restricts the outcome of the value to the closest multiple of
	 * value of <code>nearest</code>. If the value of the <code>nearest</code>
	 * is 1, it functions exactly like a rounding operation.
	 * <br>
	 * It can be thought of as a grid constructed at the intervals of the
	 * <code>nearest</code> values. The output would always land on the nearest
	 * interval of the grid value.
	 * 
	 * @param value The value to be rounded.
	 * @param nearest The increments in which the <code>value</code>
	 * needs to be preserved. */
	public static int roundNearest(float value, int nearest){
		return round(value / nearest) * nearest;
	}
	
	/** Considers the <b>value</b> which exists in one range (<b>min to max</b>) and
	 * interpolates it into another range (<b>0 to 1</b>).
	 * <br><br>
	 * <b><u>NOTE</u> : </b><i>This method would also work if,<ol><li>The range were inverted
	 * (that is, if min was greater than max).</li><li>The value was already out of range.
	 * In this case, the value returned will also be out of bounds.</li></ol></i>
	 * 
	 * @param value The value to be interpolated.
	 * @param min The minimum bound of the range in which the value supplied exists.
	 * @param max The maximum bound of the range in which the value supplied exists.
	 * 
	 * @return The interpolated value. */
	public static float norm(float value, float min, float max) {
		return (value - min) / (max - min);
	}
	
	/** Considers the <b>value</b> which exists in one range (<b>0 to 1</b>) and
	 * interpolates it into another range (<b>min to max</b>).
	 * <br><br>
	 * <b><u>NOTE</u> : </b><i>This method would also work if,<ol><li>The range were inverted
	 * (that is, if min was greater than max).</li><li>The value was already out of range.
	 * In this case, the value returned will also be out of bounds.</li></ol></i>
	 * 
	 * @param value The value to be interpolated.
	 * @param min The minimum bound of the range in which the value needs to be
	 * interpolated.
	 * @param max The maximum bound of the range in which the value needs to be
	 * interpolated.
	 * 
	 * @return The interpolated value. */
	public static float lerp(float value, float min, float max) {
		return (max - min) * value + min;
	}
	
	/** Considers the <b>value</b> which exists in one range (<b>srcMin to srcMax</b>) and
	 * interpolates it into another range (<b>destMin to destMax</b>).
	 * <br><br>
	 * <b><u>NOTE</u> : </b><i>This method would also work if,<ol>1. One or both of the
	 * ranges were inverted (that is, if min was greater than max).<br>
	 * 2. The value was already out of range. In this case, the value returned will be
	 * out of bound from the second range, but it would logically still make sense.</ol></i>
	 * 
	 * @param value The value to be interpolated.
	 * @param srcMin The minimum bound of the range in which the value supplied exists.
	 * @param srcMax The maximum bound of the range in which the value supplied exists.
	 * @param destMin The minimum bound of the range in which the value needs to be
	 * interpolated.
	 * @param destMax The maximum bound of the range in which the value needs to be
	 * interpolated.
	 * 
	 * @return The value interpolated in the other range. */
	public static float map(float value, float srcMin, float srcMax, float destMin, float destMax) {
		return lerp(norm(value, srcMin, srcMax), destMin, destMax);
	}
	
	/**  */
	public static float barrycentricInterpolation(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z),
			  l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det,
			  l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det,
			  l3 = 1F - l1 - l2;
		
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static Collatz collatzValues(long value) {
		long original = value, max = 0;
		List<Long> values = new ArrayList<>(); 
		
		do {
			if(isEven(value)) value /= 2;
			else value = 3 * value + 1;
			if(value > max) max = value;
			values.add(value);
		} while(value != 1);
		
		return new Collatz(values, original, max);
	}
	
	/** Returns the normal that's appropriate for the triangle represented
	 * by the three vertices supplied. Since only one normal is supplied for
	 * three vertices, it should mostly be used for low-poly modelling.
	 * <br><br>
	 * <b><u>NOTE</u> :</b> <br><i>1. The vertices supplied should be in the
	 * counter-clockwise direction. If they are in a clockwise direction, the
	 * vector will face the opposite direction.<br> 2. The normal vector is
	 * normalised before returning.</i>
	 * 
	 * @param v0 The first vertex.
	 * @param v1 The next vertex in the counter-clockwise direction from
	 * v0.
	 * @param v2 The last vertex continuing the same direction as before.
	 * 
	 * @return The vector that is the normal of the surface that the three
	 * vertices represent. */
	public static Vector3f calculateNormal(Vector3f v0, Vector3f v1, Vector3f v2) {
		Vector3f tangentA = new Vector3f(),
				 tangentB = new Vector3f(),
				 normal = new Vector3f();
		v1.sub(v0, tangentA);
		v2.sub(v0, tangentB);
		tangentA.cross(tangentB, normal);
		
		return normal.normalize();
	}
	
	/***/
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector2f scale, Vector3f rotation) {
		return createTransformationMatrix(translation, rotation, new Vector3f(scale.x, scale.y, 1F));
	}
	
	/***/
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector2f scale) {
		return createTransformationMatrix(translation, scale, new Vector3f(0));
	}
	
	/** Creates a transformation matrix in 3D.
	 * 
	 * @param translation The translation  */
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		return new Matrix4f().translate(translation)
				.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
				.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
				.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1))
				.scale(scale);
	}
	
	/** Creates the perspective projection matrix.
	 * 
	 * @param fov The "Field of View" of the 3D projection, in degrees.
	 * @param nearPlane The distance (from the camera) of the near plane of
	 * the frustum created by this projection. Anything closer than this
	 * distance (in world coordinates) will be considered too close to be
	 * rendered. <b>Do not pass zero as a value as it will lead to
	 * <i>'dividing by zero'</i> arithmetic errors.</b>
	 * @param farPlane The distance (from the camera) of the far plane of
	 * the frustum created by this projection. Anything further from this
	 * distance (in world coordinates) will be considered too far to be rendered.
	 * 
	 * @return The projection matrix as a 4x4 matrix. */
	public static Matrix4f createPerspectiveProjectionMatrix(float fov, float nearPlane, float farPlane) {
		Core.setProjectionMatrixValues(nearPlane, farPlane, fov);
		return new Matrix4f().perspective((float)Math.toRadians(fov), Core.getAspectRatio(), nearPlane, farPlane);
	}
	
	/** Creates the orthographic projection matrix.
	 * 
	 * @param width The width of the window of the projection.
	 * @param height The height of the window of projection.
	 * @param length The length of the projection from the screen to the
	 * farthest point.
	 * 
	 * @return The projection matrix as a 4x4 matrix. */
	public static Matrix4f createOrthoProjectionMatrix(float width, float height, float length) {
		return new Matrix4f().m00(2F / width).m11(2F / height)
				.m22(-2F / length).m33(1);
	}
	
	/***/
	public static Vector3f RGB2HSV(float r, float g, float b) {
		Vector3f out = new Vector3f();
	    float min, max, delta;
	    
	    min = r < g ? r : g;
	    min = min  < b ? min  : b;

	    max = r > g ? r : g;
	    max = max  > b ? max  : b;
	    
	    out.z = max;	// v
	    delta = max - min;
	    if (delta < 0.00001F) {
	        out.y = 0;
	        out.x = 0;	// undefined, maybe nan?
	        return out;
	    }
	    if(max > 0.0) out.y = (delta / max);	// NOTE: if max == 0, this divide would cause a crash
	    else {
	        // if max is 0, then r = g = b = s = 0, h is undefined
	        out.y = 0F;
	        out.x = Float.NaN;	// its now undefined
	        return out;
	    }
	    if( r >= max ) out.x = ( g - b ) / delta;        // > is bogus, just keeps compiler happy between yellow & magenta
	    else
		    if( g >= max ) out.x = 2F + ( b - r ) / delta;  // between cyan & yellow
		    else out.x = 4F + ( r - g ) / delta;  // between magenta & cyan
	    
	    out.x *= 60F;	// degrees
	    
	    if( out.x < 0.0 ) out.x += 360F;
	    
	    return out;
	}
	
	/** Converts HSV color input to an RGB output.
	 * 
	 * @param h Hue. As this value is in degrees, the acceptable range is 0 to 360.
	 * @param s Saturation. The acceptable range is 0 to 1.
	 * @param v Value. The acceptable range is 0 to 1.
	 * 
	 * @return The RGB color value that represents the Red, Green and Blue with its X, Y
	 * and Z axes respectively. All three values will lie between the range of 0 - 1. */
	public static Vector3f HSV2RGB(float h, float s, float v) {
		float hh, p, q, t, ff;
	    int i;
	    Vector3f out = new Vector3f();

	    if(s < 0F) {	// < is bogus, just shuts up warnings
	        out.x = v;
	        out.y = v;
	        out.z = v;
	        return out;
	    }
	    hh = h;
	    if(hh >= 360F) hh = 0F;
	    hh /= 60F;
	    i = (int)hh;
	    ff = hh - i;
	    p = v * (1F - s);
	    q = v * (1F - (s * ff));
	    t = v * (1F - (s * (1F - ff)));

	    switch(i) {
	    case 0:
	        out.x = v;
	        out.y = t;
	        out.z = p;
	        break;
	    case 1:
	        out.x = q;
	        out.y = v;
	        out.z = p;
	        break;
	    case 2:
	        out.x = p;
	        out.y = v;
	        out.z = t;
	        break;
	    case 3:
	        out.x = p;
	        out.y = q;
	        out.z = v;
	        break;
	    case 4:
	        out.x = t;
	        out.y = p;
	        out.z = v;
	        break;
	    case 5:
	    default:
	        out.x = v;
	        out.y = p;
	        out.z = q;
	        break;
	    }
	    return out;
	}
	
	/***/
	/*public static Matrix4f createViewMatrix(Camera1P camera) {
		Matrix4f matrix = new Matrix4f();
		matrix.rotate(camera.getPitch(), new Vector3f(1, 0, 0));
		matrix.rotate(camera.getYaw(), new Vector3f(0, 1, 0));
		matrix.rotate(camera.getRoll(), new Vector3f(0, 0, 1));
		Vector3f pos = camera.getPosition();
		matrix.translate(-pos.x, -pos.y, -pos.z);
		//matrix.translate(camera.position.negate());
		
		return matrix;
	}*/
	
}
