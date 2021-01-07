package in.mayank.extra.utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.SimplexNoise;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import in.mayank.extra.model.RawModel;
import in.mayank.extra.texture.Texture;

public class Loader {
	
	private List<Integer> vaos, textures;
	public List<Integer> vbos;
	
	private Map<String, Integer> textureBuffers;
	
	private int currCubeMapWidth, currCubeMapHeight;
	private static int MAX_ANISOTROPY_LEVEL = -1;
	
	public Loader() {
		vaos = new ArrayList<>();
		vbos = new ArrayList<>();
		textures = new ArrayList<>();
		textureBuffers = new HashMap<>();
		if(MAX_ANISOTROPY_LEVEL == -1) MAX_ANISOTROPY_LEVEL = GL11.glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
	}
	
	public RawModel loadToVAO(float[] positions, int dimension) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, dimension, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length / dimension);
	}
	
	public RawModel loadToVAO(float[] positions, int dimension, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, dimension, positions);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(float[] positions, int dimension, float[] textureCoords) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, dimension, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return new RawModel(vaoID, positions.length / dimension);
	}
	
	public RawModel loadToVAO(float[] positions, int dimension, int[] indices, float[] textureCoords) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, dimension, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(float[] positions, int dimension, float[] textureCoords, float[] normals) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, dimension, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, dimension, normals);
		unbindVAO();
		return new RawModel(vaoID, positions.length / dimension);
	}
	
	public RawModel loadToVAO(float[] positions, int dimension, int[] indices, float[] textureCoords, float[] normals) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, dimension, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, dimension, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(float[] positions, int dimension, int[] indices, float[] textureCoords, float[] normals, float[] tangents) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, dimension, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, dimension, normals);
		storeDataInAttributeList(3, dimension, tangents);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(FloatBuffer positions, int dimension, IntBuffer indices, FloatBuffer textureCoords, FloatBuffer normals, FloatBuffer tangents, FloatBuffer bitangents) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, dimension, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, dimension, normals);
		storeDataInAttributeList(3, dimension, tangents);
		storeDataInAttributeList(4, dimension, bitangents);
		unbindVAO();
		return new RawModel(vaoID, indices.limit());
	}
	
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	public Loader createEmptyVAO() {
		createVAO();
		return this;
	}
	
	private void storeDataInAttributeList(int attribNumber, int dimension, float[] data) {
		storeDataInAttributeList(attribNumber, dimension, storeDataInFloatBuffer(data));
	}
	
	private void storeDataInAttributeList(int attribNumber, int dimension, FloatBuffer data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribNumber, dimension, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void bindIndicesBuffer(int[] indices) {
		bindIndicesBuffer(storeDataInIntBuffer(indices));
	}
	
	private void bindIndicesBuffer(IntBuffer indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		return BufferUtils.createFloatBuffer(data.length).put(data).flip();
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data) {
		return BufferUtils.createIntBuffer(data.length).put(data).flip();
	}
	
	private ByteBuffer storeDataInByteBuffer(byte[] data) {
		return BufferUtils.createByteBuffer(data.length).put(data).flip();
	}
	
	public void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	public int loadTexture(final String fileName) {
		return loadTexture(fileName, 0, 0);
	}
	
	public int loadTexture(final String fileName, int anisotropyLevel, final float levelOfDetail) {
		Integer textureID = textureBuffers.get(fileName);
		if(textureID != null) return textureID;
		
		Texture texture = new Texture(fileName);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, levelOfDetail);
		if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			if(anisotropyLevel > 0) {
				anisotropyLevel = Math.min(anisotropyLevel, MAX_ANISOTROPY_LEVEL);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropyLevel);
			}
		}
		else {
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4F);
			System.out.println("Anisotropic filtering not supported.");
		}
		
		textureID = texture.getID();
		textures.add(textureID);
		textureBuffers.put(fileName, textureID);
		
		return textureID;
	}
	
	public int loadTexture(int width, Vector4f color) {
		int texture = new Texture(width, color).getID();
		textures.add(texture);
		return texture;
	}
	
	public int loadTexture(int width, int height, Vector4f color) {
		int texture = new Texture(width, height, color).getID();
		textures.add(texture);
		return texture;
	}
	
	public int loadTexture(int width, ByteBuffer color) {
		int texture = new Texture(width, color).getID();
		textures.add(texture);
		return texture;
	}
	
	public int loadTexture(int width, byte[] color) {
		int texture = new Texture(width, storeDataInByteBuffer(color)).getID();
		textures.add(texture);
		return texture;
	}
	
	public int loadTexture(int width, int height, ByteBuffer color) {
		int texture = new Texture(width, height, color).getID();
		textures.add(texture);
		return texture;
	}
	
	public int loadTexture(int width, int height, int depth, ByteBuffer color) {
		int texture = new Texture(width, height, depth, color).getID();
		textures.add(texture);
		return texture;
	}
	
	/***/
	public int loadTexture(int width, int height, int depth, Vector4f color) {
		int texture = new Texture(width, height, depth, color).getID();
		textures.add(texture);
		return texture;
	}
	
	public int loadTexture(int width, float xOffset, float yOffset, float zOffset, float wOffset, float xIncrement) {
		int texture = new Texture(width, xOffset, yOffset, zOffset, wOffset, xIncrement).getID();
		textures.add(texture);
		return texture;
	}
	
	public int loadTexture(int width, int height, float xOffset, float yOffset, float zOffset, float wOffset, float xIncrement, float yIncrement) {
		int texture = new Texture(width, height, xOffset, yOffset, zOffset, wOffset, xIncrement, yIncrement).getID();
		textures.add(texture);
		return texture;
	}
	
	/***/
	public int loadTexture(int width, int height, int depth, float xOffset, float yOffset, float zOffset, float wOffset, float xIncrement, float yIncrement, float zIncrement) {
		int texture = new Texture(width, height, depth, xOffset, yOffset, zOffset, wOffset, xIncrement, yIncrement, zIncrement).getID();
		textures.add(texture);
		return texture;
	}
	
	/***/
	
	// TODO: Correct Implementation
	public int loadTextureCubeMap(final String filename) {
		ByteBuffer buffer = null;
		int width, height;
		
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer comp = stack.mallocInt(1), w = stack.mallocInt(1),
					  h = stack.mallocInt(1);
			
			buffer = STBImage.stbi_load(filename, w, h, comp, 4);
			if(buffer == null) System.err.println("Couldn't load texture : "+ filename);
			width = w.get(0) / 3;
			height = h.get(0) / 3;
		}
	
		int id = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
				
		GL11.glTexSubImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 2 * width, height, 3 * width, 2 * height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);	// Right
		GL11.glTexSubImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, height, width, 2 * height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);	// Left
		GL11.glTexSubImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, width, 0, 2 * width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);	// Top
		GL11.glTexSubImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, width, 2 * height, 2 * width, 3 * height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);	// Bottom
		GL11.glTexSubImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 2 * width, height, 3 * width, 2 * height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);	// Back
		GL11.glTexSubImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, width, height, 2 * width, 2 * height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);	// Front
			
			
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			
		textures.add(id);
		return 0;
	}
		
	/***/
	public int loadTextureCubeMap(final String[] textureFiles) {
		int id = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
			
		for(int i = 0; i < textureFiles.length; i++) {
			// decodeTextureFiles method has to be run before the glTexImage2D function as
			// it instantiates the currCubeMapWidth and the currCubeMapHeight variables
			ByteBuffer data = decodeTextureFiles(textureFiles[i], false);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, currCubeMapWidth, currCubeMapHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textures.add(id);
		
		return id;
	}
	
	/***/
	public int loadTextureCubeMap(int width, int height, float xOffset, float yOffset, float xIncrement, float yIncrement) {
		int id = GL11.glGenTextures();
		float xDist = width * xIncrement, yDist = height * yIncrement;
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
		ByteBuffer data;
		
		// decodeTextureFiles method has to be run before the glTexImage2D function as
		// it instantiates the currCubeMapWidth and the currCubeMapHeight variables
		data = createNoiseBuffer(width, height, 2 * xDist, yDist, xIncrement, yIncrement);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		data = createNoiseBuffer(width, height, 0, yDist, xIncrement, yIncrement);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		data = createNoiseBuffer(width, height, 3 * xDist, yDist, xIncrement, yIncrement);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		data = createNoiseBuffer(width, height, xDist, yDist, xIncrement, yIncrement);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		data = createNoiseBuffer(width, height, xDist, 2 * yDist, xIncrement, yIncrement);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		data = createNoiseBuffer(width, height, xDist, 0, xIncrement, yIncrement);
		GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textures.add(id);
				
		return id;
	}
	
	/***/
	public int loadTextureCubeMap(final String[] textureFiles, final boolean[] flip) {
		int id = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
			
		for(int i = 0; i < textureFiles.length; i++) {
			// decodeTextureFiles method has to be run before the glTexImage2D function as
			// it instantiates the currCubeMapWidth and the currCubeMapHeight variables
			ByteBuffer data = decodeTextureFiles(textureFiles[i], flip[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, currCubeMapWidth, currCubeMapHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textures.add(id);
				
		return id;
	}
	
	private ByteBuffer createNoiseBuffer(int width, int height, float xOffset, float yOffset, float xIncrement, float yIncrement) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		
		xOffset = Maths.max(xOffset, 0);
		yOffset = Maths.max(yOffset, 0);
		float x, y = yOffset;
		byte color;
		for(int i = 0; i < height; i++) {
			x = xOffset;
			for(int j = 0; j < width; j++) {
				color = (byte)((SimplexNoise.noise(x, y, 0, 0) * 0.5F + 0.5F) * 255);
				buffer.put(color).put(color).put(color).put((byte)255);
				x += xIncrement;
			}
			y += yIncrement;
		}
		
		
		return buffer;
	}
	
	private ByteBuffer decodeTextureFiles(String fileName, boolean flip) {
		ByteBuffer buffer = null;
		
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer comp = stack.mallocInt(1), w = stack.mallocInt(1),
					  h = stack.mallocInt(1);
			
			STBImage.stbi_set_flip_vertically_on_load(flip);
			buffer = STBImage.stbi_load(fileName, w, h, comp, 4);
			if(buffer == null) System.err.println("Couldn't load texture : "+ fileName);
			currCubeMapWidth = w.get();
			currCubeMapHeight = h.get();
		}
		
		return buffer;
	}
		
	public void dispose() {
		for(int i = 0; i < vbos.size(); i++) GL15.glDeleteBuffers(vbos.get(i));
		for(int i = 0; i < vaos.size(); i++) GL30.glDeleteVertexArrays(vaos.get(i));
		for(int i = 0; i < textures.size(); i++) GL13.glDeleteTextures(textures.get(i));
		
		vaos.clear();
		vbos.clear();
		textures.clear();
	}
	
}
