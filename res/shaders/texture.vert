#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

uniform float u_time;

uniform mat4 u_model;

out vec2 TexCoord;

void main()
{	    
    gl_Position = u_projection * u_view * u_model * vec4(position, 1.0);
	TexCoord = texCoord;
}	