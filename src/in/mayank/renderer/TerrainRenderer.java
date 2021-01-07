package in.mayank.renderer;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import in.mayank.extra.core.Core;
import in.mayank.extra.model.RawModel;
import in.mayank.extra.model.Terrain;
import in.mayank.extra.texture.MaterialTerrain;
import in.mayank.extra.utils.Light;
import in.mayank.extra.utils.Maths;
import in.mayank.shader.Shader;

public class TerrainRenderer extends Renderer {
	
	private TerrainShader shader;
	private float ambientLightIntensity;
	private Light light;
	
	protected float fogDensity = 0F, fogGradient = 1F;
	
	public TerrainRenderer(String vertexFile, String fragmentFile, Matrix4f projection) {
		shader = new TerrainShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
		ambientLightIntensity = 0.2F;
		light = new Light();
	}
	
	public TerrainRenderer setFogVariables(float density, float gradient) {
		fogDensity = density;
		fogGradient = gradient;
		return this;
	}
	
	public float getFogDensity() {
		return fogDensity;
	}
	
	public float getFogGradient() {
		return fogGradient;
	}
	
	public TerrainRenderer setLight(Light light) {
		this.light.set(light);
		return this;
	}
	
	public TerrainRenderer setLight(Vector3f lightPos, Vector3f lightColor) {
		this.light.setPosition(lightPos).setColor(lightColor);
		return this;
	}
	
	public TerrainRenderer increaseLight(float delPosX, float delPosY, float delPosZ, float delColR, float delColG, float delColB) {
		light.increasePosition(delPosX, delPosY, delPosZ).increaseColor(delColR, delColG, delColB);
		return this;
	}
	
	public TerrainRenderer setAmbientLightIntensity(float ambient) {
		ambientLightIntensity = Maths.clamp(ambient, 0, 1);
		return this;
	}
	
	private void prepareRender(Terrain terrain, Matrix4f view) {
		prepareRender(terrain.getModel());
		MaterialTerrain material = terrain.geMaterial();
		loadTexture2D(0, material.getDiffuseTexture());
		shader.loadModelMatrix(Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), new Vector3f(), new Vector3f(1)));
		shader.loadViewMatrix(view);
		shader.loadTime((float)Core.getElapsedTimeInSeconds());
		shader.loadAmbientLightIntensity(ambientLightIntensity);
		shader.loadLight(light);
		shader.loadTiling(terrain.getTiling());
		shader.loadShineValues(material.getShineDamper(), material.getReflectivity());
		shader.loadFogVariables(fogDensity, fogGradient);
		shader.loadSkyColor(Core.getRed(), Core.getGreen(), Core.getBlue());
		shader.loadIsMultitextured(material.isMultitextured());
	}
	
	private void prepareInstance(Terrain terrain) {
		if(terrain.isMultiTextured()) {
			loadTexture2D(0, terrain.getDiffuseTexture());
			loadTexture2D(1, terrain.getRedTexture());
			loadTexture2D(2, terrain.getGreenTexture());
			loadTexture2D(3, terrain.getBlueTexture());
			loadTexture2D(4, terrain.getBlendMapTexture());
			shader.loadShineValues(1, 0);
		}
		else {
			MaterialTerrain material = terrain.geMaterial();
			loadTexture2D(0, material.getDiffuseTexture());
			shader.loadShineValues(material.getShineDamper(), material.getReflectivity());
		}
		shader.loadModelMatrix(Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), new Vector3f(), new Vector3f(1)));
		shader.loadTiling(terrain.getTiling());
		shader.loadIsMultitextured(terrain.isMultiTextured());
	}
	
	public void render(List<Terrain> terrains, Matrix4f view, Vector4f clipPlane) {
		shader.start();
		shader.loadViewMatrix(view);
		shader.loadTime((float)Core.getElapsedTimeInSeconds());
		shader.loadAmbientLightIntensity(ambientLightIntensity);
		shader.loadLight(light);
		shader.loadFogVariables(fogDensity, fogGradient);
		shader.loadSkyColor(Core.getRed(), Core.getGreen(), Core.getBlue());
		shader.loadClipPlane(clipPlane);
		for(Terrain terrain : terrains) {
			RawModel model = terrain.getModel();
			prepareRender(model);
			prepareInstance(terrain);
			drawTriangleCall(model);
			finishRender(model);
		}
		shader.stop();
	}
	
	public void render(Terrain terrain, Matrix4f view) {
		shader.start();
		prepareRender(terrain, view);
		drawTriangleCall(terrain.getModel());
		finishRender(terrain.getModel());
		shader.stop();
	}
	
	public void dispose() {
		shader.dispose();
	}
	
}

class TerrainShader extends Shader {
	
	public TerrainShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
		remapTextureSamplerName(0, "material.diffuse");
		remapTextureSamplerName(1, "material.red");
		remapTextureSamplerName(2, "material.green");
		remapTextureSamplerName(3, "material.blue");
		remapTextureSamplerName(4, "material.blendMap");
	}
	
	void loadClipPlane(Vector4f clipPlane) {
		loadUniform("clipPlane", clipPlane);
	}
	
	void loadIsMultitextured(boolean is) {
		loadUniform("material.multitextured", is);
	}
	
	void loadSkyColor(float red, float green, float blue) {
		loadUniform("skyColor", red, green, blue);
	}
	
	void loadFogVariables(float density, float gradient) {
		loadUniform("fogDensity", density);
		loadUniform("fogGradient", gradient);
	}
	
	void loadShineValues(float shineDamper, float reflectivity) {
		loadUniform("material.shineDamper", shineDamper);
		loadUniform("material.reflectivity", reflectivity);
	}
	
	void loadTiling(float tiling) {
		loadUniform("material.tiling", tiling);
	}
	
	void loadLight(Light light) {
		loadUniform("lightPos", light.getPosition());
		loadUniform("lightColor", light.getColor());
		loadUniform("lightAttenuation", light.getAttenuation());
	}
	
	void loadAmbientLightIntensity(float ambient) {
		loadUniform("ambientLightIntensity", ambient);
	}
	
}
