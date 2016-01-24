#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

uniform mat4 u_model;

uniform vec3 u_color;
uniform vec4 u_imageInfo;
uniform float u_spriteNumber;

uniform float u_zfar;

out vec4 Color;
out vec2 TexCoord;
out float Z_far;

void main() {
	gl_Position = u_model * vec4(position, 1.0);

	vec2 _texCoord = vec2(texCoord);
    
	Color = vec4(u_color, 1.0);
	TexCoord = _texCoord;
	Z_far = u_zfar;
}