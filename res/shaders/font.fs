#version 330

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec3 color, outlineColor;
uniform sampler2D fontAtlas;
uniform float charWidth, edgeTransition, borderWidth, borderEdge;
uniform vec2 offset;

float min(float num0, float num1){
	return num0 < num1 ? num0 : num1;
}

float max(float num0, float num1){
	return num0 > num1 ? num0 : num1;
}

float clamp(float edge0, float edge1, float value){
	return value < min(edge0, edge1) ? edge0 : (value > max(edge0, edge1) ? edge1 : value);
}

void main(void){

	float /*distance = 1.0 - texture(fontAtlas, pass_textureCoords).a,*/
		  alpha = /*1.0 -*/ smoothstep(charWidth, charWidth + edgeTransition, texture(fontAtlas, pass_textureCoords).a);
	
	float /*borderDistance = 1.0 - texture(fontAtlas, pass_textureCoords + offset).a,*/
		  outlineAlpha = smoothstep(borderWidth + borderEdge, borderWidth, 1.0 - texture(fontAtlas, pass_textureCoords + offset).a/*borderDistance*/);
	
	float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
	vec3 overallColor = mix(outlineColor, color, alpha / overallAlpha);
	
	out_Color = vec4(overallColor, overallAlpha);
	
	/*Won't distance and distance2 have exactly the same value each and every time? What's the point of doing it twice? Plus, first we are reversing the value of "texture(fontAtlas, pass_textureCoords).a" in distance variable by subtracting it by 1.0 and then doing it again in the very next line. Shouldn't we circumvent both of them by directly plugging the "texture(fontAtlas, pass_textureCoords).a" in the calculation of variable alpha?*/

}