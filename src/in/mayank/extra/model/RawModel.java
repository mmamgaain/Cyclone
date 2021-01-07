package in.mayank.extra.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import in.mayank.extra.utils.Loader;

public class RawModel {
	
	protected int vaoID, vertexCount;
	private int numBuffers = -1;
	
	/** Creates a RawModel with an empty VAO.
	 * 
	 * @param floatCount The maximum number of floats the VAO of this model
	 * will be capable of holding.
	 * @param loader The instance of Loader class that will create this VAO. */
	public RawModel(int floatCount, Loader loader) {
		
	}
	
	/** WARNING : THIS METHOD SHOULD NOT BE USED. IT IS
	 * STRICTLY FOR THE PURPOSE OF BEING USED INTERNALLY
	 * BY THE ENGINE.
	 * 
	 * @param vaoID This RawModel's VAO's ID.
	 * @param vertexCount The total number of vertices held by
	 * this RawModel.  */
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	/***/
	public boolean isValidNumBuffers() {
		return numBuffers > -1;
	}
	
	/** BEWARE : DO NOT USE THIS METHOD TO SET ANY VALUES. THIS
	 * METHOD IS STRICTLY TO BE USED BY INTERNAL PROCESSES. */
	public void setNumBuffers(int numBuffers) {
		this.numBuffers = numBuffers;
	}
	
	public int getNumBuffers() {
		return numBuffers;
	}
	
	public void storeDataInAttributeList(int attribNumber, int dimension, float[] data, Loader loader) {
		int vboID = GL15.glGenBuffers();
		loader.vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, storeDataInFloatBuffer(data), GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribNumber, dimension, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void bindIndicesBuffer(int[] indices, Loader loader) {
		int vboID = GL15.glGenBuffers();
		loader.vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, storeDataInIntBuffer(indices), GL15.GL_STATIC_DRAW);
	}
	
	public void unbindModel() {
		GL30.glBindVertexArray(0);
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		return (FloatBuffer)BufferUtils.createFloatBuffer(data.length).put(data).flip();
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data) {
		return (IntBuffer)BufferUtils.createIntBuffer(data.length).put(data).flip();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(vaoID, vertexCount);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RawModel))
			return false;
		RawModel other = (RawModel) obj;
		return vaoID == other.vaoID && vertexCount == other.vertexCount;
	}
	
}
