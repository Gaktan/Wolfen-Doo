#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 color;
layout(location = 3) in mat4 model;
// location = 4
// location = 5
// location = 6

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;
uniform float u_zfar;

out vec4 Color;
out vec2 TexCoord;
out float Z_far;

void main() {

	gl_Position = u_projection * u_view * model * vec4(position, 1.0);
	
	Color = vec4(color, 1.0);
	TexCoord = texCoord;
	Z_far = u_zfar;
}