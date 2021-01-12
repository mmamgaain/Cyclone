package in.mayank.renderer;

import static in.mayank.extra.texture.Material.NO_TEXTURE;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import in.mayank.extra.core.Core;
import in.mayank.extra.model.Model;
import in.mayank.extra.model.RawModel;
import in.mayank.extra.texture.Material;
import in.mayank.extra.utils.Light;
import in.mayank.extra.utils.Maths;
import in.mayank.shader.Shader;

public class ModelRenderer extends Renderer {
	
	private final ModelShader shader;
	protected Light light = null;
	protected float ambientLightIntensity = 0.2F;
	protected int staticEnviroMap = NO_TEXTURE;
	
	protected float fogDensity = 0F, fogGradient = 1F;
	
	public ModelRenderer(final String vertexFile, final String fragmentFile, final Matrix4f projectionMatrix) {
		shader = new ModelShader(vertexFile, fragmentFile);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadTextureAtlasOffset(new Vector2f(1));
		shader.stop();
	}
	
	public ModelRenderer setFogVariables(final float density, final float gradient) { fogDensity = density; fogGradient = gradient; return this; }
	
	public float getFogDensity() { return fogDensity; }
	
	public float getFogGradient() { return fogGradient; }
	
	public float getAmbientLightIntensity() { return ambientLightIntensity; }
	
	public ModelRenderer setAmbientLightIntensity(final float ambientLightIntensity) { this.ambientLightIntensity = Maths.clamp(ambientLightIntensity, 0, 1); return this; }
	
	public Light getLight() { return light; }
	
	public ModelRenderer setLight(final Light light) { this.light.set(light); return this; }
	
	public ModelRenderer setLight(final Vector3f position, final Vector3f color) { light.setPosition(position).setColor(color); return this; }
	
	public ModelRenderer increaseLight(final Vector3f position, final Vector3f color) { light.increasePosition(position).increaseColor(color); return this; }
	
	public ModelRenderer increaseLight(final float delPosX, final float delPosY, final float delPosZ, final float delColR, final float delColG, final float delColB) {
		light.increasePosition(delPosX, delPosY, delPosZ).increaseColor(delColR, delColG, delColB);
		return this;
	}
	
	public ModelRenderer setStaticEnvironmentMap(final int enviroMap) { staticEnviroMap = enviroMap; return this; }
	
	public ModelRenderer removeStaticEnvironmentMap() { staticEnviroMap = NO_TEXTURE; return this; }
	
	public boolean hasStaticEnvironmentMap() { return staticEnviroMap > NO_TEXTURE; }
	
	protected void prepareRender(final RawModel model, final Material material) {
		prepareRender(model);
		loadTexture2D(0, material.getDiffuseTexture());
		loadTexture2D(1, material.getNormalMap());
		loadTexture2D(2, material.getSpecularMap());
		shader.loadHasEnviroMap(hasStaticEnvironmentMap());
		shader.loadMaterial(material);
	}
	
	public void render(final Model model, final Matrix4f view, final Vector4f clipPlane, final Light light) {
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadTime((float)Core.getElapsedTimeInSeconds());
		shader.loadSkyColor(Core.getRed(), Core.getGreen(), Core.getBlue());
		shader.loadFogVariables(fogDensity, fogGradient);
		shader.loadViewMatrix(view);
		shader.loadLight(light, view);
		shader.loadAmbientLightIntensity(ambientLightIntensity);
		shader.loadModelMatrix(Maths.createTransformationMatrix(model.getPosition(), model.getRotation(), model.getScale()));
		RawModel rawModel;
		Material material;
		for(Model.Mesh mesh : model.getMeshes()) {
			rawModel = mesh.model;
			material = mesh.material;
			prepareRender(rawModel, material);
			if(material.isDoubleSided()) disableCulling();
			drawTriangleCall(rawModel);
			finishRender(rawModel);
			if(material.isDoubleSided()) enableCulling();
		}
		shader.stop();
	}
	
	public void dispose() { shader.dispose(); }
	
}

class ModelShader extends Shader {

	public ModelShader(final String vertexFile, final String fragmentFile) {
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
	
	void loadLight(final Light light, final Matrix4f view) {
		loadUniform("lightPos", light.getPosition());
		loadUniform("lightPosEyeSpace", getLightPositionEyeSpace(light.getPosition(), view));
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
