package in.mayank.extra.postprocessing;

import in.mayank.extra.utils.FBO;
import in.mayank.extra.utils.Maths;
import in.mayank.renderer.PostRenderer;
import in.mayank.shader.ShaderProgram;

public class ContrastChanger extends PostProcessing {
	
	private final PostRenderer renderer;
	private final ContrastShader shader;
	
	private float contrast = 1, brightness = 1, saturation = 1;
	
	public ContrastChanger(final String vertexFile, final String fragmentFile) {
		shader = new ContrastShader(vertexFile, fragmentFile);
		renderer = new PostRenderer();
	}
	
	public ContrastChanger(final String vertexFile, final String fragmentFile, final int width, final int height, final int samples, final int maxTargetIndex) {
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
	public ContrastChanger setContrastValues(final float contrast, final float brightness, final float saturation) {
		this.contrast = contrast;
		this.brightness = Maths.max(brightness, 0);
		this.saturation = Maths.clamp(saturation, 0, 1);
		return this;
	}
	
	public float getContrast() { return contrast; }
	
	public float getBrightness() { return brightness; }
	
	public float getSaturation() { return saturation; }
	
	public void render(final int colorTexture) {
		shader.start();
		shader.loadValues(contrast, brightness, saturation);
		bindTexture(colorTexture, 0);
		renderer.render();
		shader.stop();
	}
	
	public int getOutputTexture() { return getOutputTexture(0); }
	
	public int getOutputTexture(final int attachment) { return renderer.getOutputTexture(attachment); }
	
	public void dispose() { renderer.dispose(); shader.dispose(); }
	
}

class ContrastShader extends ShaderProgram {
	
	public ContrastShader(final String vertexFile, final String fragmentFile) { super(vertexFile, fragmentFile); }
	
	void loadValues(final float contrast, final float brightness, final float saturation) {
		loadUniform("brightness", brightness);
		loadUniform("contrast", contrast);
		loadUniform("saturation", saturation);
	}
	
}
