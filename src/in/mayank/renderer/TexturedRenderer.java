package in.mayank.renderer;

import org.joml.Matrix4f;

import in.mayank.extra.core.Core;
import in.mayank.extra.model.TexturedModel;
import in.mayank.shader.Shader;

public class TexturedRenderer extends Renderer {
	
	private final Shader shader;
	
	public TexturedRenderer(String vertexFile, String fragmentFile) { this(vertexFile, fragmentFile, PLACEHOLDER_MATRIX); }
	
	public TexturedRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projectionMatrix) {
		shader = new Shader(vertexFile, fragmentFile);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	protected void prepareRender(final TexturedModel model, final Matrix4f transform, final Matrix4f view) {
		prepareRender(model);
		loadTexture2D(0, model.getTexture());
		shader.loadModelMatrix(transform);
		shader.loadViewMatrix(view);
		shader.loadTime((float)Core.getElapsedTimeInSeconds());
	}
	
	public void render(final TexturedModel model) { render(model, PLACEHOLDER_MATRIX, PLACEHOLDER_MATRIX); }
	
	public void render(final TexturedModel model, final Matrix4f transform, final Matrix4f view) {
		shader.start();
		prepareRender(model, transform, view);
		drawTriangleCall(model.getModel());
		finishRender(model.getModel());
		shader.stop();
	}
	
	public TexturedRenderer loadProjectionMatrix(final Matrix4f projection) {
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
		return this;
	}
	
	public void dispose() { shader.dispose(); }
	
}
