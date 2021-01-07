package in.mayank.extra.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import in.mayank.extra.texture.Material;
import in.mayank.extra.utils.Loader;

public class Model {
	
	private Vector3f position = new Vector3f(), rotation = new Vector3f(), scale = new Vector3f(1);
	
	private static String directory;
	private Mesh[] meshes;
	
	private static final Material BASE_MATERIAL = new Material(new Vector3f(-1), new Vector3f(-1), new Vector3f(-1));
	private static final Vector3f INVALID_COLOR = new Vector3f(-1);
	
	private Material DEFAULT_MATERIAL;
	
	public Model(String filename, Loader loader) {
		this(filename, loader, BASE_MATERIAL);
	}
	
	public Model(String filename, Loader loader, Material defaultMat) {
		AIScene scene = Assimp.aiImportFile(filename, Assimp.aiProcess_Triangulate | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_GenUVCoords |
													  Assimp.aiProcess_CalcTangentSpace | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_OptimizeMeshes |
													  Assimp.aiProcess_GenNormals);
		
		if(scene == null || scene.mFlags() == Assimp.AI_SCENE_FLAGS_INCOMPLETE || scene.mRootNode() == null) {
			System.err.println("Assimp : Error while loading file " + filename);
			System.err.println("Assimp flag ID : " + scene.mFlags());
			System.exit(1);
		}
		directory = filename.substring(0, filename.lastIndexOf("/"));
		DEFAULT_MATERIAL = new Material(defaultMat);
		
		meshes = processScene(scene, loader);
		
		Assimp.aiReleaseImport(scene);
	}
	
	public Model setPosition(Vector3f position) {
		this.position.set(position);
		return this;
	}
	
	public Model setPosition(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}
	
	public Model setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
		return this;
	}
	
	public Model setRotation(float x, float y, float z) {
		rotation.set(x, y, z);
		return this;
	}
	
	public Model setScale(Vector3f scale) {
		this.scale.set(scale);
		return this;
	}
	
	public Model setScale(float x, float y, float z) {
		scale.set(x, y, z);
		return this;
	}
	
	public Vector3f getPosition() { return position; }
	
	public Vector3f getRotation() { return rotation; }
	
	public Vector3f getScale() { return scale; }
	
	public Mesh[] getMeshes() {
		return meshes;
	}
	
	private Mesh[] processScene(AIScene scene, Loader loader) {
		// Process materials, if any
		int numMaterials = scene.mNumMaterials();
		PointerBuffer aiMaterials = scene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for(int i = 0; i < numMaterials; i++) {
			AIMaterial material = AIMaterial.create(aiMaterials.get(i));
			processMaterial(material, materials, loader);
		}
		
		// Process all the mesh data
		int numMeshes = scene.mNumMeshes();
		PointerBuffer aiMeshes = scene.mMeshes();
		Mesh[] meshes = new Mesh[numMeshes];
		for(int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh, materials, loader);
			meshes[i] = mesh;
		}
		
		return meshes;
	}
	
	private void processMaterial(AIMaterial aiMaterial, List<Material> materials, Loader loader) {
		int diffuseTexture = 0, normalMap = 0, specularMap = 0;
		Vector3f ambientColor = new Vector3f(), diffuseColor = new Vector3f(), specularColor = new Vector3f();
		
		AIColor4D color = AIColor4D.create();
		AIString path = AIString.calloc();
		String texturePath;
		if(DEFAULT_MATERIAL.hasDiffuseTexture()) diffuseTexture = DEFAULT_MATERIAL.getDiffuseTexture();
		else {
			Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
			texturePath = path.dataString();
			if(texturePath != null && texturePath.length() > 0) diffuseTexture = loader.loadTexture(directory + "/" + texturePath, 4, 0);
			else diffuseTexture = 0;
		}
		
		if(DEFAULT_MATERIAL.hasNormalMap()) normalMap = DEFAULT_MATERIAL.getNormalMap();
		else {
			Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_NORMALS, 1, path, (IntBuffer) null, null, null, null, null, null);
			texturePath = path.dataString();
			if(texturePath != null && texturePath.length() > 0) normalMap = loader.loadTexture(directory + "/" + texturePath);
			else normalMap = 0;
		}
		
		if(DEFAULT_MATERIAL.hasSpecularMap()) specularMap = DEFAULT_MATERIAL.getSpecularMap();
		else {
			Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_SPECULAR, 2, path, (IntBuffer) null, null, null, null, null, null);
			texturePath = path.dataString();
			if(texturePath != null && texturePath.length() > 0) specularMap = loader.loadTexture(directory + "/" + texturePath);
			else specularMap = 0;
		}
		//path.free();
		
		if(!DEFAULT_MATERIAL.getAmbientColor().equals(INVALID_COLOR)) ambientColor.set(DEFAULT_MATERIAL.getAmbientColor());
		else if(Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color) == Assimp.aiReturn_SUCCESS)
			ambientColor.set(color.r(), color.g(), color.b());
		else ambientColor.set(0, 0, 0);
		
		if(!DEFAULT_MATERIAL.getDiffuseColor().equals(INVALID_COLOR)) diffuseColor.set(DEFAULT_MATERIAL.getDiffuseColor());
		else if(Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color) == Assimp.aiReturn_SUCCESS)
			diffuseColor.set(color.r(), color.g(), color.b());
		else diffuseColor.set(0, 0, 0);
		
		if(!DEFAULT_MATERIAL.getSpecularColor().equals(INVALID_COLOR)) specularColor.set(DEFAULT_MATERIAL.getSpecularColor());
		else if(Assimp.aiGetMaterialColor(aiMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color) == Assimp.aiReturn_SUCCESS)
			specularColor.set(color.r(), color.g(), color.b());
		else specularColor.set(0, 0, 0);
		//color.free();
		
		materials.add(new Material(diffuseColor, ambientColor, specularColor).setDiffuseTexture(diffuseTexture).setNormalMap(normalMap).setSpecularMap(specularMap)
																			 .setEnviroRefractivity(DEFAULT_MATERIAL.getEnviroRefractivity())
																			 .setIsDoubleSided(DEFAULT_MATERIAL.isDoubleSided())
																			 .setShineValues(DEFAULT_MATERIAL.getShineDamper(), DEFAULT_MATERIAL.getSpecularReflectivity())
																			 .setTransparency(DEFAULT_MATERIAL.getTransparency()));
	}
	
	private Mesh processMesh(AIMesh aiMesh, List<Material> materials, Loader loader) {
		FloatBuffer vertices, textureCoords, normals, tangents, bitangents;
		IntBuffer indices;
		
		// Process vertices
		AIVector3D.Buffer aiVertices = aiMesh.mVertices();
		AIVector3D aiVertexCoords = null;
		vertices = BufferUtils.createFloatBuffer(aiMesh.mNumVertices() * 3);
		if(aiVertices != null) {
			while(aiVertices.remaining() > 0) {
				aiVertexCoords = aiVertices.get();
				vertices.put(aiVertexCoords.x()).put(aiVertexCoords.y()).put(aiVertexCoords.z());
			}
			//aiVertices.free();
			//aiVertexCoords.free();
		}
		vertices.flip();
		
		// Process texture coordinates
		AIVector3D.Buffer aiTextures = aiMesh.mTextureCoords(0);
		AIVector3D aiTextureCoords = null;
		int numTextureCoords = aiMesh.mNumVertices() * 2;
		textureCoords = BufferUtils.createFloatBuffer(numTextureCoords);
		if(aiTextures != null) {
			while(aiTextures.remaining() > 0) {
				aiTextureCoords = aiTextures.get();
				textureCoords.put(aiTextureCoords.x()).put(aiTextureCoords.y());
			}
			//aiTextures.free();
			//aiTextureCoords.free();
		}
		else for(int i = 0; i < numTextureCoords; i++) textureCoords.put(0);
		textureCoords.flip();
		
		// Process normals
		AIVector3D.Buffer aiNormals = aiMesh.mNormals();
		AIVector3D aiNormalsCoords = null;
		normals = BufferUtils.createFloatBuffer(aiNormals.limit() * 3);
		if(aiNormals != null) {
			while(aiNormals.remaining() > 0) {
				aiNormalsCoords = aiNormals.get();
				normals.put(aiNormalsCoords.x()).put(aiNormalsCoords.y()).put(aiNormalsCoords.z());
			}
			//aiNormals.free();
			//aiNormalsCoords.free();
		}
		normals.flip();
		
		// Process indices
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		indices = BufferUtils.createIntBuffer(aiMesh.mNumFaces() * 3);
		if(aiFaces != null) {
			while(aiFaces.remaining() > 0) indices.put(aiFaces.get().mIndices());
			//aiFaces.free();
		}
		indices.flip();
		
		// Process tangents
		AIVector3D.Buffer aiTangents = aiMesh.mTangents();
		AIVector3D aiTangentsCoords = null;
		int numNormals = normals.limit();
		tangents = BufferUtils.createFloatBuffer(numNormals);
		if(aiTangents != null) {
			while(aiTangents.remaining() > 0) {
				aiTangentsCoords = aiTangents.get();
				tangents.put(aiTangentsCoords.x()).put(aiTangentsCoords.y()).put(aiTangentsCoords.z());
			}
			//aiTangents.free();
			//aiTangentsCoords.free();
		}
		else for(int i = 0; i < numNormals; i++) tangents.put(0);
		tangents.flip();
		
		// Process bitangents
		AIVector3D.Buffer aiBitangents = aiMesh.mBitangents();
		AIVector3D aiBitangentsCoords = null;
		bitangents = BufferUtils.createFloatBuffer(numNormals);
		if(aiBitangents != null) {
			while(aiBitangents.remaining() > 0) {
				aiBitangentsCoords = aiBitangents.get();
				bitangents.put(aiBitangentsCoords.x()).put(aiBitangentsCoords.y()).put(aiBitangentsCoords.z());
			}
			//aiBitangents.free();
			//aiBitangentsCoords.free();
		}
		else for(int i = 0; i < numNormals; i++) bitangents.put(0);
		bitangents.flip();
		
		Material material;
		int materialIndex = aiMesh.mMaterialIndex();
		if(materialIndex >= 0 && materialIndex < materials.size()) material = materials.get(materialIndex);
		else material = DEFAULT_MATERIAL;
		
		return new Mesh(vertices, textureCoords, normals, tangents, bitangents, indices, material, loader);
	}
	
	public class Mesh {
		public RawModel model;
		public Material material;
		
		public Mesh(FloatBuffer position, FloatBuffer textureCoords, FloatBuffer normals, FloatBuffer tangents, FloatBuffer bitangents, IntBuffer indices, Material material, Loader loader) {
			this.material = material;
			model = loader.loadToVAO(position, 3, indices, textureCoords, normals, tangents, bitangents);
		}
	}
	
}
