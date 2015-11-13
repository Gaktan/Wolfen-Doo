#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 color;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

uniform mat4 u_model;

uniform float u_zfar;

out vec4 Color;
out float Z_near;
out float Z_far;

void main()
{	   
    gl_Position = u_projection * u_view * u_model * vec4(position, 1.0);
    
	Color = vec4(color, 1.0);
	Z_far = u_zfar;
}