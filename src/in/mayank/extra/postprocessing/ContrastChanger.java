package in.mayank.extra.postprocessing;

import in.mayank.extra.utils.FBO;
import in.mayank.extra.utils.Maths;
import in.mayank.renderer.PostRenderer;
import in.mayank.shader.ShaderProgram;

public class ContrastChanger extends PostProcessing {
	
	private PostRenderer renderer;
	private ContrastShader shader;
	
	private float contrast = 1, brightness = 1, saturation = 1;
	
	public ContrastChanger(String vertexFile, String fragmentFile) {
		shader = new ContrastShader(vertexFile, fragmentFile);
		renderer = new PostRenderer();
	}
	
	public ContrastChanger(String vertexFile, String fragmentFile, int width, int height, int samples, int maxTargetIndex) {
		shader = new ContrastShader(vertexFile, fragmentFile);
		renderer = new PostRenderer(width, height, FBO.COLOR_ATTACHMENT_TEXTURE, FBO.DEPTH_ATTACHMENT_BUFFER, samples, maxTargetIndex);
	}
	
	/** Sets the contrast values.
	 * 
	 * @param contrast The sharpness between different colours. A value of 1 has no effect(default). It has no range.
	 * 				   At zero, there is no contrast (separation) between colours, so all the colours blend together
	 * 				   to a neutral grey. At increasing positive values, the contrast sharpens and colours appear
	 * 				   more different. At negative values, flipped colours appear.
	 * @param brightness Brightness of the colour. A value of 1 has no effect(default). Minimum possible value is 0.
	 * @param saturation The dullness of the colour. A value of 1 has no effect(default). Ranges between 0 and 1. */
	public ContrastChanger setContrastValues(float contrast, float brightness, float saturation) {
		this.contrast = contrast;
		this.brightness = Maths.max(brightness, 0);
		this.saturation = Maths.clamp(saturation, 0, 1);
		return this;
	}
	
	public float getContrast() {
		return contrast;
	}
	
	public float getBrightness() {
		return brightness;
	}
	
	public float getSaturation() {
		return saturation;
	}
	
	public void render(int colorTexture) {
		shader.start();
		shader.loadValues(contrast, brightness, saturation);
		bindTexture(colorTexture, 0);
		renderer.render();
		shader.stop();
	}
	
	public int getOutputTexture() {
		return getOutputTexture(0);
	}
	
	public int getOutputTexture(int attachment) {
		return renderer.getOutputTexture(attachment);
	}
	
	public void dispose() {
		renderer.dispose();
		shader.dispose();
	}
	
}

class ContrastShader extends ShaderProgram {
	
	public ContrastShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	void loadValues(float contrast, float brightness, float saturation) {
		loadUniform("brightness", brightness);
		loadUniform("contrast", contrast);
		loadUniform("saturation", saturation);
	}
	
	protected void bindAttributes() {}
	
}
