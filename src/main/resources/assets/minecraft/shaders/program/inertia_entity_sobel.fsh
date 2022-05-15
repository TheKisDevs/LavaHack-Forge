#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
	float radius = 1;
    vec4 color = vec4(0, 0, 0, 0);

    vec4 current = texture2D(DiffuseSampler, texCoord);
    if (current.a == 0) {
        for (float x = -radius; x <= radius; x++) {
            for (float y = -radius; y <= radius; y++) {
                vec4 currentColor = texture2D(DiffuseSampler, texCoord + vec2(oneTexel.x * x, oneTexel.y * y));
                if (currentColor.a != 0) {
                    color = currentColor;
                }
            }
        }
        gl_FragColor = color;
    } else {
        gl_FragColor = vec4(current.rgb, 0);
    }
}