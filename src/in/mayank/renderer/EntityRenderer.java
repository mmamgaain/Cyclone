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
	
	private final EntityShader shader;
	protected final Light light;
	protected float ambientLightIntensity;
	protected int staticEnviroMap = NO_TEXTURE;
	
	protected float fogDensity = 0F, fogGradient = 1F;
	
	public EntityRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projection) {
		shader = new EntityShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
		light = new Light();
		ambientLightIntensity = 0.2F;
	}
	
	public EntityRenderer setFogVariables(final float density, final float gradient) {
		fogDensity = density;
		fogGradient = gradient;
		return this;
	}
	
	public float getFogDensity() { return fogDensity; }
	
	public float getFogGradient() { return fogGradient; }
	
	public float getAmbientLightIntensity() { return ambientLightIntensity; }
	
	public EntityRenderer setAmbientLightIntensity(final float ambientLightIntensity) { this.ambientLightIntensity = Maths.clamp(ambientLightIntensity, 0, 1); return this; }
	
	public Light getLight() { return light; }
	
	public EntityRenderer setLight(final Light light) { this.light.set(light); return this; }
	
	public EntityRenderer setLight(final Vector3f position, final Vector3f color) { light.setPosition(position).setColor(color); return this; }
	
	public EntityRenderer increaseLight(final Vector3f position, final Vector3f color) {
		light.increasePosition(position).increaseColor(color);
		return this;
	}
	
	public EntityRenderer increaseLight(final float delPosX, final float delPosY, final float delPosZ, final float delColR, final float delColG, final float delColB) {
		light.increasePosition(delPosX, delPosY, delPosZ).increaseColor(delColR, delColG, delColB);
		return this;
	}
	
	public EntityRenderer setStaticEnvironmentMap(final int enviroMap) { staticEnviroMap = enviroMap; return this; }
	
	public EntityRenderer removeStaticEnvironmentMap() { staticEnviroMap = NO_TEXTURE; return this; }
	
	public boolean hasStaticEnvironmentMap() { return staticEnviroMap > NO_TEXTURE; }
	
	protected void prepareRender(final Entity entity, final Matrix4f view) {
		prepareRender(entity.getModel());
		final Material material = entity.getMaterial();
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
	
	protected void prepareTexturedModel(final TexturedModel model) {
		prepareRender(model.getModel());
		final Material material = model.getMaterial();
		loadTexture2D(0, material.getDiffuseTexture());
		loadTexture2D(1, material.getNormalMap());
		loadTexture2D(2, material.getSpecularMap());
		shader.loadMaterial(material);
		if(material.isDoubleSided()) disableCulling();
	}
	
	protected void prepareInstance(final Entity entity) {
		shader.loadModelMatrix(Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale()));
		shader.loadTextureAtlasOffset(entity.getTextureOffset());
	}
	
	public void render(final Map<TexturedModel, List<Entity>> entities, final Matrix4f view, final Vector4f clipPlane) {
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
			final List<Entity> batch = entities.get(model);
			for(final Entity entity : batch) {
				prepareInstance(entity.update());
				drawTriangleCall(entity.getModel());
			}
			enableCulling();
			finishRender(model.getModel());
		}
		shader.stop();
	}
	
	public void render(final Entity entity, final Matrix4f view) {
		shader.start();
		prepareRender(entity, view);
		drawTriangleCall(entity.getModel());
		finishRender(entity.getModel());
		shader.stop();
	}
	
	public void dispose() { shader.dispose(); }
	
}

class EntityShader extends Shader {
	
	public EntityShader(final String vertexFile, final String fragmentFile) {
		super(vertexFile, fragmentFile);
		remapTextureSamplerName(0, "material.texture0");
		remapTextureSamplerName(1, "material.normalMap");
		remapTextureSamplerName(2, "material.specularMap");
	}
	
	void loadClipPlane(final Vector4f clipPlane) { loadUniform("clipPlane", clipPlane); }
	
	void loadMaterial(final Material material) {
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
		loadUniform("material.hasFresnel", material.hasFresnel());
		loadUniform("material.fresnelPower", material.getFresnelPower());
	}
	
	void loadSkyColor(final float red, final float green, final float blue) { loadUniform("skyColor", red, green, blue); }
	
	void loadFogVariables(final float density, final float gradient) { loadUniform("fogDensity", density); loadUniform("fogGradient", gradient); }
	
	void loadTextureAtlasOffset(final Vector2f offset) { loadUniform("offset", offset); }
	
	void loadLight(final Light light, final Matrix4f view, final boolean inEyeSpace) {
		loadUniform("lightPos", light.getPosition());
		if(inEyeSpace) loadUniform("lightPosEyeSpace", getLightPositionEyeSpace(light.getPosition(), view));
		loadUniform("lightColor", light.getColor());
		loadUniform("lightAttenuation", light.getAttenuation());
	}
	
	private Vector3f getLightPositionEyeSpace(final Vector3f pos, final Matrix4f view) {
		final Vector4f eyeSpacePos = new Vector4f(pos.x, pos.y, pos.z, 1F);
		view.transform(eyeSpacePos, eyeSpacePos);
		return new Vector3f(eyeSpacePos.x, eyeSpacePos.y, eyeSpacePos.z);
	}
	
	void loadAmbientLightIntensity(final float ambient) { loadUniform("ambientLightIntensity", ambient); }
	
	void loadHasEnviroMap(final boolean enviroMap) { loadUniform("hasEnviroMap", enviroMap); }
	
}
