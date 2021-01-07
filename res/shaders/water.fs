#version 400 core

in vec2 textureCoords;
in vec4 clipSpace;
in vec3 toCameraVector, fromLightVector;

out vec4 out_Color;

struct Material{
	sampler2D reflectionTexture, refractionTexture, depthMap, distortionMap, normalMap;
	bool usingDistortion, usingNormal;
	vec2 moveFactor;
	float waveStrength, clarity, shineDamper, reflectivity, chaos, maxDepth;
	vec3 waterColor;
};
uniform Material material;
uniform vec3 lightColor;
uniform float near, far;

void main(void) {
	
	vec2 ndc = (clipSpace.xy / clipSpace.w) * 0.5 + 0.5,
		 refractTexCoords = ndc,
		 reflectTexCoords = vec2(ndc.x, -ndc.y);
	
	float depth = texture(material.depthMap, refractTexCoords).r,
		  floorDist = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
	
	depth = gl_FragCoord.z;
	float waterDist = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near)),
		  waterDepth = floorDist - waterDist;
	
	
	vec2 distortedTexCoords = textureCoords;
	if(material.usingDistortion) {
		distortedTexCoords = texture(material.distortionMap, textureCoords + material.moveFactor).rg * material.chaos;
		distortedTexCoords = textureCoords + vec2(distortedTexCoords - material.moveFactor.yx);
		vec2 totalDistortion = (texture(material.distortionMap, distortedTexCoords).rg * 2.0 - 1.0) * material.waveStrength * clamp(waterDepth / (material.maxDepth * 4.0), 0.0, 1.0);
		
		refractTexCoords += totalDistortion;
		refractTexCoords = clamp(refractTexCoords, vec2(0.001), vec2(0.999));
		
		reflectTexCoords += totalDistortion;
		reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
		reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);
	}
	
	vec3 normal = vec3(0.0, 1.0, 0.0),
		 viewVector = normalize(toCameraVector);
	if(material.usingNormal) {
		vec3 normalMapColor = texture(material.normalMap, distortedTexCoords).rgb;
		normal = normalize(vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b, normalMapColor.g * 2.0 - 1.0));
	}
	
	// Specular calculations
	vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
	float specularFactor = max(dot(reflectedLight, viewVector), 0.0);
	specularFactor = pow(specularFactor, material.shineDamper);
	vec3 specularHighlights = lightColor * specularFactor * material.reflectivity * clamp(waterDepth / material.maxDepth, 0.0, 1.0);
	
	vec4 reflectColor = texture(material.reflectionTexture, reflectTexCoords),
		 refractColor = texture(material.refractionTexture, refractTexCoords);
	
	float refractiveFactor = max(dot(viewVector, normal), 0.0);
	refractiveFactor = pow(refractiveFactor, material.clarity);
	
	out_Color = mix(reflectColor, refractColor, refractiveFactor) + vec4(specularHighlights, 0.0);
	out_Color = mix(out_Color, vec4(material.waterColor, 1.0), 0.2);
	out_Color.a = clamp(waterDepth / material.maxDepth, 0.0, 1.0);
	
}