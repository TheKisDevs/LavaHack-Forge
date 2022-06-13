#version 120

varying vec4 varyingTexCoord0;

void main(void) {
    varyingTexCoord0 = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}