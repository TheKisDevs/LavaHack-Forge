#version 120

uniform sampler2D texture;

uniform float opacityModifier;

//varying vec2 sampleCoordinates[5];
varying vec4 varyingTexCoord0;

void main(void) {
//    vec2 texCoord0 = gl_MultiTexCoord0.xy;
//    sampleCoordinates[0] = texCoord0;
//    sampleCoordinates[1] = vec2(texCoord0.x - texelSize.x, texCoord0.y);
//    sampleCoordinates[2] = vec2(texCoord0.x + texelSize.x, texCoord0.y);
//    sampleCoordinates[3] = vec2(texCoord0.x, texCoord0.y - texelSize.y);
//    sampleCoordinates[4] = vec2(texCoord0.x, texCoord0.y + texelSize.y);
    vec4 centerTexel = texture2D(texture, varyingTexCoord0);
    vec4 sampleTexel0 = texture2D(texture, vec2(varyingTexCoord0.x - texelSize.x, varyingTexCoord0.y));
    vec4 sampleTexel2 = texture2D(texture, vec2(varyingTexCoord0.x + texelSize.x, varyingTexCoord0.y));
    vec4 sampleTexel5 = texture2D(texture, vec2(varyingTexCoord0.x, varyingTexCoord0.y - texelSize.y));
    vec4 sampleTexel7 = texture2D(texture, vec2(varyingTexCoord0.x, varyingTexCoord0.y + texelSize.y));
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
    gl_FragColor = vec4((colorSum / alphaSum) * opacityModifier, clamp(alphaSum, 0.0f, 1.0f) * opacityModifier);
}