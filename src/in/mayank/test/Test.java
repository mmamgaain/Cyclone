package in.mayank.test;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import in.mayank.extra.core.Core;
import in.mayank.extra.imgui.ExampleUi;
import in.mayank.extra.imgui.ImGuiLayer;
import in.mayank.extra.input.GameAction;
import in.mayank.extra.model.Entity;
import in.mayank.extra.model.Model;
import in.mayank.extra.model.Model.Mesh;
import in.mayank.extra.model.ModelData;
import in.mayank.extra.model.RawModel;
import in.mayank.extra.model.TexturedModel;
import in.mayank.extra.texture.Material;
import in.mayank.extra.utils.Camera1P;
import in.mayank.extra.utils.Light;
import in.mayank.extra.utils.Loader;
import in.mayank.extra.utils.Maths;
import in.mayank.extra.utils.OBJLoader;
import in.mayank.extra.utils.ParticleMaster;
import in.mayank.renderer.MasterRenderer;
import in.mayank.renderer.ModelRenderer;
import in.mayank.renderer.Renderer;

public class Test extends Core {
	
	/*private static final float[] VERTICES = {
			-0.5F,  0.5F,
			-0.5F, -0.5F,
			 0.5F,  0.5F,
			 0.5F, -0.5F
	};
	
	private static final float[] TEXTURE_COORDS = {
			0, 0,
			0, 1,
			1, 0,
			1, 1
	};
	
	private static final int[] INDICES = {
			0, 1,
			2, 2,
			1, 3
	};
	
	private static final float[] NORMALS = {
			0, 0,
			1, 0,
			0, 1,
			0, 0,
			1, 0,
			0, 1
	};*/ 
	
	private GameAction exit, forward, backward, left, right, panLeft, panRight, panUp, panDown, up, down, sprint, crawl, normal;
	private Loader loader;
	private Entity sphere, dragon, sphere2;
	private List<Entity> boxes;
	private Camera1P camera;
	private Matrix4f view;
	private MasterRenderer renderer;
	private float moveFactor = 0.3F, rotFactor = 0.01F;
	/*private WaterFBO fbo;
	private WaterRenderer water;
	private List<WaterTile> tiles;*/
	private final Light light;
	private Model model;
	private ModelRenderer mRenderer;
	private int sphereNormal, sphere2Normal, dragonNormal;
	private ImGuiLayer imgui;
	private final float[] color = { 1F, 0F, 0F }, position = { 0F, 10F, -20F }, fresnelPower = { 2 }, transparency = { 0 }, lightColor = { /*0.529F, 0.807F, 0.980F*/1.0F, 0.639F, 0.223F };
	private boolean hasFresnel = true;
	public final ExampleUi exampleUi;
	
	public Test() {
		setVSync(0);
		//setFullscreen(false);
		init();
		//setCursorDisabled(true);
		
		loader = new Loader();
		camera = new Camera1P().setPosition(0, 10, 0);
		Matrix4f projection = Maths.createPerspectiveProjectionMatrix(80, 0.1F, 1000);
		view = new Matrix4f();
		light = new Light(new Vector3f(10000, 10000, 10000), new Vector3f(lightColor[0], lightColor[1], lightColor[2]));
		String[] textureFiles = {"res/textures/skyboxes/default/right.png", "res/textures/skyboxes/default/left.png",
								 "res/textures/skyboxes/default/top.png", "res/textures/skyboxes/default/bottom.png",
								 "res/textures/skyboxes/default/back.png", "res/textures/skyboxes/default/front.png"};
		//int skyTextureID = loader.loadTextureCubeMap(width, height, 0, 0, 0.01F, 0.01F);
		renderer = new MasterRenderer("res/shaders/entity.vs", "res/shaders/entity.fs", "res/shaders/terrain.vs",
									  "res/shaders/terrain.fs", projection).setLight(light)
									  .setSky("res/shaders/sky.vs", "res/shaders/sky.fs", loader, textureFiles);
		renderer.setStaticEnvironmentMap(renderer.getSkyTexture());
		//fbo = new WaterFBO(width / 2, height / 2, width / 2, height / 2);
		//water = new WaterRenderer("res/shaders/water.vs", "res/shaders/water.fs", fbo, projection, loader);
		//tiles = new ArrayList<>();
		mRenderer = new ModelRenderer("res/shaders/entity.vs", "res/shaders/entity.fs", projection).setStaticEnvironmentMap(renderer.getSkyTexture());
		ParticleMaster.init("res/shaders/particle.vs", "res/shaders/particle.fs", loader, projection);
		boxes = new ArrayList<>();
		imgui = new ImGuiLayer(getGLFWWindow());
		imgui.initImGui();
		
		initControls();
		initAssets();
		
		Material mat = new Material(/*loader.loadTexture("res/textures/grass.png", 4, 0)*/new Vector3f(0.133F, 0.545F, 0.133F), new Vector3f(1), new Vector3f(0.874F, 0.564F, 0.234F))
					.setIsDoubleSided(true).setNormalMap(sphereNormal).setShineValues(20, 1).setFresnelPower(fresnelPower[0]);
		//Material mat = new Material(loader.loadTexture("res/models/crate/crate.png", 4));
		//Material mat = new Material(loader.loadTexture("res/models/barrel/textures/barrel_d.jpg", 4)).setNormalMap(loader.loadTexture("res/models/barrel/textures/barrel_n.jpg")).setSpecularMap(loader.loadTexture("res/models/barrel/textures/barrel_s.jpg"));
		model = new Model("res/models/monkey.obj", loader/*, mat*/).setPosition(-20, 10, -25).setRotation(0, 0.1F, 0);
		exampleUi = new ExampleUi();
		
		startGame();
	}
	
	private void initAssets() {
		// Terrains
		/*MaterialTerrain terrainMaterial = new MaterialTerrain(loader.loadTexture("res/textures/BrickRound0105_5_S.jpg", 4, 0),
										  loader.loadTexture("res/textures/Dirt.jpg"), loader.loadTexture("res/textures/grass.jpg"),
										  loader.loadTexture("res/textures/mosaic-floor.jpg"), loader.loadTexture("res/textures/blendmaps/blendMap.png"));
		
		Terrain terrain = new Terrain(0, -1, 800, 0, terrainMaterial, "res/textures/heightmaps/heightmap.png", loader),
				terrain1 = new Terrain(-1, -1, 800, 0, terrainMaterial, "res/textures/heightmaps/heightmap.png", loader);
		renderer.addTerrain(terrain).addTerrain(terrain1);*/
		
		// Textured models
		ModelData sphereData = OBJLoader.loadOBJModel("res/models/sphere.obj"),
				  dragonData = OBJLoader.loadOBJModel("res/models/dragon.obj"),
				  boxData = OBJLoader.loadOBJModel("res/models/crate/crate.obj");
		RawModel sphereRaw = loader.loadToVAO(sphereData.getVertices(), 3, sphereData.getIndices(), sphereData.getTextureCoords(),
											  sphereData.getNormals(), sphereData.getTangents());
		
		sphereNormal = loader.loadTexture("res/textures/collection/159_norm.JPG;true", 4, 0);
		sphere2Normal = loader.loadTexture("res/textures/collection/188_norm2.JPG", 4, 0);
		dragonNormal = loader.loadTexture("res/textures/collection/158_norm.JPG", 4, 0);
		
		TexturedModel sphereModel = new TexturedModel(sphereRaw, new Material(new Vector3f(color[0], color[1], color[2]),
									new Vector3f(color[0], color[1], color[2]), new Vector3f(1, 0.843F, 0)).setShineValues(20, 1)
									.setNormalMap(sphereNormal).setFresnelPower(fresnelPower[0]).setTransparency(transparency[0])),
					  sphereModel2 = new TexturedModel(sphereRaw, new Material(loader.loadTexture("res/textures/collection/188.JPG", 4, 0))
							  		 .setNormalMap(sphere2Normal).setShineValues(10, 2).setFresnelPower(fresnelPower[0]).setTransparency(transparency[0])),
					  dragonModel = new TexturedModel(loader.loadToVAO(dragonData.getVertices(), 3, dragonData.getIndices(),
							  		dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getTangents()),
							  		new Material(new Vector3f(0.2F, 0.7F, 0.8F), new Vector3f(0.2F, 0.7F, 0.8F),
							  					 new Vector3f(1, 0.647F, 0)).setShineValues(20, 1).setNormalMap(dragonNormal)
							  		.setFresnelPower(fresnelPower[0]).setTransparency(transparency[0])),
					  crateModel = new TexturedModel(loader.loadToVAO(boxData.getVertices(), 3, boxData.getIndices(),
							  		boxData.getTextureCoords(), boxData.getNormals()),
							  		new Material(loader.loadTexture("res/models/crate/crate.png", 4, 0))
							  		.setFresnelPower(fresnelPower[0]).setTransparency(transparency[0]));
		
		sphere = new Entity(sphereModel).setPosition(position[0], position[1], position[2]).setScale(5, 5, 5).addRotation(0, -0.1F, 0);
		sphere2 = new Entity(sphereModel2).setPosition(0, 15, -30).setScale(5, 5, 5).addRotation(0, 0.1F, 0);
		dragon = new Entity(dragonModel, new Vector3f(-15, 5, -20), new Vector3f(), new Vector3f(1)).addRotation(0, 0.1F, 0);
		for(int i = 0; i < 20; i++) boxes.add(new Entity(crateModel,
				Maths.getRandomVector(new Vector3f(-500, 2, -500), new Vector3f(500, 5, -20)), new Vector3f(0), new Vector3f(0.05F)));
		
		// Cards
		
		// Water tiles
		/*tiles.add(new WaterTile(0, -400, 800, 400, 0).setShineVariables(20, 1).setWaveVariables(0.01F, new Vector2f(0.01F, -0.01F), 0.01F)
				.setDistortionMap(loader.loadTexture("res/textures/dudvmaps/water.png")).setClarity(0.85F)
				.setNormalMap(loader.loadTexture("res/textures/normalmaps/water.png")));*/
		
		// Music
		
		// Static text
	}
	
	private void initControls() {
		mapToKey(exit = new GameAction(), GLFW.GLFW_KEY_ESCAPE);
		mapToKey(forward = new GameAction(), GLFW.GLFW_KEY_W);
		mapToKey(backward = new GameAction(), GLFW.GLFW_KEY_S);
		mapToKey(left = new GameAction(), GLFW.GLFW_KEY_A);
		mapToKey(right = new GameAction(), GLFW.GLFW_KEY_D);
		/*mapToMouse(panLeft = new GameAction(), MM_LEFT);
		mapToMouse(panRight = new GameAction(), MM_RIGHT);
		mapToMouse(panUp = new GameAction(), MM_UP);
		mapToMouse(panDown = new GameAction(), MM_DOWN);*/
		mapToKey(panLeft = new GameAction(), GLFW.GLFW_KEY_LEFT);
		mapToKey(panRight = new GameAction(), GLFW.GLFW_KEY_RIGHT);
		mapToKey(up = new GameAction(), GLFW.GLFW_KEY_UP);
		mapToKey(down = new GameAction(), GLFW.GLFW_KEY_DOWN);
		mapToKey(sprint = new GameAction(), GLFW.GLFW_KEY_LEFT_SHIFT);
		mapToKey(crawl = new GameAction(), GLFW.GLFW_KEY_LEFT_CONTROL);
		mapToKey(normal = new GameAction(GameAction.BEHAVIOUR_DETECT_INITIAL_PRESS_ONLY), GLFW.GLFW_KEY_SPACE);
	}
	
	public void update() {
		handleControls();
		
		/*fbo.bindReflectionBuffer();
		handleAssets();
		float d = camera.getPosition().y * 2;
		camera.changePosition(0, -d, 0).invertPitch();
		renderer.render(camera.getViewMatrix(), new Vector4f(0, 1, 0, -0.1F));
		camera.changePosition(0, d, 0).invertPitch();*/
		
		view.set(camera.getViewMatrix());
		
		/*fbo.bindRefractionBuffer();
		handleAssets();
		renderer.render(view, new Vector4f(0, -1, 0, 60));
		FBO.unbindFrameBuffer();*/
		
		handleAssets();
		Renderer.startFrame();
		renderer.render(view, new Vector4f(0, -1, 0, 60));
		//water.render(tiles, view, light, camera.getPosition());
		
		mRenderer.render(model, view, new Vector4f(0, -1, 0, 60), light);
		
		renderImGui();
		/*imgui.startFrame();
		try { exampleUi.render(); } catch (Exception e) { e.printStackTrace(); }
		imgui.endFrame();*/
	}
	
	public void renderImGui() {
		imgui.startFrame();
		ImGui.begin("ImGui");
		ImGui.text("FPS : " + (int)getInstantaneousFrameRate());
		ImGui.separator();
		if(ImGui.colorPicker3("Light Color", lightColor)) { light.setColor(lightColor[0], lightColor[1], lightColor[2]); renderer.setLight(light); }
		if(ImGui.sliderFloat3("Sphere Position", position, -20, 20)) sphere.setPosition(position[0], position[1], position[2]);
		if(ImGui.sliderFloat("Fresnel Power", fresnelPower, 0, 4)) {
			sphere.getMaterial().setFresnelPower(fresnelPower[0]);
			dragon.getMaterial().setFresnelPower(fresnelPower[0]);
			sphere2.getMaterial().setFresnelPower(fresnelPower[0]);
			for(Entity box : boxes) box.getMaterial().setFresnelPower(fresnelPower[0]);
			for(Mesh mesh : model.getMeshes()) mesh.material.setFresnelPower(fresnelPower[0]);
		}
		if(ImGui.checkbox("Has Fresnel", hasFresnel)) {
			hasFresnel = !hasFresnel;
			sphere.getMaterial().setHasFresnel(hasFresnel);
			dragon.getMaterial().setHasFresnel(hasFresnel);
			sphere2.getMaterial().setHasFresnel(hasFresnel);
			for(Entity box : boxes) box.getMaterial().setHasFresnel(hasFresnel);
			for(Mesh mesh : model.getMeshes()) mesh.material.setHasFresnel(hasFresnel);
		}
		ImGui.separator();
		if(ImGui.sliderFloat("Refractivity", transparency, 0, 1)) {
			sphere.getMaterial().setTransparency(transparency[0]);
			dragon.getMaterial().setTransparency(transparency[0]);
			sphere2.getMaterial().setTransparency(transparency[0]);
			for(Entity box : boxes) box.getMaterial().setTransparency(transparency[0]);
			for(Mesh mesh : model.getMeshes()) mesh.material.setTransparency(transparency[0]);
		}
		ImGui.end();
		imgui.endFrame();
	}
	
	private void handleAssets() { renderer.addEntity(sphere).addEntity(dragon).addEntity(sphere2).addEntity(boxes); }
	
	private void handleControls() {
		if(exit.isPressed()) stopGame();
		if(sprint.isPressed()) moveFactor = 0.4F;
		else if (crawl.isPressed()) moveFactor = 0.1F;
		else moveFactor = 0.3F;
		
		if(forward.isPressed()) camera.moveForward(moveFactor);
		if(backward.isPressed()) camera.moveBackward(moveFactor);
		if(left.isPressed()) camera.moveLeft(moveFactor);
		if(right.isPressed()) camera.moveRight(moveFactor);
		
		camera/*.changePitch((panUp.getAmount() - panDown.getAmount()) * rotFactor)*/.changeYaw((panRight.getAmount() - panLeft.getAmount()) * rotFactor);
		
		Vector3f pos = camera.getPosition();
		if(up.isPressed()) pos.y += moveFactor;
		if(down.isPressed()) pos.y -= moveFactor;
		camera.setPosition(pos);
		
		if(normal.isPressed()) {
			sphere.setNormalMap(sphere.hasNormalMap() ? Material.NO_TEXTURE : sphereNormal);
			sphere2.setNormalMap(sphere2.hasNormalMap() ? Material.NO_TEXTURE : sphere2Normal);
			dragon.setNormalMap(dragon.hasNormalMap() ? Material.NO_TEXTURE : dragonNormal);
		}
		
	}
	
	public void dispose() {
		loader.dispose();
		renderer.dispose();
		//fbo.dispose();
		//water.dispose();
		mRenderer.dispose();
		ParticleMaster.dispose();
		System.out.println("Total Time : " + getElapsedTimeInSeconds() + " seconds");
		imgui.dispose();
	}
	
	public static void main(String[] args) { new Test(); }

}

