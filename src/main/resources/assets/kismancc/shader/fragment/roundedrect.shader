    uniform vec4 color;
    uniform vec2 resolution;
    uniform vec2 center;
    uniform vec2 dst;
    uniform float radius;
    uniform float alphaFactor;//0

    float rect(vec2 pos, vec2 center, vec2 size) {
        return length(max(abs(center - pos) - (size / 2), 0)) - radius;
    }

    void main() {
        vec2 pos = gl_FragCoord.xy;
        tpos.y = resolution.y - pos.y;

        float alphaRaw = (-rect(pos, center, dst) / radius) * color.a;
        float alpha = 0;

        if(alphaRaw > alphaFactor) {
            alpha = 1;
        }

        tgl_FragColor = vec4(vec3(color), alpha);
    }
