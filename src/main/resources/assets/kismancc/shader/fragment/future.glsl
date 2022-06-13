#version 120

uniform sampler2D texture;
uniform vec2 texelSize;

uniform bool outline;
uniform float outlineOpacity;
uniform float outlineWidth;

uniform bool filled;
uniform int filledMode;//1 - Color | 2 - Rainbow | 3 - Image
uniform vec4 filledColor;
uniform sampler2D filledImage;
uniform float filledBlend;

void main(void) {
    if(outline) {
        doOutline();
    }

    if(filled) {
        doFilledColor();
    }
}

void doFilledImage(void) {

}

void doFilledColor(void) {
    vec2 texCoord0 = gl_MultiTexCoord0.xy;
    vec4 sampleTexel = texture2D(texture, texCoord0.xy);
    gl_FragColor = vec4(color.rgb * filledColor.a, sampleTexel.a != 0.0f ? color.a : 0.0f);
}

void doFilledRainbow(void) {

}

void doOutline(void) {
    vec2 texCoord0 = gl_MultiTexCoord0.xy;

    vec4 centerTexel = texture2D(texture, texCoord0);
    vec4 sampleTexel0 = texture2D(texture, vec2(texCoord0.x - texelSize.x, texCoord0.y));
    vec4 sampleTexel2 = texture2D(texture, vec2(texCoord0.x + texelSize.x, texCoord0.y));
    vec4 sampleTexel5 = texture2D(texture, vec2(texCoord0.x, texCoord0.y - texelSize.y));
    vec4 sampleTexel7 = texture2D(texture, vec2(texCoord0.x, texCoord0.y + texelSize.y));
    vec3 colorSum = centerTexel.rgb * centerTexel.a;
    colorSum += sampleTexel0.rgb * sampleTexel0.a;
    colorSum += sampleTexel2.rgb * sampleTexel2.a;
    colorSum += sampleTexel5.rgb * sampleTexel5.a;
    colorSum += sampleTexel7.rgb * sampleTexel7.a;
    float alphaSum = 0.0f;
    alphaSum += sampleTexel0.a - centerTexel.a;
    alphaSum += sampleTexel2.a - centerTexel.a;
    alphaSum += sampleTexel5.a - centerTexel.a;
    alphaSum += sampleTexel7.a - centerTexel.a;
    gl_FragColor = vec4((colorSum / alphaSum) * outlineOpacity, clamp(alphaSum, 0.0f, 1.0f) * outlineOpacity);
}