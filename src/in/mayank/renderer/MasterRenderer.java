package in.mayank.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import in.mayank.extra.model.Entity;
import in.mayank.extra.model.Terrain;
import in.mayank.extra.model.TexturedModel;
import in.mayank.extra.utils.Light;
import in.mayank.extra.utils.Loader;

public class MasterRenderer {
	
	private EntityRenderer renderer;
	private TerrainRenderer tRenderer;
	private Map<TexturedModel, List<Entity>> entities;
	private List<Terrain> terrains;
	private SkyboxRenderer sky = null;
	private final Matrix4f projection;
	
	public MasterRenderer(final String vertexFile, final String fragmentFile, final String terrainVertexFile, final String terrainFragmentFile, final Matrix4f projection) {
		this.projection = projection;
		renderer = new EntityRenderer(vertexFile, fragmentFile, projection);
		tRenderer = new TerrainRenderer(terrainVertexFile, terrainFragmentFile, projection);
		entities = new HashMap<>();
		terrains = new ArrayList<>();
	}
	
	public MasterRenderer setSky(final String vertexFile, final String fragmentFile, final Loader loader, final String[] textureFiles) {
		if(sky == null) sky = new SkyboxRenderer(vertexFile, fragmentFile, projection, loader, textureFiles);
		return this;
	}
	
	public MasterRenderer setSky(final String vertexFile, final String fragmentFile, final Loader loader, final String filename) {
		if(sky == null) sky = new SkyboxRenderer(vertexFile, fragmentFile, projection, loader, filename);
		return this;
	}
	
	public MasterRenderer setSky(String vertexFile, String fragmentFile, Loader loader, int skyCubeTexture) {
		if(sky == null) sky = new SkyboxRenderer(vertexFile, fragmentFile, projection, loader, skyCubeTexture);
		return this;
	}
	
	public int getSkyTexture() {
		if(sky == null) return 0;
		return sky.getTexture();
	}
	
	public MasterRenderer changeSkyTexture(final String[] textureFiles, final Loader loader) {
		sky.setTexture(textureFiles, loader);
		return this;
	}
	
	public MasterRenderer changeSkyTexture(String filename, Loader loader) {
		sky.setTexture(filename, loader);
		return this;
	}
	
	public MasterRenderer setSkyRotation(float speed, int direction) {
		sky.setRotation(speed, direction);
		return this;
	}
	
	public float getSkyRotationSpeed() {
		return sky.getSpeed();
	}
	
	public int getSkyRotationDirection() {
		return sky.getDirection();
	}
	
	public MasterRenderer setEnvironmentMap(int enviroMap) {
		renderer.setStaticEnvironmentMap(enviroMap);
		//TODO: Set environment map for the terrain renderer and for any
		//other renderer which applies
		return this;
	}
	
	public MasterRenderer setFogVariables(float density, float gradient) {
		renderer.setFogVariables(density, gradient);
		tRenderer.setFogVariables(density, gradient);
		return this;
	}
	
	public float getFogDensity() {
		return renderer.fogDensity;
	}
	
	public float getFogGradient() {
		return renderer.fogGradient;
	}
	
	public MasterRenderer setLight(Light light) {
		renderer.setLight(light);
		tRenderer.setLight(light);
		//TODO: Set light for other renderer
		return this;
	}
	
	public MasterRenderer setLight(Vector3f position, Vector3f color) {
		renderer.setLight(position, color);
		tRenderer.setLight(position, color);
		return this;
	}
	
	public Light getLight() {
		return renderer.light;
	}
	
	public Vector3f getLightPosition() {
		return renderer.light.getPosition();
	}
	
	public Vector3f getLightColor() {
		return renderer.light.getColor();
	}
	
	public MasterRenderer setAmbientLightIntensity(float ambient) {
		renderer.setAmbientLightIntensity(ambient);
		tRenderer.setAmbientLightIntensity(ambient);
		//TODO: Set ambient light intensity for other renderer
		return this;
	}
	
	public MasterRenderer increaseLight(float delPosX, float delPosY, float delPosZ, float delColR, float delColG, float delColB) {
		renderer.increaseLight(delPosX, delPosY, delPosZ, delColR, delColG, delColB);
		tRenderer.increaseLight(delPosX, delPosY, delPosZ, delColR, delColG, delColB);
		//TODO: Increase light for other renderer
		return this;
	}
	
	public void render(Matrix4f view, Vector4f clipPlane) {
		renderer.render(entities, view, clipPlane);
		tRenderer.render(terrains, view, clipPlane);
		//TODO: Render call for other renderer
		if(sky != null) sky.render(view);
		entities.clear();
	}
	
	public MasterRenderer addEntity(List<Entity> entities) {
		for(Entity entity : entities)
			addEntity(entity);
		return this;
	}
	
	public MasterRenderer addEntity(Entity entity) {
		TexturedModel model = entity.getTexturedModel();
		List<Entity> batch = entities.get(model);
		if(batch != null) batch.add(entity);
		else {
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(model, newBatch);
		}
		return this;
	}
	
	public MasterRenderer addTerrain(Terrain terrain) {
		terrains.add(terrain);
		return this;
	}
	
	public void dispose() {
		renderer.dispose();
		tRenderer.dispose();
		if(sky != null) sky.dispose();
		//TODO: Dispose other renderer
	}
	
}
