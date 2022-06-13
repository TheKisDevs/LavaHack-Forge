#version 120

uniform sampler2D texture;

uniform vec4 color;

varying vec4 varyingTexCoord0;

void main(void) {
    vec4 sampleTexel = texture2D(texture, varyingTexCoord0.xy);
    gl_FragColor = vec4(sampleTexel.rgb * color.a, sampleTexel.a != 0.0f ? color.a : 0.0f);
}