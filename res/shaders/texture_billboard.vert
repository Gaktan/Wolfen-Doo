#version 330

layout(location = 0) in vec3 position;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

uniform mat4 u_model;

uniform vec3 u_color;
uniform vec3 u_texCoord;

uniform float u_zfar;

out vec4 Color;
out vec2 TexCoord;
out float Z_far;

void main() {	    
	vec3 scale = vec3(u_model[0][0], u_model[1][1], u_model[2][2]);
	gl_Position = u_projection * (u_view * u_model * vec4(0.0, 0.0, 0.0, 1.0) + vec4(position, 0.0) * vec4(scale, 1.0));
	
	float x = u_texCoord.x;
	float y = u_texCoord.y;
	float factor = u_texCoord.z;
	
	vec2 texCoord;
	
	if (position.x < 0) {
		texCoord.x = x * factor;
	}
	else {
		texCoord.x = (x+1) * factor;
	}
	
	if (position.y > 0) {
		texCoord.y = y * factor;
	}
	else {
		texCoord.y = (y+1) * factor;
	}
    
	Color = vec4(u_color, 1.0);
	TexCoord = texCoord;
	Z_far = u_zfar;
}