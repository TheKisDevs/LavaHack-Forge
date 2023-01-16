#version 120

uniform sampler2D texture;
uniform vec4 rgba;
uniform vec4 rgba1;
uniform float step;
uniform float offset;
uniform float mix;
uniform bool customAlpha;
uniform float alpha;

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);
    if (centerCol.a != 0.0) {
        float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + offset;

        distance = distance / step;

        distance = ((sin(distance) + 1.0) / 2.0);

        float distanceInv = 1 - distance;
        float r = rgba.r * distance + rgba1.r * distanceInv;
        float g = rgba.g * distance + rgba1.g * distanceInv;
        float b = rgba.b * distance + rgba1.b * distanceInv;
        float a = rgba.a * distance + rgba1.a * distanceInv;
        vec4 color = vec4(r, g, b, a);
        vec4 finalColor = mix(color, centerCol, mix);

        float finalAlpha = finalColor.a;

        if(customAlpha) {
            finalAlpha = alpha;
        }

        gl_FragColor = vec4(finalColor.rbg, finalAlpha);
    }
}