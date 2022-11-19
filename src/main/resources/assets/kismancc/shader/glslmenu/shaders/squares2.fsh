#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;


vec2 translate(vec2 pos, vec2 translate) {
    return pos + vec2(-translate.x, -translate.y);
}

mat2 rotate2D(float a) {
    float co = cos(a), si = sin(a);
    return mat2(co, -si, si, co);
}

float sdBox(in vec2 p, in vec2 size) {
    p = abs(p) - size;

    p *= rotate2D(1.0*time);

    return length(max(p, 0.0)) + min(0.0, max(p.x, p.y));
}

void main()
{
    highp vec2 size = vec2(resolution.x, resolution.y);

    vec2 uv = (2.0 * gl_FragCoord.xy - size) / size.x;
    uv = translate(uv, vec2(0.0));
    uv *= rotate2D(0.5*time);

    highp vec3 color = vec3(0.0);

    float box = sdBox(uv,  vec2(0.6));
    float box1 = sdBox(uv, vec2(0.5));
    float box2 = sdBox(uv, vec2(0.4));
    float box3 = sdBox(uv, vec2(0.3));
    float box4 = sdBox(uv, vec2(0.2));
    float box5 = sdBox(uv, vec2(0.1));

    float aaWidth = 10.0/size.x;

    color = mix(color, vec3(0.9), smoothstep(aaWidth, aaWidth-0.01, abs(box)));
    color = mix(color, vec3(0.7), smoothstep(aaWidth, aaWidth-0.01, abs(box1)));
    color = mix(color, vec3(0.5), smoothstep(aaWidth, aaWidth-0.01, abs(box2)));
    color = mix(color, vec3(0.4), smoothstep(aaWidth, aaWidth-0.01, abs(box3)));
    color = mix(color, vec3(0.2), smoothstep(aaWidth, aaWidth-0.01, abs(box4)));
    color = mix(color, vec3(0.1), smoothstep(aaWidth, aaWidth-0.01, abs(box5)));

    gl_FragColor = vec4(color, 1.0);
}