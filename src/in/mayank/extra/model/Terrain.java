package in.mayank.extra.model;

import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import in.mayank.extra.core.Core;
import in.mayank.extra.texture.MaterialTerrain;
import in.mayank.extra.utils.Loader;
import in.mayank.extra.utils.Maths;
import in.mayank.extra.utils.PerlinHeightGenerator;

public class Terrain {
	
	private float size, maxHeight;
	/*private static final int MAX_PIXEL_COLOR = 256 * 256 * 256,
							 HALF_MAX_PIXEL_COLOR = (MAX_PIXEL_COLOR) / 2;*/
	private int vertexCount;
	
	private float x, z, baseHeight = 0;
	private float tiling = 40F;
	private float[] heights;
	
	private RawModel model;
	private MaterialTerrain material;
	
	public Terrain(int gridX, int gridZ, float baseHeight, MaterialTerrain material, Loader loader) {
		this(gridX, gridZ, 800, baseHeight, 128, material, loader);
	}
	
	public Terrain(int gridX, int gridZ, float size, float baseHeight, int vertexCount, MaterialTerrain material, Loader loader) {
		init(gridX, gridZ, size, 0, baseHeight, material);
		this.vertexCount = vertexCount;
		model = generateTerrain(loader);
	}
	
	public Terrain(int gridX, int gridZ, float size, float baseHeight, MaterialTerrain material, String heightMap, Loader loader) {
		init(gridX, gridZ, size, baseHeight, 40, material);
		model = generateTerrain(heightMap, loader);
	}
	
	public Terrain(int gridX, int gridZ, float size, float baseHeight, MaterialTerrain material, PerlinHeightGenerator generator, Loader loader) {
		init(gridX, gridZ, size, baseHeight, 40, material);
		model = generateTerrain(generator, loader);
	}
	
	private void init(int gridX, int gridZ, float size, float baseHeight, float maxHeight, MaterialTerrain material) {
		this.material = material;
		this.size = size;
		this.baseHeight = baseHeight;
		this.maxHeight = maxHeight;
		x = gridX * size;
		z = gridZ * size;
	}
	
	private RawModel generateTerrain(Loader loader) {
		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3],
				normals = new float[count * 3],
				textureCoords = new float[count * 2];
		heights = new float[count];
		int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
		int vertexPointer = 0;
		for(int i = 0; i < vertexCount; i++) {
			for(int j = 0; j < vertexCount; j++) {
				vertices[vertexPointer * 3] = (float)j / ((float)vertexCount - 1) * size;
				vertices[vertexPointer * 3 + 1] = baseHeight;
				vertices[vertexPointer * 3 + 2] = (float)i / ((float)vertexCount - 1) * size * Core.getAspectRatio();
				heights[vertexPointer] = maxHeight;
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float)j / ((float)vertexCount - 1);
				textureCoords[vertexPointer * 2 + 1] = (float)i / ((float)vertexCount - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz = 0; gz < vertexCount - 1; gz++) {
			for(int gx = 0; gx < vertexCount - 1; gx++) {
				int topLeft = (gz * vertexCount) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * vertexCount) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, 3, indices, textureCoords, normals);
	}
	
	public RawModel generateTerrain(String heightMap, Loader loader) {
		FloatBuffer pixels;
		int width, height;
		
		try(MemoryStack stack = MemoryStack.stackPush()){
			int[] w = new int[1], h = new int[1], comp = new int[1];
			
			pixels = STBImage.stbi_loadf(heightMap, w, h, comp, 1);
			if(pixels == null) System.err.println("Couldn't load texture : " + heightMap);
			width = w[0];
			height = h[0];
		}
		
		int count = width * height;
		float[] vertices = new float[count * 3],
				normals = new float[count * 3],
				textureCoords = new float[count * 2];
		heights = new float[count];
		int[] indices = new int[6 * (width - 1) * (height - 1)];
		int vertexPointer = 0;
		for(int i = 0; i < height ; i++) {
			for(int j = 0; j < width; j++) {
				vertices[vertexPointer * 3] = (float)j/((float)width - 1) * size;
				heights[vertexPointer] = vertices[vertexPointer * 3 + 1] = baseHeight + getHeight(j, i, pixels, width, height);
				vertices[vertexPointer * 3 + 2] = (float)i/((float)height - 1) * size;
				
				// Filling up
				if(vertexPointer >= width) {
					Vector3f normal = getNormal(j, i - 1, width, height);
					normals[vertexPointer * 3 + 0] = normal.x;
					normals[vertexPointer * 3 + 1] = normal.y;
					normals[vertexPointer * 3 + 2] = normal.z;
				}
				
				textureCoords[vertexPointer * 2 + 0] = (float)j/((float)width - 1);
				textureCoords[vertexPointer * 2 + 1] = (float)i/((float)height - 1);
				
				vertexPointer++;
			}
		}
		
		vertexPointer -= width;
		for(int i = 0; i < width; i++) {
			Vector3f normal = getNormal(i, height - 1, width, height);
			normals[vertexPointer * 3 + 0] = normal.x;
			normals[vertexPointer * 3 + 1] = normal.y;
			normals[vertexPointer * 3 + 2] = normal.z;
			vertexPointer++;
		}
		
		int pointer = 0;
		for(int gz = 0; gz < height - 1; gz++){
			for(int gx = 0; gx < width - 1; gx++){
				int topLeft = (gz * height) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * height) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, 3, indices, textureCoords, normals);
	}
	
	public RawModel generateTerrain(PerlinHeightGenerator generator, Loader loader) {
		int vertexCount = 128;
		
		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3],
				normals = new float[count * 3],
				textureCoords = new float[count * 2];
		heights = new float[count];
		int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
		int vertexPointer = 0;
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * size;
				heights[vertexPointer] = vertices[vertexPointer*3+1] = baseHeight + getHeight(j, i, generator);
				vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * size * Core.getAspectRatio();
				
				Vector3f normal = getNormal(j, i, generator);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<vertexCount-1;gz++){
			for(int gx=0;gx<vertexCount-1;gx++){
				int topLeft = (gz*vertexCount)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*vertexCount)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, 3, indices, textureCoords, normals);
	}
	
	private float getHeight(int x, int y, FloatBuffer imageBuffer, int imageWidth, int imageHeight) {
		if((x < 0 || x >= imageWidth) || (y < 0 || y >= imageHeight)) return 0;
		float height = imageBuffer.get(y * imageWidth + x);
		/* The following addition operation on the height wouldn't make sense.
		 * It is just a hack as it appears that STBImage doesn't return a linearly
		 * interpolated float color value. */
		height += 0.285F;
		height *= maxHeight * 2F;
		height -= maxHeight;
		return height;
	}
	
	private float getHeight(int x, int z, PerlinHeightGenerator generator) {
		return generator.getInterpolatedHeight(x, z);
	}
	
	/***/
	public float getHeight(float x, float z) {
		float terrainX = x - this.x , terrainZ = z - this.z;
		int row = heights.length >> 1;
		float gridSquareSize = size / (row - 1);
		int gridX = (int)Math.floor(terrainX / gridSquareSize),
			gridZ = (int)Math.floor(terrainZ / gridSquareSize);
		if(gridX >= row - 1 || gridZ >= row - 1 || gridX < 0 || gridZ < 0)
			return 0;
		float xCoord = (terrainX % gridSquareSize)/gridSquareSize,
			  zCoord = (terrainZ % gridSquareSize)/gridSquareSize;
		if(xCoord <= (1 - zCoord))
			return Maths.barrycentricInterpolation(new Vector3f(0, heights[gridZ * vertexCount + gridX], 0), new Vector3f(1, heights[gridZ * vertexCount + gridX + 1], 0), new Vector3f(0, heights[(gridZ + 1) * vertexCount + gridX], 1), new Vector2f(xCoord, zCoord));
		else
			return Maths.barrycentricInterpolation(new Vector3f(1, heights[gridZ * vertexCount + gridX + 1], 1), new Vector3f(1, heights[(gridZ + 1) * vertexCount + gridX], 1), new Vector3f(0, heights[(gridZ + 1) * vertexCount + gridX], 1), new Vector2f(xCoord, zCoord));
		
		//return heights[z * vertexCount + x];
	}
	
	private Vector3f getNormal(int x, int y, int width, int height) {
		int index = y * width + x;
		float heightL = heights[index], heightR = heights[index], heightD = heights[index], heightU = heights[index];
		if(x > 0) heightL = heights[index - 1];
		if(x < width - 1) heightR = heights[index + 1];
		if(y < height - 1) heightD = heights[(y + 1) * width + x];
		if(y > 0) heightU = heights[(y - 1) * width + x];
		return new Vector3f(heightL - heightR, 2F, heightD - heightU).normalize();
	}
	
	/*private Vector3f getNormal(int x, int y, BufferedImage image) {
		float heightL = getHeight(x-1, y, image),
			  heightR = getHeight(x+1, y, image),
			  heightD = getHeight(x, y-1, image),
			  heightU = getHeight(x, y+1, image);
		return new Vector3f(heightL - heightR, 2F, heightD - heightU).normalize();
	}*/
	
	private Vector3f getNormal(int x, int y, PerlinHeightGenerator generator) {
		float heightL = getHeight(x-1, y, generator),
			  heightR = getHeight(x+1, y, generator),
			  heightD = getHeight(x, y-1, generator),
			  heightU = getHeight(x, y+1, generator);
		return new Vector3f(heightL - heightR, 2F, heightD - heightU).normalize();
	}
	
	public float getX() {
		return x;
	}
	
	public Terrain setX(float x) {
		this.x = x;
		return this;
	}
	
	public float getZ() {
		return z;
	}
	
	public Terrain setZ(float z) {
		this.z = z;
		return this;
	}
	
	public Terrain setSize(float size) {
		this.size = size;
		return this;
	}
	
	public float getSize() {
		return size;
	}
	
	public Terrain setVertexCount(int count) {
		vertexCount = count;
		return this;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public RawModel getModel() {
		return model;
	}
	
	public Terrain setTiling(int tiling) {
		this.tiling = tiling;
		return this;
	}
	
	public float getTiling() {
		return tiling;
	}
	
	public float getMaxHeight() {
		return maxHeight;
	}
	
	public Terrain setMaxHeight(float maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}
	
	public boolean isMultiTextured() {
		return material.isMultitextured();
	}
	
	public float getBaseHeight() {
		return baseHeight;
	}
	
	public MaterialTerrain geMaterial() {
		return material;
	}
	
	public int getDiffuseTexture() {
		return material.getDiffuseTexture();
	}
	
	public int getRedTexture() {
		return material.getRedTexture();
	}
	
	public int getGreenTexture() {
		return material.getGreenTexture();
	}
	
	public int getBlueTexture() {
		return material.getBlueTexture();
	}
	
	public int getBlendMapTexture() {
		return material.getBlendMapTexture();
	}
	
}
