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
    gl_Position = u_projection * u_view * u_model * 
	vec4(position.x, position.y + sin(u_time + (u_model[3][0]* 0.2) + 
	(u_model[3][2]* 0.2))/10, position.z, 1.0);
	
	TexCoord = texCoord;
}