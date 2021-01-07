#version 330

in vec2 blurTextureCoords[11];

out vec4 out_Color;

uniform sampler2D texture0;

const float BLUR_WEIGHTS[11] = float[11](0.0093, 0.028002, 0.065984, 0.121703, 0.175713, 0.198596, 0.175713, 0.121703, 0.065984, 0.028002, 0.0093);

void main(void) {
	
	out_Color = vec4(0.0);
	for(int i = 0; i < 11; i++) out_Color += texture(texture0, blurTextureCoords[i]) * BLUR_WEIGHTS[i];
	
}