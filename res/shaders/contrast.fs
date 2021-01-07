#version 330

#ifndef LUMINANCE_WEIGHTS
#define LUMINANCE_WEIGHTS vec3(0.299, 0.587, 0.114)
#endif

in vec2 textureCoords;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_BrightColor;

uniform sampler2D texture0;
uniform float contrast, brightness, saturation;

void main(void){

	out_Color = texture(texture0, textureCoords);
	
	//calculate saturation
	float luminance = dot(out_Color.rgb, LUMINANCE_WEIGHTS);
	out_Color = mix(vec4(vec3(luminance), 1.0), out_Color, saturation);
	
	//calculate contrast
	out_Color.rgb = (out_Color.rgb - 0.5) * contrast + 0.5;
	
	//calculate brightness
	out_Color.rgb *= brightness;
	
	//Bright filter calculations
	float brightness = (out_Color.r * 0.2124) + (out_Color.g * 0.7152) + (out_Color.b * 0.0722);
	out_BrightColor = out_Color * brightness * 1.5;

}