package in.mayank.renderer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import in.mayank.extra.core.Core;
import in.mayank.extra.model.RawModel;
import in.mayank.extra.utils.Loader;
import in.mayank.extra.utils.Maths;
import in.mayank.shader.Shader;

public class SkyboxRenderer extends Renderer {
	
	public static final int MOVE_DIRECTION_NONE = 0, MOVE_DIRECTION_ANTICLOCKWISE = 1, MOVE_DIRECTION_CLOCKWISE = 2;
	public static float size = 500F;
	
	private static final float[] VERTICES = {
		    -size,  size, -size,
		    -size, -size, -size,
		     size, -size, -size,
		     size, -size, -size,
		     size,  size, -size,
		    -size,  size, -size,

		    -size, -size,  size,
		    -size, -size, -size,
		    -size,  size, -size,
		    -size,  size, -size,
		    -size,  size,  size,
		    -size, -size,  size,

		     size, -size, -size,
		     size, -size,  size,
		     size,  size,  size,
		     size,  size,  size,
		     size,  size, -size,
		     size, -size, -size,

		    -size, -size,  size,
		    -size,  size,  size,
		     size,  size,  size,
		     size,  size,  size,
		     size, -size,  size,
		    -size, -size,  size,

		    -size,  size, -size,
		     size,  size, -size,
		     size,  size,  size,
		     size,  size,  size,
		    -size,  size,  size,
		    -size,  size, -size,

		    -size, -size, -size,
		    -size, -size,  size,
		     size, -size, -size,
		     size, -size, -size,
		    -size, -size,  size,
		     size, -size,  size
	};
	private final RawModel model;
	private int textureID;
	private final SkyboxShader shader;
	private float speed = 0;
	private int direction = MOVE_DIRECTION_NONE;
	
	public SkyboxRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projection, final Loader loader, final int skyCubeTexture) {
		model = loader.loadToVAO(VERTICES, 3);
		shader = new SkyboxShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
		textureID = skyCubeTexture;
	}
	
	/** Creates a Skybox renderer with the supplied parameters.
	 * 
	 * @param vertexFile The vertex shader file path.
	 * @param fragmentFile The fragment shader file path.
	 * @param projection The Matrix4f which serves as the projection matrix.
	 * @param loader The Loader object to be used to load the sky assets.
	 * @param textureFiles The array of the string paths of the six textures to be 
	 * mapped to the skybox cube model. They have to be supplied in the following order:
	 * <ol>
	 * 1. Right face.<br>
	 * 2. Left face.<br>
	 * 3. Top face.<br>
	 * 4. Bottom face.<br>
	 * 5. Back face.<br>
	 * 6. Front face.
	 * </ol> */
	public SkyboxRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projection, final Loader loader, final String[] textureFiles) {
		this(vertexFile, fragmentFile, projection, loader, loader.loadTextureCubeMap(textureFiles));
	}
	
	public SkyboxRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projection, final Loader loader, final String filename) {
		this(vertexFile, fragmentFile, projection, loader, loader.loadTextureCubeMap(filename));
	}
	
	public SkyboxRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projection, final Loader loader, final String[] textureFiles, final float levelOfDetail) {
		this(vertexFile, fragmentFile, projection, loader, loader.loadTextureCubeMap(textureFiles, levelOfDetail));
	}
	
	public SkyboxRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projection, final Loader loader, final String filename, final float levelOfDetail) {
		this(vertexFile, fragmentFile, projection, loader, loader.loadTextureCubeMap(filename, levelOfDetail));
	}
	
	public SkyboxRenderer setTexture(final String[] textureFiles, final Loader loader) { textureID = loader.loadTextureCubeMap(textureFiles); return this; }
	
	public SkyboxRenderer setTexture(final String filename, final Loader loader) { textureID = loader.loadTextureCubeMap(filename); return this; }
	
	public int getTexture() { return textureID; }
	
	public SkyboxRenderer setRotation(final float speed, final int direction) { this.speed = speed; this.direction = direction; return this; }
	
	public float getSpeed() { return speed; }
	
	public int getDirection() { return direction; }
	
	protected void prepareRender(final RawModel model, final Matrix4f view) {
		super.prepareRender(model);
		GL11.glDepthMask(false);	// Stop the sky-box from being written to the depth buffer
		GL11.glDepthRange(0.9999999F, 0.9999999F);	// Sets the depth to maximum so it doesn't clip anything
		shader.setRotation(speed, direction);
		shader.loadViewMatrix(view);
		loadTextureCubeMap(0, textureID);
	}
	
	protected void finishRender(RawModel model) {
		GL11.glDepthRange(0F, 1F);	// Restoring depth range
		GL11.glDepthMask(true);	// Restoring depth mask
		super.finishRender(model);
	}
	
	public void render(Matrix4f view) {
		shader.start();
		prepareRender(model, view);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		finishRender(model);
		shader.stop();
	}
	
	public void dispose() {
		shader.dispose();
	}
	
}

class SkyboxShader extends Shader {
	
	public int direction;
	public float rotationSpeed = 0, rotation = 0;
	
	public SkyboxShader(final String vertexFile, final String fragmentFile) {
		super(vertexFile, fragmentFile);
		direction = SkyboxRenderer.MOVE_DIRECTION_NONE;
	}
	
	void setRotation(final float speed, final int direction) { rotationSpeed = speed; this.direction = direction; }
	
	public void loadViewMatrix(final Matrix4f viewMatrix) {
		Matrix4f view = new Matrix4f(viewMatrix);
		view.m30(0);
		view.m31(0);
		view.m32(0);
		if(direction == SkyboxRenderer.MOVE_DIRECTION_ANTICLOCKWISE)
			rotation += rotationSpeed * Maths.max((float)Core.getDeltaTimeInSeconds(), 0.016F);
		else if(direction == SkyboxRenderer.MOVE_DIRECTION_CLOCKWISE)
			rotation -= rotationSpeed * Maths.max((float)Core.getDeltaTimeInSeconds(), 0.016F);
		view.rotate((float)Math.toRadians(rotation), 0, 1, 0);
		loadUniform("view", view);
	}
	
}
