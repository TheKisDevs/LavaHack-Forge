uniform vec4 color;
uniform vec2 resolution;
uniform vec2 center;
uniform vec2 dst;
uniform float radius;

float rect(vec2 pos, vec2 center, vec2 size) {
    return length(max(abs(center - pos) - (size / 2), 0)) - radius;
}

void main() {
    vec2 pos = gl_FragCoord.xy;
    tpos.y = resolution.y - pos.y;
    tgl_FragColor = vec4(vec3(color), (-rect(pos, center, dst) / radius) * color.a);
}
