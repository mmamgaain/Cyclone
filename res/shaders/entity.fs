#version 330

in vec2 out_textureCoords;
in vec3 surfaceNormal, toLightVector, toCameraVector, reflectionVector, refractionVector;
in float visibility;

out vec4 color;

struct Material {
	sampler2D texture0 /* Diffuse texture */, normalMap, specularMap;
	float shineDamper, reflectivity, transparency, fresnelPower;
	bool hasDiffuseTexture, hasNormalMap, hasSpecularMap, isTransparent, hasFresnel;
	vec3 diffuse, ambient, specular;
};
uniform Material material;

uniform float time, ambientLightIntensity, refractiveFactor;
uniform vec3 lightColor, skyColor, lightAttenuation;
uniform samplerCube texture3;	// Environment Map
uniform bool hasEnviroMap;

void main(void){
	// Diffuse shading
	vec4 textureColor = vec4(material.diffuse, 1.0);
	if(material.hasDiffuseTexture) {
		textureColor *= texture(material.texture0, out_textureCoords);
		if(textureColor.a == 0.0) discard;
	}
	
	vec3 unitNormal = normalize(surfaceNormal);
	if(material.hasNormalMap) {
		vec4 normalMapColor = 2.0 * texture(material.normalMap, out_textureCoords) - 1.0;
		unitNormal = normalize(normalMapColor.rgb);
	}
	
	vec3 unitLightVector = normalize(toLightVector);
	// Phong shading
	float brightness = max(dot(unitNormal, unitLightVector), ambientLightIntensity);
	float lightDist = length(toLightVector),
		  attenuation = lightAttenuation.x + lightAttenuation.y * lightDist + lightAttenuation.z * lightDist * lightDist;
	vec3 diffuse = brightness * lightColor * material.ambient / attenuation;
	
	// Specular shading
	vec3 reflectedLightVector = reflect(-unitLightVector, unitNormal);
	float specularFactor = max(dot(reflectedLightVector, toCameraVector), 0.0),
		  dampedFactor = pow(specularFactor, material.shineDamper);
	vec3 finalSpecular = dampedFactor * material.reflectivity * lightColor;
	if(material.hasSpecularMap) {
		vec3 specularMapColor = texture(material.specularMap, out_textureCoords).rgb;
		finalSpecular *= specularMapColor.r + specularMapColor.b;
		if(specularMapColor.g > 0.5) diffuse = vec3(1.0);
	}
	finalSpecular *= material.specular / attenuation;
	
	color = (vec4(diffuse, 1.0) * textureColor) + vec4(finalSpecular, 0.0);
	color = mix(vec4(skyColor, 1.0), color, visibility);
	
	float fresnelFactor = pow(max(dot(unitNormal, toCameraVector), 0.0), 1.0 / material.fresnelPower);
	// Fresnel effect
	if(material.hasFresnel) color = mix(vec4(lightColor, 1.0), color, fresnelFactor);
	
	if(hasEnviroMap && material.isTransparent) {
		vec4 reflectColor = texture(texture3, reflectionVector),
			 refractColor = texture(texture3, refractionVector),
			 enviroColor = mix(refractColor, reflectColor, refractiveFactor);
		color = mix(color, enviroColor, material.transparency);
	}
	
	
}
