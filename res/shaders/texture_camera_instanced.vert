#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec3 color;
layout(location = 3) in mat4 model;
layout(location = 7) in float spriteNumber;

/* Camera stuff */
uniform mat4 u_view;
uniform mat4 u_projection;

uniform vec4 u_imageInfo;

uniform float u_zfar;

out vec4 Color;
out vec2 TexCoord;
out float Z_far;

void main() { 
	gl_Position = model * vec4(position, 1.0);

	vec2 _texCoord = vec2(texCoord);
	
	if (spriteNumber >= 0.0) {
		float factorX = u_imageInfo.z / u_imageInfo.x;
		float factorY = u_imageInfo.w / u_imageInfo.y;

		float x = mod(spriteNumber, (u_imageInfo.x / u_imageInfo.z));
		float y = int(spriteNumber / (u_imageInfo.x / u_imageInfo.z));

		float posX = float(position.x > 0);
		_texCoord.x = (x + posX) * factorX;

		float posY = float(position.y < 0);
		_texCoord.y = (y + posY) * factorY;
	}
    
	Color = vec4(color, 1.0);
	TexCoord = _texCoord;
	Z_far = u_zfar;
}