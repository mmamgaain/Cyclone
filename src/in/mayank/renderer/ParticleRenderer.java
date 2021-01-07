package in.mayank.renderer;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import in.mayank.extra.model.Particle;
import in.mayank.extra.model.RawModel;
import in.mayank.extra.utils.Loader;
import in.mayank.shader.Shader;

public class ParticleRenderer extends Renderer {
	
	private RawModel quad;
	
	private ParticleShader shader;
	
	public ParticleRenderer(String vertexFile, String fragmentFile, Loader loader, Matrix4f projectionMatrix) {
		shader = new ParticleShader(vertexFile, fragmentFile);
		quad = loader.loadToVAO(new float[]{ -0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, -0.5F }, 2);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	private void prepare() {
		prepareRender(quad);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
	}
	
	private Matrix4f getModelViewMatrix(Vector3f position, Vector3f scale, float rotation, Matrix4f view) {
		Matrix4f model = new Matrix4f();
		model.translate(position);
		
		model.m00(view.m00()).m01(view.m10()).m02(view.m20())
			 .m10(view.m01()).m11(view.m11()).m12(view.m21())
			 .m20(view.m02()).m21(view.m12()).m22(view.m22());
		
		model.rotate(rotation, new Vector3f(0, 0, 1)).scale(scale.x, scale.y, scale.z);
		
		return view.mul(model, model);
	}
	
	public void render(List<Particle> particles, Matrix4f view) {
		shader.start();
		prepare();
		for(Particle particle : particles) {
			shader.loadModelViewMatrix(getModelViewMatrix(particle.getPosition(), particle.getScale(), particle.getRotation(), view));
			shader.loadColor(particle.getColor());
			drawTriangleCall(quad);
		}
		finish();
		shader.stop();
	}
	
	private void finish() {
		finishRender(quad);
		GL11.glDepthMask(true);
	}
	
	public void dispose() { shader.dispose(); }
	
}

class ParticleShader extends Shader {

	public ParticleShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	void loadModelViewMatrix(Matrix4f modelView) { loadUniform("modelView", modelView); }
	
	void loadColor(Vector3f color) { loadUniform("color", color); }
	
}
