#version 400 core

layout (location = 0) in vec2 position;

out vec2 textureCoords;
out vec4 clipSpace;
out vec3 toCameraVector, fromLightVector;

uniform mat4 project, view, transform;
uniform float tiling;
uniform vec3 cameraPos, lightPos;

void main(void) {
	
	vec4 worldPosition = transform * vec4(position.x, 0.0, position.y, 1.0);
	clipSpace = project * view * worldPosition;
	
	gl_Position = clipSpace;
	textureCoords = (position / 2.0 + 0.5) * tiling;
	toCameraVector = cameraPos - worldPosition.xyz;
	fromLightVector = worldPosition.xyz - lightPos;
	
}
