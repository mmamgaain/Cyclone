package in.mayank.renderer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import in.mayank.extra.model.RawModel;

public class Renderer {
	
	private static int MAX_BUFFER_SIZE = -1;
	public static final Matrix4f PLACEHOLDER_MATRIX = new Matrix4f();
	private boolean renderLines = false, renderPoint = false;
	
	/** Clears the entire drawing screen of the currently bound frame-buffer so that the next frame can be drawn. */
	public static void startFrame() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	/** Enables back-face culling */
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	/** Disables back-face culling */
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	/**  */
	public void loadTexture2D(int index, int textureID) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	/***/
	public void loadTexture3D(int index, int textureID) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL13.GL_TEXTURE_3D, textureID);
	}
	
	/***/
	public void loadTextureCubeMap(int index, int textureID) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
	}
	
	/** Binds the VAO of the {@link RawModel} provided and enables the VBO(s)
	 * associated with it.
	 * 
	 * @param model The {@link RawModel} that needs to be bound. */
	protected void prepareRender(RawModel model) {
		if(MAX_BUFFER_SIZE == -1) MAX_BUFFER_SIZE = GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS);
		GL30.glBindVertexArray(model.getVaoID());
		
		// Enabling the buffers attached to this vertex array
		if(model.isValidNumBuffers()) for(int i = 0; i <= model.getNumBuffers(); i++) GL20.glEnableVertexAttribArray(i);
		else {
			int i = 0;
			for(; i < MAX_BUFFER_SIZE; i++) {
				if(GL20.glGetVertexAttribi(i, GL20.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING) != GL11.GL_FALSE)
					GL20.glEnableVertexAttribArray(i);
				else break;
			}
			model.setNumBuffers(i - 1);
		}
	}
	
	/**  */
	protected void drawTriangleCall(RawModel model) {
		if(GL11.glGetBoolean(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING)) GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		else GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, model.getVertexCount());
	}
	
	protected void drawLineCall(RawModel model) {
		if(GL11.glGetBoolean(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING)) GL11.glDrawElements(GL11.GL_LINES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		else GL11.glDrawArrays(GL11.GL_LINE_STRIP, 0, model.getVertexCount());
	}
	
	protected void drawPointCall(RawModel model) {
		GL11.glDrawArrays(GL11.GL_POINTS, 0, model.getVertexCount());
	}
	
	/** Disables all the enabled VBO(s) and unbinds the VAO.
	 * 
	 * @param model The {@link RawModel} that needs to be unbound. */
	protected void finishRender(RawModel model) {
		// Disabling the buffers attached to this vertex array
		for(int i = 0; i <= model.getNumBuffers(); i++) GL20.glDisableVertexAttribArray(i);
		renderPoint = false;
		renderLines = false;
		GL30.glBindVertexArray(0);
	}
	
	/**  */
	public void renderTriangle(RawModel model) {
		prepareRender(model);
		drawTriangleCall(model);
		finishRender(model);
	}
	
	public void renderLines(RawModel model) {
		if(!renderLines) { renderLines = true; GL11.glEnable(GL11.GL_LINE_SMOOTH); }
		prepareRender(model);
		drawLineCall(model);
		finishRender(model);
	}
	
	public void renderPoints(RawModel model) {
		if(!renderPoint) { renderPoint = true; GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE); }
		prepareRender(model);
		drawPointCall(model);
		finishRender(model);
	}
	
}
