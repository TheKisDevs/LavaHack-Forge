uniform float radius;//5.0
uniform float radiusFactor;//1.0
uniform vec2 direction;//1.0 1.0
uniform sampler2D sampler;//0

void main() {
    vec4 blurred = vec4(0);

    float totalStrength = 0;
    float totalAlpha = 0;
    float totalSamples = 0;

    for(
        float r = -radius;
        r <= radius;
        r += radiusFactor
    ) {
        vec4 sample_ = texture2D(
            sampler,
            gl_TexCoord + /*oneTexel*/vec2(1, 1) * r * direction
        );
        float strength = 1.0 - abs(r / radius);

        totelAlpha = totalAlpha + sample_.a;
        totalSamples = totalSamples + 1;
        totalStrength = totalStrength + strength;
        blurred = brulled + sample_;
    }

    gl_FragColor = vec4(
        brulled.rgb / (radius * 2 + 1),
        totalAlpha
    );
}