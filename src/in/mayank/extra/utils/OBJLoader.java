package in.mayank.extra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import in.mayank.extra.model.ModelData;
import in.mayank.extra.model.Vertex;

public class OBJLoader {
	
	public static ModelData loadOBJModel(String fileName) {
		FileReader isr = null;
		File objFile = new File(fileName);
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found at the location provided.");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vertex> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		try{
			while(true){
				line = reader.readLine();
				if(line.startsWith("v ")){
					String[] currentLine = line.split(" ");
					Vector3f vertex = getProcessedVector3(currentLine);
					
					//vertex.y *= Core.getAspectRatio();
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);
					
				} else if(line.startsWith("vt ")){
					String[] currentLine = line.split(" ");
					Vector2f texture = getProcessedVector2(currentLine);
					textures.add(texture);
				} else if(line.startsWith("vn ")){
					String[] currentLine = line.split(" ");
					Vector3f normal = getProcessedVector3(currentLine);
					normals.add(normal);
				} else if(line.startsWith("f ")){
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" "),
						 vertex1 = currentLine[1].split("/"),
						 vertex2 = currentLine[2].split("/"),
						 vertex3 = currentLine[3].split("/");
				Vertex v0 = processVertex(vertex1, vertices, indices),
					   v1 = processVertex(vertex2, vertices, indices),
					   v2 = processVertex(vertex3, vertices, indices);
				calculateTangents(v0, v1, v2, textures);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the model file : " + fileName);
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3],
				texturesArray = new float[vertices.size() * 2],
				normalsArray  = new float[vertices.size() * 3],
				tangentsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray, tangentsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		
		return new ModelData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, furthest);
	}
	
	private static Vector3f getProcessedVector3(String[] line) {
		int pointer = 1;
		float[] ret = new float[3];
		for(int i = 0; i < 3; i++) {
			String currentInput;
			do {
				currentInput = line[pointer++];
			}while(currentInput.isEmpty());
			ret[i] = Float.valueOf(currentInput);
		}
		return new Vector3f(ret[0], ret[1], ret[2]);
	}
	
	private static Vector2f getProcessedVector2(String[] line) {
		int pointer = 1;
		float[] ret = new float[2];
		for(int i = 0; i < 2; i++) {
			String currentInput;
			do {
				currentInput = line[pointer++];
			}while(currentInput.isEmpty());
			ret[i] = Float.valueOf(currentInput);
		}
		return new Vector2f(ret[0], ret[1]);
	}
	
	private static Vertex processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if(currentVertex.isSet()) return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		
		currentVertex.setTextureIndex(textureIndex);
		currentVertex.setNormalIndex(normalIndex);
		indices.add(index);
		return currentVertex;
		
	}
	
	private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2, List<Vector2f> textures) {
		Vector3f deltaPos1 = new Vector3f(),
				 deltaPos2 = new Vector3f();
		v1.getPosition().sub(v0.getPosition(), deltaPos1);
		v2.getPosition().sub(v0.getPosition(), deltaPos2);
		Vector2f uv0 = textures.get(v0.getTextureIndex()),
				 uv1 = textures.get(v1.getTextureIndex()),
				 uv2 = textures.get(v2.getTextureIndex()),
				 deltaUV1 = new Vector2f(),
				 deltaUV2 = new Vector2f();
		uv1.sub(uv0, deltaUV1);
		uv2.sub(uv0, deltaUV2);
		
		float r = 1F / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
		deltaPos1.mul(deltaUV2.y);
		deltaPos2.mul(deltaUV1.y);
		Vector3f tangent = new Vector3f();
		deltaPos1.sub(deltaPos2, tangent);
		tangent.mul(r);
		v0.addTangent(tangent);
		v1.addTangent(tangent);
		v2.addTangent(tangent);
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++)
			indicesArray[i] = indices.get(i);
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray, float[] tangentsArray) {
		float furthestPoint = 0;
		for(int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if(currentVertex.getLength() > furthestPoint)
				furthestPoint = currentVertex.getLength();
			Vector3f position = currentVertex.getPosition(),
					 normalVector = normals.get(currentVertex.getNormalIndex()),
					 tangent = currentVertex.getAveragedTangent();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			verticesArray[i * 3 + 0] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2 + 0] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1F - textureCoord.y;
			normalsArray [i * 3 + 0] = normalVector.x;
			normalsArray [i * 3 + 1] = normalVector.y;
			normalsArray [i * 3 + 2] = normalVector.z;
			tangentsArray[i * 3 + 0] = tangent.x;
			tangentsArray[i * 3 + 1] = tangent.y;
			tangentsArray[i * 3 + 2] = tangent.z;
		}
		return furthestPoint;
	}

	private static Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		}
		else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			} else {
				//Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				Vertex duplicateVertex = previousVertex.duplicate(vertices.size());	// New
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}
		}
	}
	
	private static void removeUnusedVertices(List<Vertex> vertices){
		for(Vertex vertex : vertices){
			vertex.averageTangents();
			if(!vertex.isSet()){
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
	
}
