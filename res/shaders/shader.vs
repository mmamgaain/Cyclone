#version 330

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 textureCoords;

out vec2 out_textureCoords;

uniform float time;
uniform mat4 transform, view, project;

void main(void){

	gl_Position = project * view * transform * vec4(position, 0.0, 1.0);
	
	out_textureCoords = textureCoords;

}
