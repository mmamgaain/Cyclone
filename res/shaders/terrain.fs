#version 330 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal, toLightVector, toCameraVector;
in float visibility;

out vec4 color;

struct Material {
	sampler2D diffuse, red, green, blue, blendMap;
	int textureIndex;
	float shineDamper, reflectivity, tiling;
	bool multitextured;
};
uniform Material material;

uniform float time, ambientLightIntensity;
uniform vec3 lightColor, skyColor, lightAttenuation;

void main(void){

	vec2 out_textureCoords = pass_textureCoords * material.tiling;
	
	// Phong shading
	vec3 unitLightVector = normalize(toLightVector),
		 unitNormal = normalize(surfaceNormal);
	float brightness = max(dot(unitNormal, unitLightVector), ambientLightIntensity);
	float lightDist = length(toLightVector),
		  attenuation = lightAttenuation.x + lightAttenuation.y * lightDist + lightAttenuation.z * lightDist * lightDist;
	vec3 diffuse = brightness * lightColor / attenuation;
	
	vec4 textureColor = vec4(0.0);
	
	// Diffuse shading
	if(material.multitextured){
		vec4 blendMapColor = texture(material.blendMap, pass_textureCoords);
		float backgroundColor = 1.0 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
		vec4 backgroundTexture = texture(material.diffuse, out_textureCoords) * backgroundColor;
		vec4 redTexture = texture(material.red, out_textureCoords) * blendMapColor.r,
			 greenTexture = texture(material.green, out_textureCoords) * blendMapColor.g,
			 blueTexture = texture(material.blue, out_textureCoords) * blendMapColor.b;
		
		textureColor = backgroundTexture + redTexture + greenTexture + blueTexture;
	}
	else textureColor = texture(material.diffuse, out_textureCoords);
	
	// Specular shading
	vec3 reflectedLightVector = reflect(-unitLightVector, unitNormal);
	float specularFactor = max(dot(reflectedLightVector, toCameraVector), 0.0),
		  dampedFactor = pow(specularFactor, material.shineDamper) * material.reflectivity;
	vec3 finalSpecular = dampedFactor * lightColor;
	
	color = textureColor * vec4(diffuse, 1.0) + vec4(finalSpecular, 1.0);
	color = mix(vec4(skyColor, 1.0), color, visibility);

}