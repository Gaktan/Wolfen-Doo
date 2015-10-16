#version 330

layout(location = 0) in vec3 position;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

uniform mat4 u_model;

uniform vec3 u_scale;

uniform vec3 u_upColor;
uniform vec3 u_downColor;

out vec4 Color;

void main()
{	   
	vec3 newPos = position;
	newPos.x *= u_scale.x;
	newPos.y *= u_scale.y;
	newPos.z *= u_scale.z;
    gl_Position = u_projection * u_view * u_model * vec4(newPos, 1.0);
    
    vec3 color;
    
    if(newPos.y < 0)
    	color = u_downColor;
    else
    	color = u_upColor;
    
	Color = vec4(color, 1.0);
}