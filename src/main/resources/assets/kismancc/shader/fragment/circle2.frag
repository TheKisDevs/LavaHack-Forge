#version 120

uniform sampler2D texture;

uniform float time;
uniform vec2 resolution;
uniform float divider;
uniform float maxSample;
uniform vec2 texelSize;

uniform vec4 color1;
uniform vec4 color2;
uniform vec4 filledColor;
uniform vec3 outlineColor;

uniform bool customAlpha;

uniform float mix;

uniform bool filled;

uniform bool rainbow;
uniform float rainbowAlpha;

uniform bool circle;
uniform float circleRadius;

uniform bool glow;
uniform float glowRadius;

uniform bool outline;
uniform bool fadeOutline;
uniform float outlineRadius;

const float twoThirds = 2.0f / 3.0f;
const float oneThirds = 1.0f / 3.0f;
const float hueConversion = (1.0f / 360.0f);

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0f, twoThirds, oneThirds, 3.0f);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0f - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0f, 1.0f), c.y);
}

float glowShader(float originalAlpha) {
	float alpha = 0;

	for (float x = -glowRadius; x < glowRadius; x++) {
		for (float y = -glowRadius; y < glowRadius; y++) {
			vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));

			if (currentColor.a != 0) {
			    if(glow) {
    			    alpha += divider > 0 ? max(0.0, (maxSample - distance(vec2(x, y), vec2(0))) / divider) : originalAlpha;
    			} else if(outline) {
    			    alpha = 1;
    			}
			}
		}
	}

	return alpha;
}

vec4 circleShader() {
    vec2 uv = gl_FragCoord.xy / resolution;
    vec2 pos = (vec2(720.0f) * uv);

    float length = sqrt(pos.x * pos.x + pos.y * pos.y);
    float lengthFactor = length / circleRadius;
    float lengthFactor2 = floor(lengthFactor);
    bool flag = false;

    vec4 currentColor = vec4(0, 0, 0, 0);

    if(mod(lengthFactor2, 2) == 0) {
        currentColor = color1;
    } else {
        currentColor = color2;
    }

    return currentColor;
}

vec4 rainbowShader() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    vec2 uv = gl_FragCoord.xy / resolution;
    vec2 pos = (vec2(720.0f) * uv);
    vec2 sv = vec2(pos.x / resolution.x, pos.y / resolution.y);
    float hue = time + ((((pos.x * 1.5f) + (((pos.y * 0.75f) * pos.x) * 0.003f)) + (pos.y * 0.25f) + ((pos.x * (pos.y * 0.65f)) * 0.002f)) * 0.75f);
    hue = mod(hue, 360.0f);
    hue = clamp(hue * hueConversion, 0.0f, 1.0f);
    vec3 rgbColor = hsv2rgb(vec3(hue, sv.x, sv.y));
    vec4 color = vec4(rgbColor, rainbowAlpha);


    vec4 finalColor = vec4(color.rgb * color.a, centerCol.a != 0.0f ? color.a : 0.0f);

    return finalColor;
}

// vec3 mix2(vec3 x, vec3 y, float a) {
//     float mix = х * (1.0 - а) + у * а;

//     return mix;
// }

void main(void) {
    vec4 centerCol = texture2D(texture, gl_FragColor.xy);

    float alpha = 0;

    if(centerCol.a == 0) {
        if(glow || outline) {
            alpha = glowShader(centerCol.a);
        }
    } else {
        if(filled) {
            if(customAlpha) {
                alpha = filledColor.a;
            } else {
                alpha = centerCol.a;
            }

            centerCol = filledColor;
        } else if(circle) {
            centerCol = circleShader();

            if(customAlpha) {
                alpha = centerCol.a;
            }
        } else if(rainbow) {
            centerCol = rainbowShader();

            if(customAlpha) {
                alpha = centerCol.a;
            }
        }
    }

    gl_FragColor = vec4(mix(centerCol.rgb, filledColor.rgb, mix), alpha);
}