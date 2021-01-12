package in.mayank.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL43;

/** A template class for a shader program. If you're creating a new
 * shader pipeline, it needs to inherit this class as it contains
 * all the prerequisites to create an OpenGL shader. */
public abstract class ShaderProgram {
	
	private final int id;
	private final boolean usesGeometryShader;
	private static List<String> attributeList = new ArrayList<>();
	/** {@value #MAX_TEXTURE_UNITS} */
	public static int MAX_TEXTURE_UNITS = -1;
	protected int[] location_textureUnits;
	private HashMap<String, Integer> uniformLocations;
	
	private static FloatBuffer matrix4, matrix3;
	
	/***/
	public ShaderProgram(String computeFile) {
		int computeShaderID = loadShader(computeFile, GL43.GL_COMPUTE_SHADER);
		id = GL20.glCreateProgram();
		GL20.glAttachShader(id, computeShaderID);
		usesGeometryShader = false;
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
		matrix3 = BufferUtils.createFloatBuffer(9);
		matrix4 = BufferUtils.createFloatBuffer(16);
		GL20.glDetachShader(id, computeShaderID);
		GL20.glDeleteShader(computeShaderID);
	}
	
	/** Creates a new minimal shader pipeline containing a vertex
	 * and a fragment shader.
	 * 
	 * @param vertexFile The path for the vertex shader file.
	 * @param fragmentFile The path for the fragment shader file. */
	public ShaderProgram(final String vertexFile, final String fragmentFile) {
		final int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER),
				  fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		id = GL20.glCreateProgram();
		GL20.glAttachShader(id, vertexShaderID);
		GL20.glAttachShader(id, fragmentShaderID);
		usesGeometryShader = false;
		for(int i = 0; i < attributeList.size(); i++)
			bindAttribute(i, attributeList.get(i));
		attributeList.clear();
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
		uniformLocations = new HashMap<>();
		MAX_TEXTURE_UNITS = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
		location_textureUnits = new int[MAX_TEXTURE_UNITS];
		getAllTextureLocations();
		matrix3 = BufferUtils.createFloatBuffer(9);
		matrix4 = BufferUtils.createFloatBuffer(16);
		GL20.glDetachShader(id, vertexShaderID);
		GL20.glDetachShader(id, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
	}
	
	/** Creates a new shader pipeline containing a vertex, geometry
	 * and a fragment shader.
	 * 
	 * @param vertexFile The path for the vertex shader file.
	 * @param geometryFile The path of the geometry shader file.
	 * @param fragmentFile The path for the fragment shader file. */
	public ShaderProgram(String vertexFile, String geometryFile, String fragmentFile) {
		final int vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER),
				  geometryShaderID = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER),
				  fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		id = GL20.glCreateProgram();
		GL20.glAttachShader(id, vertexShaderID);
		GL20.glAttachShader(id, geometryShaderID);
		GL20.glAttachShader(id, fragmentShaderID);
		usesGeometryShader = true;
		for(int i = 0; i < attributeList.size(); i++)
			bindAttribute(i, attributeList.get(i));
		attributeList.clear();
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
		uniformLocations = new HashMap<>();
		MAX_TEXTURE_UNITS = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
		location_textureUnits = new int[MAX_TEXTURE_UNITS];
		getAllTextureLocations();
		matrix3 = BufferUtils.createFloatBuffer(9);
		matrix4 = BufferUtils.createFloatBuffer(16);
		GL20.glDetachShader(id, vertexShaderID);
		GL20.glDetachShader(id, geometryShaderID);
		GL20.glDetachShader(id, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(geometryShaderID);
		GL20.glDeleteShader(fragmentShaderID);
	}
	
	/** Starts the shader and implements it for any subsequent
	 * draw calls. This shader pipeline will be used till any other
	 * shader's start method is called or the {@link #stop()} method
	 * is called. */
	public void start() { GL20.glUseProgram(id); bindAllTextureUnits(); }
	
	/** Stops this shader from being used. */
	public void stop() { GL20.glUseProgram(0); }
	
	/** @return True if this shader pipeline uses the geometry shader,
	 * false if not. */
	public boolean usesGeometryShader() { return usesGeometryShader; }
	
	/** Gets the location of the uniform variable used in the
	 * shader source code.
	 * 
	 * @param name The String name of the variable used in the
	 * shader(s). They could be defined in one or multiple shaders
	 * in the same pipeline.
	 * 
	 * @return The ID location of this uniform variable from the attached
	 * shaders as an <code>int</code>, or -1 if no such active uniform
	 * variable exists. */
	protected int getUniformLocation(final String name) { return getUniformLocation(name, true); }
	
	private int getUniformLocation(final String name, final boolean store) {
		Integer loc = uniformLocations.get(name);
		if(loc != null) return loc;
		loc = GL20.glGetUniformLocation(id, name);
		if(store) uniformLocations.put(name, loc);
		return loc;
	}
	
	/** The default texture samples' names that are available in the
	 * shaders are <code>texture0, texture1, texture2,</code> and so on.
	 * The name of any and all textures can be changed by this method.
	 * <br /><br />
	 * <b><u>NOTE</u></b> : <br><i>1. The main reason for the existence of this
	 * method (other than just aesthetic) is that it might be necessary
	 * for introducing Materials to the shaders. In that case, structs
	 * will have to be created and the new name of the texture samplers
	 * will be <code>[struct instance name].[texture name]</code>.<br>
	 * 2. This method <b>doesn't</b> need to be called every render cycle.</i>
	 * 
	 * @param location The index location of the texture unit to have
	 * a new name.
	 * @param name The new name of the said texture unit.
	 * 
	 * @return A signal for if the task was successfully performed or not.
	 * The reason for the failure for this method could be the location being
	 * out of the range of [0 to {@link #MAX_TEXTURE_UNITS}] */
	protected boolean remapTextureSamplerName(final int location, final String name) {
		if(location >= 0 && location < MAX_TEXTURE_UNITS) { location_textureUnits[location] = getUniformLocation(name, false); return true; }
		return false;
	}
	
	/** Loads a <code>float</code> to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param value The value to be loaded. */
	protected void loadUniform(final String name, final float value) { GL20.glUniform1f(getUniformLocation(name), value); }
	
	/** Loads an <code>int</code> to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param value The value to be loaded. */
	protected void loadUniform(final String name, final int value) { GL20.glUniform1i(getUniformLocation(name), value); }
	
	/** Loads a <code>boolean</code> value to the shader.
	 * 
	 * @param name The name of the uniform variable.
	 * @param value The value to be loaded. */
	protected void loadUniform(final String name, final boolean value) { GL20.glUniform1i(getUniformLocation(name), value ? 1 : 0); }
	
	/** Loads a 3D Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param v The Vector to be loaded. */
	protected void loadUniform(final String name, final Vector3f v) { GL20.glUniform3f(getUniformLocation(name), v.x, v.y, v.z); }
	
	/** Loads a 3D Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param x The X-axis of the Vector to be loaded.
	 * @param y The Y-axis of the Vector to be loaded.
	 * @param z The Z-axis of the Vector to be loaded. */
	protected void loadUniform(final String name, final float x, final float y, final float z) { GL20.glUniform3f(getUniformLocation(name), x, y, z); }
	
	/** Loads a 2D Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param v The Vector to be loaded. */
	protected void loadUniform(final String name, final Vector2f v) { GL20.glUniform2f(getUniformLocation(name), v.x, v.y); }
	
	/** Loads a 2D Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param x The X-axis of the Vector to be loaded.
	 * @param y The Y-axis of the Vector to be loaded. */
	protected void loadUniform(final String name, final float x, final float y) { GL20.glUniform2f(getUniformLocation(name), x, y); }
	
	/** Loads a 2D integer Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param v The Vector to be loaded. */
	protected void loadUniform(final String name, final Vector2i v) { GL20.glUniform2i(getUniformLocation(name), v.x, v.y); }
	
	/** Loads a 2D integer Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param x The X-axis of the Vector to be loaded.
	 * @param y The Y-axis of the Vector to be loaded. */
	protected void loadUniform(final String name, final int x, final int y) { GL20.glUniform2i(getUniformLocation(name), x, y); }
	
	/** Loads a 4D Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param v The Vector to be loaded. */
	protected void loadUniform(final String name, final Vector4f v) { GL20.glUniform4f(getUniformLocation(name), v.x, v.y, v.z, v.w); }
	
	/** Loads a 4D Vector to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param x The X-axis of the Vector to be loaded.
	 * @param y The Y-axis of the Vector to be loaded.
	 * @param z The Z-axis of the Vector to be loaded.
	 * @param w The W-axis of the Vector to be loaded. */
	protected void loadUniform(final String name, final float x, final float y, final float z, final float w) { GL20.glUniform4f(getUniformLocation(name), x, y, z, w); }
	
	/** Loads a 4D Matrix to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param matrix The Matrix to be loaded. */
	protected void loadUniform(final String name, final Matrix4f matrix) { GL20.glUniformMatrix4fv(getUniformLocation(name), false, matrix.get(matrix4)); }
	
	/** Loads a transposed 4D Matrix to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param matrix The Matrix to be loaded. */
	protected void loadUniformTransposed(final String name, final Matrix4f matrix) { GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix.get(matrix4)); }
	
	/** Loads a 3D Matrix to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param matrix The Matrix to be loaded. */
	protected void loadUniform(final String name, final Matrix3f matrix) { GL20.glUniformMatrix3fv(getUniformLocation(name), false, matrix.get(matrix3)); }
	
	/** Loads a transposed 3D Matrix to the uniform variable.
	 * 
	 * @param name The name of the uniform variable.
	 * @param matrix The Matrix to be loaded. */
	protected void loadUniformTransposed(final String name, final Matrix3f matrix) { GL20.glUniformMatrix3fv(getUniformLocation(name), true, matrix.get(matrix3)); }
	
	/** Binds a particular VBO from the VAO that is currently bound.
	 * 
	 * @param attribute The index of the VBO (within the VAO) to be 
	 * bound.
	 * @param variableName The String name of the variable in the
	 * shader (vertex shader) with which the VBO needs to be
	 * bound. */
	protected void bindAttribute(final int attribute, final String variableName) { GL20.glBindAttribLocation(id, attribute, variableName); }
	
	/* Binds all of the texture slots used in the shader files to the corresponding
	 * texture bank so that it can read from the correct texture input. It is assumed
	 * that the textures are  */
	private void bindAllTextureUnits() {
		for(int i = 0; i < MAX_TEXTURE_UNITS; i++) {
			if(location_textureUnits[i] == -1) break;
			GL20.glUniform1i(location_textureUnits[i], i);
		}
	}
	
	/** Creates a shader for this shader pipeline.
	 * 
	 * @param source The relative file path of this shader file's source
	 * code.
	 * @param type The type of shader.
	 * 
	 * @return The ID of the shader created. */
	private static int loadShader(final String source, final int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(source));
			String line;
			while((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
				if(type == GL20.GL_VERTEX_SHADER && line.startsWith("in ")) collectAttributes(line);
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read shader file");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader");
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.exit(-1);
		}
		return shaderID;
	}
	
	/** While reading the vertex shader, picks up the input variables declared and
	 * attaches them to the corresponding VBO slots of the attached VAO. This
	 * assumes that the order in which they are declared in the shader is the
	 * same order in which they have been stored in the VAO.
	 * 
	 * @param line The single line being read from the vertex shader which contains
	 * the input declaration. This is sent by this function's invocation by the
	 * {@link #loadShader(String, int)} function. */
	private static void collectAttributes(final String line) {
		String[] words = line.split(" ");
		String attrib = words[words.length - 1];
		// Assuming that one line of the input only contains one declaration
		// Example :
		// 		in vec3 position;			Correct
		// 		in vec3 position, normal;	Incorrect
		attributeList.add(attrib.substring(0, attrib.length() - 1));
	}
	
	/** Creates texture uniforms which can be used in the shader files. The
	 * naming convention used is <i>texture('index value of the texture slot')</i>;
	 * without the parenthesis. Initially, only 5 texture units (positions 0 to 4)
	 * are tested. If you're using more than that, use {@link #remapTextureSamplerName(int, String)}
	 * method.
	 * <br />
	 * <b><u>For example</u>,</b> <i>texture0, texture1, texture2,... and so on.</i>
	 * <br />The maximum texture number goes up to one less than the value of
	 * {@link #MAX_TEXTURE_UNITS}. */
	private void getAllTextureLocations() {
		int i = 0;
		for(; i < Math.min(5, MAX_TEXTURE_UNITS); i++) {
			if(location_textureUnits[i] == -1) break;
			location_textureUnits[i] = getUniformLocation("texture" + i, false);
		}
		location_textureUnits[i] = -1;
	}
	
	/** Deletes the shader resources from the GPU. */
	public void dispose() { stop(); uniformLocations.clear(); matrix3.clear(); matrix4.clear(); GL20.glDeleteProgram(id); }
	
}
