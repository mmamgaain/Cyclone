package in.mayank.renderer;

import static in.mayank.extra.texture.Material.NO_TEXTURE;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import in.mayank.extra.core.Core;
import in.mayank.extra.model.Entity;
import in.mayank.extra.model.TexturedModel;
import in.mayank.extra.texture.Material;
import in.mayank.extra.utils.Light;
import in.mayank.extra.utils.Maths;
import in.mayank.shader.Shader;

public class EntityRenderer extends Renderer {
	
	private EntityShader shader;
	protected Light light = null;
	protected float ambientLightIntensity;
	protected int staticEnviroMap = NO_TEXTURE;
	
	protected float fogDensity = 0F, fogGradient = 1F;
	
	public EntityRenderer(String vertexFile, String fragmentFile, Matrix4f projection) {
		shader = new EntityShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
		light = new Light();
		ambientLightIntensity = 0.2F;
	}
	
	public EntityRenderer setFogVariables(float density, float gradient) {
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
	
	public float getAmbientLightIntensity() {
		return ambientLightIntensity;
	}
	
	public EntityRenderer setAmbientLightIntensity(float ambientLightIntensity) {
		this.ambientLightIntensity = Maths.clamp(ambientLightIntensity, 0, 1);
		return this;
	}
	
	public Light getLight() {
		return light;
	}
	
	public EntityRenderer setLight(Light light) {
		this.light.set(light);
		return this;
	}
	
	public EntityRenderer setLight(Vector3f position, Vector3f color) {
		light.setPosition(position).setColor(color);
		return this;
	}
	
	public EntityRenderer increaseLight(Vector3f position, Vector3f color) {
		light.increasePosition(position).increaseColor(color);
		return this;
	}
	
	public EntityRenderer increaseLight(float delPosX, float delPosY, float delPosZ, float delColR, float delColG, float delColB) {
		light.increasePosition(delPosX, delPosY, delPosZ).increaseColor(delColR, delColG, delColB);
		return this;
	}
	
	public EntityRenderer setStaticEnvironmentMap(int enviroMap) {
		staticEnviroMap = enviroMap;
		return this;
	}
	
	public EntityRenderer removeStaticEnvironmentMap() {
		staticEnviroMap = NO_TEXTURE;
		return this;
	}
	
	public boolean hasStaticEnvironmentMap() {
		return staticEnviroMap > NO_TEXTURE;
	}
	
	protected void prepareRender(Entity entity, Matrix4f view) {
		prepareRender(entity.getModel());
		Material material = entity.getMaterial();
		loadTexture2D(0, material.getDiffuseTexture());
		loadTexture2D(1, material.getNormalMap());
		loadTexture2D(2, material.getSpecularMap());
		loadTextureCubeMap(3, staticEnviroMap);
		shader.loadHasEnviroMap(hasStaticEnvironmentMap());
		shader.loadMaterial(material);
		shader.loadTime((float)Core.getElapsedTimeInSeconds());
		shader.loadModelMatrix(Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale()));
		shader.loadViewMatrix(view);
		shader.loadLight(light, view, material.hasNormalMap());
		shader.loadAmbientLightIntensity(ambientLightIntensity);
		shader.loadFogVariables(fogDensity, fogGradient);
		shader.loadSkyColor(Core.getRed(), Core.getGreen(), Core.getBlue());
		if(material.isDoubleSided()) disableCulling();
	}
	
	protected void prepareTexturedModel(TexturedModel model) {
		prepareRender(model.getModel());
		Material material = model.getMaterial();
		loadTexture2D(0, material.getDiffuseTexture());
		loadTexture2D(1, material.getNormalMap());
		loadTexture2D(2, material.getSpecularMap());
		shader.loadMaterial(material);
		if(material.isDoubleSided()) disableCulling();
	}
	
	protected void prepareInstance(Entity entity) {
		shader.loadModelMatrix(Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale()));
		shader.loadTextureAtlasOffset(entity.getTextureOffset());
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities, Matrix4f view, Vector4f clipPlane) {
		shader.start();
		shader.loadViewMatrix(view);
		shader.loadTime((float)Core.getElapsedTimeInSeconds());
		shader.loadAmbientLightIntensity(ambientLightIntensity);
		shader.loadFogVariables(fogDensity, fogGradient);
		shader.loadSkyColor(Core.getRed(), Core.getGreen(), Core.getBlue());
		shader.loadHasEnviroMap(hasStaticEnvironmentMap());
		if(hasStaticEnvironmentMap()) loadTextureCubeMap(3, staticEnviroMap);
		shader.loadClipPlane(clipPlane);
		for(TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			shader.loadLight(light, view, model.getMaterial().hasNormalMap());
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch) {
				prepareInstance(entity.update());
				drawTriangleCall(entity.getModel());
			}
			enableCulling();
			finishRender(model.getModel());
		}
		shader.stop();
	}
	
	public void render(Entity entity, Matrix4f view) {
		shader.start();
		prepareRender(entity, view);
		drawTriangleCall(entity.getModel());
		finishRender(entity.getModel());
		shader.stop();
	}
	
	public void dispose() {
		shader.dispose();
	}
	
}

class EntityShader extends Shader {
	
	public EntityShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
		remapTextureSamplerName(0, "material.texture0");
		remapTextureSamplerName(1, "material.normalMap");
		remapTextureSamplerName(2, "material.specularMap");
	}
	
	void loadClipPlane(Vector4f clipPlane) {
		loadUniform("clipPlane", clipPlane);
	}
	
	void loadMaterial(Material material) {
		loadUniform("material.hasDiffuseTexture", material.hasDiffuseTexture());
		loadUniform("material.hasNormalMap", material.hasNormalMap());
		loadUniform("hasNormalMap", material.hasNormalMap());
		loadUniform("material.hasSpecularMap", material.hasSpecularMap());
		loadUniform("isTransparent", material.hasTransparency());
		loadUniform("material.isTransparent", material.hasTransparency());
		loadUniform("material.shineDamper", material.getShineDamper());
		loadUniform("material.reflectivity", material.getSpecularReflectivity());
		loadUniform("enviroRefractivity", material.getEnviroRefractivity());
		loadUniform("material.transparency", material.getTransparency());
		loadUniform("material.diffuse", material.getDiffuseColor());
		loadUniform("material.ambient", material.getAmbientColor());
		loadUniform("material.specular", material.getSpecularColor());
		loadUniform("numberOfRows", material.getNumberOfRows());
	}
	
	void loadSkyColor(float red, float green, float blue) {
		loadUniform("skyColor", red, green, blue);
	}
	
	void loadFogVariables(float density, float gradient) {
		loadUniform("fogDensity", density);
		loadUniform("fogGradient", gradient);
	}
	
	void loadTextureAtlasOffset(Vector2f offset) {
		loadUniform("offset", offset);
	}
	
	void loadLight(Light light, Matrix4f view, boolean inEyeSpace) {
		loadUniform("lightPos", light.getPosition());
		if(inEyeSpace) loadUniform("lightPosEyeSpace", getLightPositionEyeSpace(light.getPosition(), view));
		loadUniform("lightColor", light.getColor());
		loadUniform("lightAttenuation", light.getAttenuation());
	}
	
	private Vector3f getLightPositionEyeSpace(Vector3f pos, Matrix4f view) {
		Vector4f eyeSpacePos = new Vector4f(pos.x, pos.y, pos.z, 1F);
		view.transform(eyeSpacePos, eyeSpacePos);
		return new Vector3f(eyeSpacePos.x, eyeSpacePos.y, eyeSpacePos.z);
	}
	
	void loadAmbientLightIntensity(float ambient) {
		loadUniform("ambientLightIntensity", ambient);
	}
	
	void loadHasEnviroMap(boolean enviroMap) {
		loadUniform("hasEnviroMap", enviroMap);
	}
	
}
