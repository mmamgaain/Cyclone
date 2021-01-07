package in.mayank.extra.postprocessing;

import in.mayank.extra.core.Core;
import in.mayank.extra.utils.FBO;
import in.mayank.renderer.PostRenderer;
import in.mayank.shader.ShaderProgram;

public class HorizontalBlurFilter extends PostProcessing {
	
	private PostRenderer renderer;
	private HorizontalBlurShader shader;
	
	public HorizontalBlurFilter(String vertexFile, String fragmentFile) {
		renderer = new PostRenderer();
		shader = new HorizontalBlurShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadPixelSize(Core.getWidth());
		shader.stop();
	}
	
	public HorizontalBlurFilter(int width, int height, String vertexFile, String fragmentFile, int samples, int targets) {
		renderer = new PostRenderer(width, height, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_BUFFER, samples, targets);
		shader = new HorizontalBlurShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadPixelSize(width);
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

class HorizontalBlurShader extends ShaderProgram {
	
	HorizontalBlurShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	void loadPixelSize(int width) {
		loadUniform("pixelSize", 1F / width);
	}
	
}
