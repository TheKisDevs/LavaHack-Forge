#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 resolution;

float random(vec2 pos) {
	return fract(1.0 * sin(pos.y + fract(100.0 * sin(pos.x))));
}

float noise(vec2 pos) {
	vec2 i = floor(pos);
	vec2 f = fract(pos);
	float a = random(i + vec2(0.0, 0.0));
	float b = random(i + vec2(1.0, 0.0));
	float c = random(i + vec2(0.0, 1.0));
	float d = random(i + vec2(1.0, 1.0));
	vec2 u = f * f * (3.0 - 2.0 * f);
	return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 pos) {
	float v = 0.0;
	float a = 0.5;
	vec2 shift = vec2(100.0);
	mat2 rot = mat2(cos(0.15), sin(0.15), -sin(0.25), cos(0.5));
	for (int i=0; i < 6; i++) {
		v += a * noise(pos);
		pos = rot * pos * 2.0 + shift;
		a *= 0.55;
	}
	return v;
}

void main() {
	vec2 p = (gl_FragCoord.xy - resolution);
	vec2 p2 = (gl_FragCoord.xy * 4.0 -resolution) / min(resolution.x, resolution.y);
	vec2 p3 = (gl_FragCoord.xy * 2.0 -resolution) / min(resolution.x, resolution.y);


	p=p/min(resolution.x, resolution.y);
	float f = fbm(p * 2.0 * vec2(fbm(p - (time / 8.0)), fbm(p / 2.0 - (time / 8.0))));

//--------------------------------------------
	float c1 = 1.5 / length(p);
	float c2 = 2.5 / length(p2);
	float c3 = 3.5 / length(p3);

	vec3 col = vec3(c1*0.52,c2*0.2,c3*0.8);
//---------------------------------------------

	vec3 c = vec3(1, 1, 1);
	c = (f * 1.5) * c;
	gl_FragColor = vec4(col*c, 1.0);
}