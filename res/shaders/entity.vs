#version 330

#ifndef REFRACTIVE_INDEX
#define REFRACTIVE_INDEX 1.0/1.33
#endif

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normals;
layout (location = 3) in vec3 tangents;
layout (location = 4) in vec3 bitangents;

out vec2 out_textureCoords;
out vec3 surfaceNormal, toLightVector, toCameraVector, reflectionVector, refractionVector;
out float visibility, refractiveFactor;

uniform float time, fogDensity, fogGradient, enviroRefractivity;
uniform mat4 transform, view, project;
uniform vec3 lightPosEyeSpace, lightPos;
uniform vec2 offset, numberOfRows;
uniform bool hasNormalMap, hasEnviroMap, isTransparent;
uniform vec4 clipPlane;

void main(void){

	vec4 worldPosition = transform * vec4(position, 1.0),
		 fromCameraPosition = view * worldPosition;
	gl_Position = project * fromCameraPosition;
	
	out_textureCoords = (textureCoords / numberOfRows) + offset;
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	
	// Phong shading
	vec4 completeNormal = transform * vec4(normals, 0.0);
	surfaceNormal = completeNormal.xyz;
	toLightVector = lightPos - worldPosition.xyz;
	
	// Specular shading
	toCameraVector = normalize((inverse(view) * vec4(0.0, 0.0, 0.0, 1.0) - worldPosition).xyz);
	
	// Environment reflection and refraction calculations
	// This has to be done before normal mapping calculations as they modify the
	// vector variables used in these calculations to tangent space and we need them in
	// world space for these calculations for now.
	if(hasEnviroMap && isTransparent) {
		vec3 unitNormal = normalize(surfaceNormal);
		reflectionVector = reflect(-toCameraVector, unitNormal);
		refractionVector = refract(-toCameraVector, unitNormal, REFRACTIVE_INDEX);
		refractiveFactor = pow(dot(-fromCameraPosition.xyz, unitNormal), enviroRefractivity);
	}
	
	// Normal map modifications
	if(hasNormalMap) {
		vec3 norm 	= normalize((view * completeNormal).xyz),
			 tang 	= normalize((view * transform * vec4(tangents, 0.0)).xyz),
		 	 bitang = cross(norm, tang);
		 	 //bitang = normalize(bitangents);
		 	 //bitang = normalize((view * transform * vec4(bitangents, 0.0)).xyz);
	
		mat3 toTangentSpace = mat3(
			tang.x, bitang.x, norm.x,
			tang.y, bitang.y, norm.y,
			tang.z, bitang.z, norm.z
		);
		toLightVector = toTangentSpace * (lightPosEyeSpace - fromCameraPosition.xyz);
		toCameraVector = toTangentSpace * normalize(-fromCameraPosition.xyz);
	}
	
	// Fog calculations
	float dist = length(fromCameraPosition.xyz);
	visibility = clamp(exp(-pow(dist * fogDensity, fogGradient)), 0.0, 1.0);

}