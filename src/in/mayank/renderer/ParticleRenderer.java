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
	
	private final RawModel quad;
	
	private final ParticleShader shader;
	
	public ParticleRenderer(final String vertexFile, final String fragmentFile, final Loader loader, final Matrix4f projectionMatrix) {
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
	
	private Matrix4f getModelViewMatrix(final Vector3f position, final Vector3f scale, final float rotation, final Matrix4f view) {
		Matrix4f model = new Matrix4f();
		model.translate(position);
		
		model.m00(view.m00()).m01(view.m10()).m02(view.m20())
			 .m10(view.m01()).m11(view.m11()).m12(view.m21())
			 .m20(view.m02()).m21(view.m12()).m22(view.m22());
		
		model.rotate(rotation, new Vector3f(0, 0, 1)).scale(scale.x, scale.y, scale.z);
		
		return view.mul(model, model);
	}
	
	public void render(final List<Particle> particles, final Matrix4f view) {
		shader.start();
		prepare();
		for(final Particle particle : particles) {
			shader.loadModelViewMatrix(getModelViewMatrix(particle.getPosition(), particle.getScale(), particle.getRotation(), view));
			shader.loadColor(particle.getColor());
			drawTriangleCall(quad);
		}
		finish();
		shader.stop();
	}
	
	private void finish() { finishRender(quad); GL11.glDepthMask(true); }
	
	public void dispose() { shader.dispose(); }
	
}

class ParticleShader extends Shader {

	public ParticleShader(final String vertexFile, final String fragmentFile) { super(vertexFile, fragmentFile); }
	
	void loadModelViewMatrix(final Matrix4f modelView) { loadUniform("modelView", modelView); }
	
	void loadColor(final Vector3f color) { loadUniform("color", color); }
	
}
