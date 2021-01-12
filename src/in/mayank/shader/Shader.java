package in.mayank.shader;

import org.joml.Matrix4f;

public class Shader extends ShaderProgram {
	
	public Shader(final String vertexFile, final String fragmentFile) { super(vertexFile, fragmentFile); }
	
	public void loadTime(final float time) { loadUniform("time", time); }
	
	public void loadScreenSize(final float width, final float height) { loadUniform("width", width); loadUniform("height", height); }
	
	public void loadProjectionMatrix(final Matrix4f project) { loadUniform("project", project); }
	
	public void loadViewMatrix(final Matrix4f view) { loadUniform("view", view); }
	
	public void loadModelMatrix(final Matrix4f transform) { loadUniform("transform", transform); }

}
