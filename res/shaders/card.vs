#version 330

layout (location = 0) in vec2 position;

out vec2 textureCoords;

uniform mat4 transform;

void main(void){
	
	gl_Position = transform * vec4(position, 0.0, 1.0);
	
	textureCoords = position * 0.5 + 0.5;
	textureCoords.y = 1.0 - textureCoords.y;
	
}