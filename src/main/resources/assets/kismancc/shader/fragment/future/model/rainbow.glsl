#version 120

uniform sampler2D texture;

uniform vec2 sRes;

uniform int time;
uniform vec2 sv;
uniform float opacity;

const float twoThirds = 2.0f / 3.0f;
const float oneThirds = 1.0f / 3.0f;
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0f, twoThirds, oneThirds, 3.0f);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0f - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0f, 1.0f), c.y);
}

const float hueConversion = (1.0f / 360.0f);
void main(void) {
    vec2 uv = gl_FragCoord.xy / sRes;
    vec2 pos = (vec2(720.0f) * uv);
    float hue = time + ((((pos.x * 1.5f) + (((pos.y * 0.75f) * pos.x) * 0.003f)) + (pos.y * 0.25f) + ((pos.x * (pos.y * 0.65f)) * 0.002f)) * 0.75f);
    hue = mod(hue, 360.0f);
    hue = clamp(hue * hueConversion, 0.0f, 1.0f);
    vec3 rgbColor = hsv2rgb(vec3(hue, sv.x, sv.y));
    vec4 color = vec4(rgbColor, opacity);

    vec4 texelColor = texture2D(texture, gl_TexCoord[0].xy);
    gl_FragColor = vec4(color.rgb * color.a, texelColor.a != 0.0f ? color.a : 0.0f);
    gl_FragDepth = 0.0f;
}