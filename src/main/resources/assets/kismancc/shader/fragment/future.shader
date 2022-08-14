#version 120

uniform sampler2D texture;
uniform sampler2D image;
uniform vec2 texelSize;


uniform bool filled;
uniform int filledMode; // Color - 1 | Cut Out - 2 | Image - 3
uniform vec4 filledColor;
uniform bool blur;

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);



    if(centerCol.a != 0) {
        if(filled) {//Filled
            vec4 sampleTexel = texture2D(texture, gl_TexCoord[0].xy);

            if(filledMode == 1) {//Color
                centerCol = vec4(color.rgb * color.a, sampleTexel.a != 0 ? color.a : 0);
            } else if(filledMode == 2) {//Cut Out
                //TODO
            } else if(filledMode == 3) {//Use Image
                vec4 texel = texture2D(image, gl_TexCoord[0].xy * vec2(1.0, -1.0));
                vec3 blended = mix(texel.rgb, sampleTexel.rgb, blend).rgb;

                centerCol = vec4(blended * color.a * 1.5f, sampleTexel.a != 0.0f ? color.a : 0.0f);
            }

            if(blur) {
                //Blur
            }

            if(filled) {
                //Filled Filled
            }
        }

    } else {
        //Outline
    }
}