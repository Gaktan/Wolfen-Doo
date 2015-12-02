#version 330

in vec4 Color;
in vec2 TexCoord;
in float Z_far;

out vec4 color;

uniform sampler2D u_myTexture;

void main() {
	//invert colors
	//color = vec4(vec3(1.0 - texture(u_myTexture, TexCoord)), 1.0);
	
	 // pixelation
    float m_ResX = 0.008;
    float m_ResY = 0.005;
    
   	vec2 uv = TexCoord.xy;
 	vec2 coord = vec2(m_ResX * floor(uv.x / m_ResX),
                  	  m_ResY * floor(uv.y / m_ResY));
 	vec3 tc = texture2D(u_myTexture, coord).rgb;
	color = vec4(tc, 1.0);
	
	/*
	// b&w
	//color = texture(u_myTexture, TexCoord);
    float average = 0.2126 * color.g + 0.7152 * color.b + 0.0722 * color.r;
    color = vec4(vec3(average), 1.0);
    */
	
	/*
	// 256 colors
	float colors = 6.34132;
	
	vec3 tc = texture2D(u_myTexture, TexCoord).rgb;
	tc.r = int(tc.r * colors) / colors;
	tc.g = int(tc.g * colors) / colors;
	tc.b = int(tc.b * colors) / colors;

	color = vec4(tc, 1.0);
	*/
}
