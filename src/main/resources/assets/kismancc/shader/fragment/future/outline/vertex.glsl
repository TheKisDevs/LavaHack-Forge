#version 120

uniform vec2 texelSize;

//varying vec2 sampleCoordinates[5];
varying vec4 varyingTexCoord0;

void main(void) {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vec2 varyingTexCoord0 = gl_MultiTexCoord0.xy;
//    sampleCoordinates[0] = texCoord0;
//    sampleCoordinates[1] = vec2(texCoord0.x - texelSize.x, texCoord0.y);
//    sampleCoordinates[2] = vec2(texCoord0.x + texelSize.x, texCoord0.y);
//    sampleCoordinates[3] = vec2(texCoord0.x, texCoord0.y - texelSize.y);
//    sampleCoordinates[4] = vec2(texCoord0.x, texCoord0.y + texelSize.y);
}