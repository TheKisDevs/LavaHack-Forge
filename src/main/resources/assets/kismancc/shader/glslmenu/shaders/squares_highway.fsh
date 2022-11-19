#define PI 3.14159265359

mat2 rotate2d(float _angle){
    return mat2(cos(_angle),-sin(_angle),
                sin(_angle),cos(_angle));
}

vec2 box(float scale, vec2 uv, float thk, float dark)
{
    uv *= scale;

    float dispx = 1.0 - step(1.0, abs(uv.x) - thk);
    float dispy = 1.0 - step(1.0, abs(uv.y) - thk);
    float disp = min(1.0, (dispx * dispy) + 0.05);



    float e1 = smoothstep(thk + 0.02, thk, abs(cos(uv.x*PI*0.5)));
    float e2 = smoothstep(thk + 0.02, thk, abs(cos(uv.y*PI*0.5)));
    float o = mix((e1*e2)*disp, (e1+e2)*disp, (1.0+sin(iTime*0.4))*0.5);
    o *= dark;
    return vec2(o, disp);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    // Normalized pixel coordinates (from -1-1 to 11)
    vec2 uv = fragCoord/iResolution.xy;
    uv -= vec2(0.5);
    uv *= 2.0;
    uv.x *= iResolution.x / iResolution.y;

    float o = 0.0;
    float m = 1.0;

    // Time varying pixel color
    uv *= rotate2d(17.0+PI*iTime*0.1);
    float off = mod(iTime*(2.0), 1.0);

    for (float i = -2.0 - off; i < 10.2 ; i+= 0.2)
    {
        if (i < 0.0) continue;
        uv *= rotate2d(i*0.03*PI*0.1*sin(2.0+iTime*0.2));
        uv.x += i * 0.015 * sin(0.2);
        //uv.y += i * 0.012 * sin(i*2.0+iTime*2.0);
        vec2 b = box(i, uv, 0.022, i/10.0);
        o += b.x * m;
        m *= b.y;
    }

    fragColor = vec4(mix(1.0-vec3(o), vec3(o), sin(iTime*0.3+0.5)), 1.0);
}