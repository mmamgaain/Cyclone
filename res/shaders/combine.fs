#version 330

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D texture0, texture1;

void main(void) {
	
	out_Color = texture(texture0, textureCoords) + texture(texture1, textureCoords);
	
}
