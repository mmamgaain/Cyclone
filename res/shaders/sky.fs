#version 330

in vec3 textureCoords;

out vec4 out_Color;

uniform samplerCube texture0;

void main(void){

	/*vec4 color = texture(texture0, textureCoords);
	if(color.b > 0.1 && color.b < 0.7) color.b += 0.3;
	out_Color = color;*/
	//out_Color = mix(texture(texture0, textureCoords), vec4(1.0, 0.0, 0.5, 1.0), 0.7);
	out_Color = texture(texture0, textureCoords);

}
