#version 330

layout(location = 0) in vec3 position;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

uniform mat4 u_model;

uniform vec3 u_color;
uniform vec3 u_texCoord;

out vec4 Color;
out vec2 TexCoord;

void main()
{	 
	mat4 model = u_model * 10;
	gl_Position = (model * vec4(0.0, 0.0, 0.0, 1.0) + vec4(position, 0.0));
	
	float x = u_texCoord.x;
	float y = u_texCoord.y;
	float factor = u_texCoord.z;
	
	vec2 texCoord;
	
	if(position.x < 0){
		texCoord.x = x * factor;
	}
	if(position.x > 0){
		texCoord.x = (x+1) * factor;
	}
	
	if(position.y > 0){
		texCoord.y = y * factor;
	}
	if(position.y < 0){
		texCoord.y = (y+1) * factor;
	}
    
	Color = vec4(u_color, 1.0);
	TexCoord = texCoord;
}