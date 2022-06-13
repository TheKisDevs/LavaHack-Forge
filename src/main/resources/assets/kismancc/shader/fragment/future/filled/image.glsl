#version 120

uniform sampler2D texture;
uniform sampler2D image;

uniform vec4 color;
uniform float blend;

varying vec4 varyingTexCoord0;

void main(void) {
    vec4 sampleTexel = texture2D(texture, varyingTexCoord0.xy);
    vec4 texel = texture2D(image, varyingTexCoord0.xy * vec2(1.0, -1.0));
    vec3 blended = mix(texel.rgb, sampleTexel.rgb, blend).rgb;
    gl_FragColor = vec4(blended * color.a * 1.5f, sampleTexel.a != 0.0f ? color.a : 0.0f);
}