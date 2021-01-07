#version 330

layout (location = 0) in vec3 position;

out vec3 textureCoords;

uniform mat4 project, view;

void main(void){

	gl_Position = project * view * vec4(position, 1.0);
	
	textureCoords = position;

}