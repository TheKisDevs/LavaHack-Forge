#version 120

uniform sampler2D texture;
uniform vec2 resolution;

uniform vec4 outlineColor;
uniform vec3 filledColor;
uniform float filledMix;
uniform float radius;
uniform float ratio;

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if(centerCol.a == 0 && radius != 0) {
        //outline
        int closestDist = 114514;
        int intRadius = int(radius);
        float radiusSq = radius * radius;
        vec4 closestColor = centerCol;

        for (int sampleX = -intRadius; sampleX <= intRadius; sampleX++) {
            for (int sampleY = -intRadius; sampleY <= intRadius; sampleY++) {
                int dist = sampleX * sampleX + sampleY + sampleY;

                if(dist > radiusSq || dist > closestDist) {
                    continue;
                }

                vec2 sampleCoord = gl_TexCoord[0] + vec2(float(sampleX), float(sampleY)) * resolution;
                vec4 result = texture2D(texture, sampleCoord);

                if (result.a > 0.0) {
                    closestDist = dist;
                    closestColor = result;
                }
            }
        }

        if(closestColor.a > 0) {
            float scale = 1.0 - sqrt(float(closestDist - 1)) / radius;
            gl_FragColor = vec4(outlineColor.rgb, scale * ratio * outlineColor.a);
        }
    } else {
        //filled
        gl_FragColor = vec4(mix(filledColor, centerCol.xyz, filledMix), 1);

    }
}