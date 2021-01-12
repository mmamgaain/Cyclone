package in.mayank.renderer;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import in.mayank.extra.core.Core;
import in.mayank.extra.model.RawModel;
import in.mayank.extra.model.WaterTile;
import in.mayank.extra.utils.Light;
import in.mayank.extra.utils.Loader;
import in.mayank.extra.utils.Maths;
import in.mayank.extra.utils.WaterFBO;
import in.mayank.shader.Shader;

public class WaterRenderer extends Renderer {
	
	private final RawModel quad;
	private final WaterShader shader;
	private final WaterFBO fbo;
	
	public WaterRenderer(final String vertexFile, final String fragmentFile, final WaterFBO fbo, final Matrix4f projection, final Loader loader) {
		quad = loader.loadToVAO(new float[] {-1, -1, -1, 1, 1, -1, 1, 1}, 2);
		shader = new WaterShader(vertexFile, fragmentFile);
		this.fbo = fbo;
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
	}
	
	protected void prepareRender(final RawModel model, final Vector3f cameraPos, final Light light, final Matrix4f view) {
		super.prepareRender(model);
		shader.loadCameraPosition(cameraPos);
		shader.loadLight(light);
		shader.loadViewMatrix(view);
		loadTexture2D(0, fbo.getReflectionTexture());
		loadTexture2D(1, fbo.getRefractionTexture());
		loadTexture2D(2, fbo.getRefractionDepthTexture());
		shader.loadNearFarValues(Core.getNearValue(), Core.getFarValue());
		if(!GL11.glIsEnabled(GL11.GL_BLEND)) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	
	private void prepareInstance(final WaterTile tile) {
		if(tile.hasDistortionMap()) loadTexture2D(3, tile.getDistortionMap());
		if(tile.hasNormalMap()) loadTexture2D(4, tile.getNormalMap());
		shader.loadMaterial(tile);
		shader.loadModelMatrix(Maths.createTransformationMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), new Vector3f(0), new Vector3f(tile.getSizeX(), 0, tile.getSizeZ())));
	}
	
	public void render(final List<WaterTile> tiles, final Matrix4f view, final Light light, final Vector3f cameraPos) {
		shader.start();
		prepareRender(quad, cameraPos, light, view);
		for(final WaterTile tile : tiles) {
			prepareInstance(tile.update());
			drawTriangleCall(quad);
		}
		finishRender(quad);
		shader.stop();
	}
	
	public void dispose() { shader.dispose(); fbo.dispose(); }
	
}

class WaterShader extends Shader {
	
	public WaterShader(final String vertexFile, final String fragmentFile) {
		super(vertexFile, fragmentFile);
		remapTextureSamplerName(0, "material.reflectionTexture");
		remapTextureSamplerName(1, "material.refractionTexture");
		remapTextureSamplerName(2, "material.depthMap");
		remapTextureSamplerName(3, "material.distortionMap");
		remapTextureSamplerName(4, "material.normalMap");
	}
	
	void loadMaterial(final WaterTile tile) {
		loadUniform("material.waterColor", tile.getWaterColor());
		loadUniform("material.maxDepth", tile.getMaxDistance());
		loadUniform("material.shineDamper", tile.getShineDamper());
		loadUniform("material.reflectivity", tile.getReflectivity());
		loadUniform("material.clarity", tile.getClarity());
		loadUniform("material.waveStrength", tile.getWaveStrength());
		loadUniform("material.moveFactor", tile.getMoveFactor());
		loadUniform("material.usingDistortion", tile.hasDistortionMap());
		loadUniform("material.usingNormal", tile.hasNormalMap());
		loadUniform("tiling", tile.getTiling());
		loadUniform("material.chaos", tile.getDistortionChaos());
	}
	
	void loadNearFarValues(final float near, final float far) { loadUniform("near", near); loadUniform("far", far); }
	
	void loadLight(final Light light) { loadUniform("lightPos", light.getPosition()); loadUniform("lightColor", light.getColor()); }
	
	void loadCameraPosition(final Vector3f pos) { loadUniform("cameraPos", pos); }
	
}
