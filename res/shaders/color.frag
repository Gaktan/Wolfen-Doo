#version 330

in vec4 Color;
in float Z_far;

out vec4 color;

float Z_near = 0.2; 
  
float LinearizeDepth(float depth) {
    float z = depth * 2.0 - 1.0;
    return (2.0 * Z_near * Z_far) / (Z_far + Z_near - z * (Z_far - Z_near));	
}

void main() {             
    float depth = LinearizeDepth(gl_FragCoord.z) / Z_far;
	color = Color - vec4(vec3(depth), 0.0f);
}
