package in.mayank.shader;

import org.joml.Matrix4f;

public class Shader extends ShaderProgram {
	
	public Shader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	public void loadTime(float time) {
		loadUniform("time", time);
	}
	
	public void loadScreenSize(float width, float height) {
		loadUniform("width", width);
		loadUniform("height", height);
	}
	
	public void loadProjectionMatrix(Matrix4f project) {
		loadUniform("project", project);
	}
	
	public void loadViewMatrix(Matrix4f view) {
		loadUniform("view", view);
	}
	
	public void loadModelMatrix(Matrix4f transform) {
		loadUniform("transform", transform);
	}

}
