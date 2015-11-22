#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 color;
layout(location = 3) in vec3 offset;
layout(location = 4) in float scale;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

//uniform mat4 u_model;

//uniform vec3 u_color;
//uniform vec3 u_texCoord;

uniform float u_zfar;

out vec4 Color;
out vec2 TexCoord;
out float Z_far;

void main() {

	mat4 model;
	model[3] = vec4(offset, 1.0);

	gl_Position = u_projection * (u_view * model * vec4(0.0, 0.0, 0.0, 1.0) + vec4(position, 1.0) * scale);

	Color = vec4(color, 1.0);
	TexCoord = texCoord;
	Z_far = u_zfar;
}