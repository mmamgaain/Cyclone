#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D texture0;

void main(void){
	
	color = texture(texture0, textureCoords);
	
}