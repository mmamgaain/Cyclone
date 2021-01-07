package in.mayank.extra.postprocessing;

import in.mayank.extra.core.Core;
import in.mayank.extra.utils.FBO;
import in.mayank.renderer.PostRenderer;
import in.mayank.shader.ShaderProgram;

public class VerticalBlurFilter extends PostProcessing {
	
	private PostRenderer renderer;
	private VerticalBlurShader shader;
	
	public VerticalBlurFilter(String vertexFile, String fragmentFile) {
		renderer = new PostRenderer();
		shader = new VerticalBlurShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadPixelSize(Core.getHeight());
		shader.stop();
	}
	
	public VerticalBlurFilter(int width, int height, String vertexFile, String fragmentFile, int samples, int targets) {
		renderer = new PostRenderer(width, height, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_BUFFER, samples, targets);
		shader = new VerticalBlurShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadPixelSize(height);
		shader.stop();
	}
	
	public void render(int colorTexture) {
		shader.start();
		bindTexture(colorTexture, 0);
		renderer.render();
		shader.stop();
	}
	
	public int getOutputTexture() {
		return getOutputTexture(0);
	}
	
	protected int getOutputTexture(int attachment) {
		return renderer.getOutputTexture(attachment);
	}
	
	public void dispose() {
		renderer.dispose();
		shader.dispose();
	}

}

class VerticalBlurShader extends ShaderProgram {
	
	public VerticalBlurShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	void loadPixelSize(int height) {
		loadUniform("pixelSize", 1F / height);
	}
	
}
