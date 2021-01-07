#version 330

layout (location = 0) in vec2 position;

uniform mat4 project, modelView;

void main(void) {
	
	gl_Position = project * modelView * vec4(position, 0.0, 1.0);
	
}