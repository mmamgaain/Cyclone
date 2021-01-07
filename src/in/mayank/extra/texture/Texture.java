package in.mayank.extra.texture;

import java.nio.ByteBuffer;

import org.joml.SimplexNoise;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import in.mayank.extra.utils.Maths;

public class Texture {
	
	private int textureID, width, height = 0, depth = 0;
	private ByteBuffer pixels;
	
	public static final int TEXTURE_1D 		 = GL11.GL_TEXTURE_1D,
							TEXTURE_2D 		 = GL11.GL_TEXTURE_2D,
							TEXTURE_3D 		 = GL12.GL_TEXTURE_3D,
							TEXTURE_CUBE_MAP = GL13.GL_TEXTURE_CUBE_MAP;
	
	public Texture(String filePath) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			int[] w = new int[1], h = new int[1], comp = new int[1];
			
			pixels = STBImage.stbi_load(filePath, w, h, comp, 4);
			if(pixels == null) System.err.println("Couldn't load texture : " + filePath);
			//System.out.println("The number of components of " + filePath + " texture are " + comp[0]);
			width = w[0];
			height = h[0];
		}
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		STBImage.stbi_image_free(pixels);
	}
	
	/***/
	public Texture(int width, Vector4f color) {
		color.mul(255);
		pixels = BufferUtils.createByteBuffer(width * 4);
		for(int i = 0; i < width; i++) pixels.put((byte)color.x).put((byte)color.y).put((byte)color.z).put((byte)color.w);
		textureID = createTexture(width, pixels);
	}
	
	/***/
	public Texture(int width, int height, Vector4f color) {
		setDimensions(width, height);
		int dimension = width * height;
		//color.mul(255, color);
		pixels = BufferUtils.createByteBuffer(dimension * 4);
		for(int i = 0; i < dimension; i++) pixels.put((byte)color.x).put((byte)color.y).put((byte)color.z).put((byte)color.w);
		textureID = createTexture(width, height, pixels.flip());
	}
	
	/***/
	public Texture(int width, int height, int depth, Vector4f color) {
		int dimension = width * height * depth;
		color.mul(255);
		pixels = BufferUtils.createByteBuffer(dimension * 4);
		for(int i = 0; i < dimension; i++) pixels.put((byte)color.x).put((byte)color.y).put((byte)color.z).put((byte)color.w);
		textureID = createTexture(width, height, depth, pixels);
	}
	
	/***/
	public Texture(int width, ByteBuffer color) {
		textureID = createTexture(width, color);
	}
	
	/***/
	public Texture(int width, int height, ByteBuffer color) {
		textureID = createTexture(width, height, color);
	}
	
	/***/
	public Texture(int width, int height, int depth, ByteBuffer color) {
		textureID = createTexture(width, height, depth, color);
	}
	
	/** Creates a 1D texture out of 4D Simplex Noise.<br><br>
	 * 
	 * <b><u>NOTE</u> :</b> <i>
	 * 	<ol>
	 * 		<li>This method produces a greyscale image.</li>
	 * 		<li>It calculates the value of each channel as the scaled 0-255 range
	 * 			value from the output from a 2D Simplex function.</li>
	 * </ol>
	 * </i>
	 * 
	 * @param width The width of the texture.
	 * @param xOffset This texture's starting point of X-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param yOffset This texture's starting point of Y-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param zOffset This texture's starting point of Z-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param wOffset This texture's starting point of W-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param xIncrement The value by which the X-axis of the texture will be increased.
	 * This value cannot be negative. */
	public Texture(int width, float xOffset, float yOffset, float zOffset, float wOffset, float xIncrement) {
		xOffset = Maths.max(xOffset, 0);
		yOffset = Maths.max(yOffset, 0);
		zOffset = Maths.max(zOffset, 0);
		wOffset = Maths.max(wOffset, 0);
		pixels = BufferUtils.createByteBuffer(width * 4);
		float x = xOffset;
		byte color;
		for(int i = 0; i < width; i++) {
			color = (byte)((SimplexNoise.noise(x, yOffset, zOffset, wOffset) * 0.5F + 0.5F) * 255);
			pixels.put(color).put(color).put(color).put((byte)255);
			x += xIncrement;
		}
		textureID = createTexture(width, pixels.flip());
	}
	
	/** Creates a 2D texture out of 4D Simplex Noise.<br><br>
	 * 
	 * <b><u>NOTE</u> :</b> <i>
	 * 	<ol>
	 * 		<li>This method produces a greyscale image.</li>
	 * 		<li>It calculates the value of each channel as the scaled 0-255 range
	 * 			value from the output from a 2D Simplex function.</li>
	 * </ol>
	 * </i>
	 * 
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 * @param depth The depth of the texture.
	 * @param xOffset This texture's starting point of X-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param yOffset This texture's starting point of Y-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param zOffset This texture's starting point of Z-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param wOffset This texture's starting point of W-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param xIncrement The value by which the X-axis of the texture will be increased.
	 * This value cannot be negative.
	 * @param yIncrement The value by which the Y-axis of the texture will be increased.
	 * This value cannot be negative.
	 * @param zIncrement The value by which the Z-axis of the texture will be increased.
	 * This value cannot be negative. */
	public Texture(int width, int height, float xOffset, float yOffset, float zOffset, float wOffset, float xIncrement, float yIncrement) {
		xOffset = Maths.max(xOffset, 0);
		yOffset = Maths.max(yOffset, 0);
		zOffset = Maths.max(zOffset, 0);
		wOffset = Maths.max(wOffset, 0);
		pixels = BufferUtils.createByteBuffer(width * height * 4);
		float x, y = yOffset;
		byte color;
		for(int i = 0; i < height; i++) {
			x = xOffset;
			for(int j = 0; j < width; j++) {
				color = (byte)((SimplexNoise.noise(x, y, zOffset, wOffset) * 0.5F + 0.5F) * 255);
				pixels.put(color).put(color).put(color).put((byte)255);
				x += xIncrement;
			}
			y += yIncrement;
		}
		textureID = createTexture(width, height, pixels.flip());
	}
	
	/** Creates a 3D texture out of 4D Simplex Noise.<br><br>
	 * 
	 * <b><u>NOTE</u> :</b> <i>
	 * 	<ol>
	 * 		<li>This method produces a greyscale image.</li>
	 * 		<li>It calculates the value of each channel as the scaled 0-255 range
	 * 			value from the output from a 2D Simplex function.</li>
	 * </ol>
	 * </i>
	 * 
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 * @param depth The depth of the texture.
	 * @param xOffset This texture's starting point of X-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param yOffset This texture's starting point of Y-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param zOffset This texture's starting point of Z-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param wOffset This texture's starting point of W-axis value w.r.t. the simplex noise.
	 * This value cannot be negative.
	 * @param xIncrement The value by which the X-axis of the texture will be increased.
	 * This value cannot be negative.
	 * @param yIncrement The value by which the Y-axis of the texture will be increased.
	 * This value cannot be negative.
	 * @param zIncrement The value by which the Z-axis of the texture will be increased.
	 * This value cannot be negative. */
	public Texture(int width, int height, int depth, float xOffset, float yOffset, float zOffset, float wOffset, float xIncrement, float yIncrement, float zIncrement) {
		xOffset = Maths.max(xOffset, 0);
		yOffset = Maths.max(yOffset, 0);
		zOffset = Maths.max(zOffset, 0);
		wOffset = Maths.max(wOffset, 0);
		pixels = BufferUtils.createByteBuffer(width * height * depth * 4);
		float x, y, z = zOffset;
		byte color;
		for(int i = 0; i < depth; i++) {
			y = yOffset;
			for(int j = 0; j < height; j++) {
				x = xOffset;
				for(int k = 0; k < width; k++) {
					color = (byte)((SimplexNoise.noise(x, y, z, wOffset) * 0.5F + 0.5F) * 255);
					pixels.put(color).put(color).put(color).put((byte)255);
					x += xIncrement;
				}
				y += yIncrement;
			}
			z += zIncrement;
		}
		textureID = createTexture(width, height, pixels);
	}
	
	/** Random texture */
	public Texture(int width) {
		pixels = BufferUtils.createByteBuffer(width * 4);
		for(int j = 0; j < width; j++) {
			byte color = (byte)(Math.random() * 255);
			pixels.put(color).put(color).put(color).put((byte)255);
		}
		textureID = createTexture(width, pixels);
	}
	
	/** Random texture */
	public Texture(int width, int height) {
		pixels = BufferUtils.createByteBuffer(width * height * 4);
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				byte color = (byte)(Math.random() * 255);
				pixels.put(color).put(color).put(color).put((byte)255);
			}
		}
		textureID = createTexture(width, height, pixels);
	}
	
	/** Random texture */
	public Texture(int width, int height, int depth) {
		pixels = BufferUtils.createByteBuffer(width * height * depth * 4);
		for(int i = 0; i < depth; i++) {
			for(int j = 0; j < height; j++) {
				for(int k = 0; k < depth; k++) {
					byte color = (byte)(Math.random() * 255);
					pixels.put(color).put(color).put(color).put((byte)255);
				}
			}
		}
		textureID = createTexture(width, height, depth, pixels);
	}
	
	// Allocates memory in the GPU for a texture and load the pixel data into that texture memory. 
	private int createTexture(int width, int height, ByteBuffer pixels) {
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		this.width = width;
		this.height = height;
		return textureID;
	}
	
	// Allocates memory in the GPU for a texture and load the pixel data into that texture memory. 
	private int createTexture(int width, ByteBuffer pixels) {
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_1D, textureID);
		GL11.glTexImage1D(GL11.GL_TEXTURE_1D, 0, GL11.GL_RGBA8, width, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_1D);
		GL11.glTexParameterf(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		this.width = width;
		return textureID;
	}
	
	// Allocates memory in the GPU for a texture and load the pixel data into that texture memory. 
	private int createTexture(int width, int height, int depth, ByteBuffer pixels) {
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureID);
		GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGBA8, width, height, depth, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		GL30.glGenerateMipmap(GL12.GL_TEXTURE_3D);
		GL11.glTexParameterf(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		this.width = width;
		this.height = height;
		this.depth = depth;
		return textureID;
	}
	
	public static void update(int textureID, int width, int height, Vector2f offset, Vector2f increment, float scale) {
		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		float x = offset.x, y;
		for(int i = 0; i < height; i++) {
			y = offset.y;
			for(int j = 0; j < width; j++) {
				float colour = (Maths.abs((float)SimplexNoise.noise(x, y))) * scale;
				pixels.put((byte)(colour)).put((byte)(colour)).put((byte)(colour)).put((byte)(255));
				y += increment.y;
			}
			x += increment.x;
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)pixels.flip());
	}
	
	public int getID() { return textureID; }
	
	public ByteBuffer getPixels() { return pixels; }
	
	public int getWidth() { return width; }
	
	public int getHeight() { return height; }
	
	public int getDepth() { return depth; }
	
	private void setDimensions(int width) { this.width = width; }
	
	private void setDimensions(int width, int height) { this.width = width; this.height = height; }
	
	private void setDimensions(int width, int height, int depth) { this.width = width; this.height = height; this.depth = depth; }
	
}
