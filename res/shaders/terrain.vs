#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normals;

out vec2 pass_textureCoords;
out vec3 surfaceNormal, toLightVector, toCameraVector;
out float visibility;

uniform float time, fogDensity, fogGradient;
uniform mat4 transform, view, project;
uniform vec3 lightPos;
uniform vec4 clipPlane;

void main(void){

	vec4 worldPosition = transform * vec4(position, 1.0),
		 fromCameraPosition = view * worldPosition;
	gl_Position = project * fromCameraPosition;
	
	pass_textureCoords = textureCoords;
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	
	// Phong shading
	surfaceNormal = (transform * vec4(normals, 0.0)).xyz;
	toLightVector = lightPos - worldPosition.xyz;
	
	// Specular shading
	toCameraVector = normalize(inverse(view) * vec4(0.0, 0.0, 0.0, 1.0) - worldPosition).xyz;
	
	// Fog calculations
	float distance = length(fromCameraPosition.xyz);
	visibility = clamp(exp(-pow(distance * fogDensity, fogGradient)), 0.0, 1.0);

}