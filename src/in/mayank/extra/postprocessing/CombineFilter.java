package in.mayank.extra.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import in.mayank.extra.utils.FBO;
import in.mayank.renderer.PostRenderer;
import in.mayank.shader.Shader;

public class CombineFilter {
	
	private final PostRenderer renderer;
	private final Shader shader;
	
	public CombineFilter(final String vertexFile, final String fragmentFile) { shader = new Shader(vertexFile, fragmentFile); renderer = new PostRenderer(); }
	
	public CombineFilter(final int width, final int height, final String vertexFile, final String fragmentFile, final int samples, final int maxTargets) {
		shader = new Shader(vertexFile, fragmentFile);
		renderer = new PostRenderer(width, height, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_BUFFER, samples, maxTargets);
	}
	
	public void render(final int texture0, final int texture1) {
		shader.start();
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0);
		GL20.glActiveTexture(GL20.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture1);
		renderer.render();
		shader.stop();
	}
	
	public int getOutputTexture(final int attachment) { return renderer.getOutputTexture(attachment); }
	
	public void dispose() { renderer.dispose(); shader.dispose(); }
	
}
