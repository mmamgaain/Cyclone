package in.mayank.extra.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import in.mayank.extra.utils.FBO;
import in.mayank.renderer.PostRenderer;
import in.mayank.shader.Shader;

public class CombineFilter {
	
	private PostRenderer renderer;
	private Shader shader;
	
	public CombineFilter(String vertexFile, String fragmentFile) {
		shader = new Shader(vertexFile, fragmentFile);
		renderer = new PostRenderer();
	}
	
	public CombineFilter(int width, int height, String vertexFile, String fragmentFile, int samples, int maxTargets) {
		shader = new Shader(vertexFile, fragmentFile);
		renderer = new PostRenderer(width, height, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_BUFFER, samples, maxTargets);
	}
	
	public void render(int texture0, int texture1) {
		shader.start();
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0);
		GL20.glActiveTexture(GL20.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture1);
		renderer.render();
		shader.stop();
	}
	
	public int getOutputTexture(int attachment) {
		return renderer.getOutputTexture(attachment);
	}
	
	public void dispose() {
		renderer.dispose();
		shader.dispose();
	}
	
}
