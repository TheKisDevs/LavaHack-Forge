package com.kisman.cc.util;

public class AnimationUtils {
    public float value;
    public long lastTime;
    public float changePerMillisecond;
    public float start;
    public float end;
    public boolean increasing;

    public AnimationUtils(float duration, float start, float end) {
        this((long)(duration * Float.intBitsToFloat(Float.floatToIntBits(7.1846804E-4f) ^ 0x7E465793)), start, end);
    }

    public AnimationUtils(long duration, float start, float end) {
        this.value = start;
        this.end = end;
        this.start = start;
        this.increasing = end > start;
        float difference = Math.abs(start - end);
        this.changePerMillisecond = difference / (float)duration;
        this.lastTime = System.currentTimeMillis();
    }

    public static AnimationUtils fromChangePerSecond(float start, float end, float changePerSecond) {
        return new AnimationUtils(Math.abs((float)(start - end)) / changePerSecond, (float)start, (float)end);
    }

    public void reset() {
        this.value = this.start;
        this.lastTime = System.currentTimeMillis();
    }

    public float getValue() {
        if (this.value == this.end) {
            return this.value;
        }
        if (this.increasing) {
            if (this.value >= this.end) {
                this.value = this.end;
                return this.value;
            }
            this.value += this.changePerMillisecond * (float)(System.currentTimeMillis() - this.lastTime);
            if (this.value > this.end) {
                this.value = this.end;
            }
            this.lastTime = System.currentTimeMillis();
            return this.value;
        }
        if (this.value <= this.end) {
            this.value = this.end;
            return this.value;
        }
        this.value -= this.changePerMillisecond * (float)(System.currentTimeMillis() - this.lastTime);
        if (this.value < this.end) {
            this.value = this.end;
        }
        this.lastTime = System.currentTimeMillis();
        return this.value;
    }

    public boolean isDone() {
        return this.value == this.end;
    }
}

