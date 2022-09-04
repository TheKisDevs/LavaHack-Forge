package com.kisman.cc.util.math;

public class TimeAnimation {
    public long length;
    public double start;
    public double end;
    public double progress;
    public boolean animating;
    public boolean backwards;
    public boolean reverseOnEnd;
    public long startTime;
    public long lastTime;
    public double per;
    public long dif;
    public boolean flag;

    public TimeAnimation(long length, double start, double end) {
        this.length = length;
        this.start = start;
        this.end = end;
        startTime = System.currentTimeMillis();
        animating = true;
        dif = (System.currentTimeMillis() - startTime);
        per = (end - start) / length;
        lastTime = System.currentTimeMillis();
    }

    public void add() {
        if (animating) progress += per * (System.currentTimeMillis() - lastTime);
        lastTime = System.currentTimeMillis();
    }

    public void add(long length) {
        this.length = length;
        per = (end - start) / length;
        if (animating) progress += per * (System.currentTimeMillis() - lastTime);
        lastTime = System.currentTimeMillis();
    }

    public void play() {
        animating = true;
    }

    public void stop() {
        animating = false;
    }

    public double current() {
        return start + progress;
    }
}