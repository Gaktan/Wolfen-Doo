#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;
uniform mat4 u_model;

uniform vec3 u_color;

uniform float u_zfar;

out vec2 TexCoord;
out vec4 Color;
out float Z_far;

void main() {
    gl_Position = u_projection * u_view * u_model * vec4(position, 1.0);

	TexCoord = texCoord;
	Color = vec4(u_color, 1.0);
	Z_far = u_zfar;
}	