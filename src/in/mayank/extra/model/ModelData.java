package in.mayank.extra.model;

public class ModelData {
	
	private float[] vertices, textureCoords, normals, tangents;
	private int[] indices;
	private float furthestPoint;
	
	public ModelData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, float furthestPoint) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.furthestPoint = furthestPoint;
	}
	
	public float[] getTangents() {
		return tangents;
	}
	
	public float[] getVertices() {
		return vertices;
	}
	
	public float[] getTextureCoords() {
		return textureCoords;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public int[] getIndices() {
		return indices;
	}
	
	public float getFurthestPoint() {
		return furthestPoint;
	}
	
}
