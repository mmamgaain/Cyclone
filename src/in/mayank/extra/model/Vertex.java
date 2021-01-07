package in.mayank.extra.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

public class Vertex {
	
	private static final int NO_INDEX = -1;
	
	private Vector3f position, averagedTangent = new Vector3f();
	private int textureIndex = NO_INDEX, normalIndex = NO_INDEX, index;
	private Vertex duplicateVertex = null;
	private List<Vector3f> tangents = new ArrayList<>();
	private float length;
	
	public Vertex(int index,Vector3f position){
		this.index = index;
		this.position = position;
		this.length = position.length();
	}
	
	public void addTangent(Vector3f tangent) {
		tangents.add(tangent);
	}
	
	// New
	public Vertex duplicate(int newIndex) {
		Vertex vertex = new Vertex(newIndex, position);
		vertex.tangents = this.tangents;
		return vertex;
	}
	
	public int getIndex(){
		return index;
	}
	
	public float getLength(){
		return length;
	}
	
	public void averageTangents() {
		if(tangents.isEmpty()) return;
		for(Vector3f tangent : tangents) averagedTangent.add(tangent, averagedTangent);
		averagedTangent.normalize();
	}
	
	public Vector3f getAveragedTangent() {
		return averagedTangent;
	}
	
	public boolean isSet(){
		return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
	}
	
	public boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
		return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
	}
	
	public void setTextureIndex(int textureIndex){
		this.textureIndex = textureIndex;
	}
	
	public void setNormalIndex(int normalIndex){
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
