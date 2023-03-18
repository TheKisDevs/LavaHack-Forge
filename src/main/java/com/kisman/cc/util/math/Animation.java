package com.kisman.cc.util.math;

public class Animation {

    private final double begin;

    private final double end;

    private double current;

    private long startTime;

    protected long time;

    public Animation(double begin, double end, long time) {
        this.begin = begin;
        this.end = end;
        this.current = begin;
        this.startTime = System.currentTimeMillis();
        this.time = time;
    }

    public void update(){
        if(System.currentTimeMillis() - startTime >= time){
            current = end;
            return;
        }
        current = begin + ((System.currentTimeMillis() - startTime) / (double) time) * (end - begin);
        // keep the comment below - Cubic
        //begin + (MathUtil.curve((System.currentTimeMillis() - startTime) / (double) time) * (end - begin));
    }

    public void reset(){
        current = begin;
        startTime = System.currentTimeMillis();
    }

    public double getBegin() {
        return begin;
    }

    public double getEnd() {
        return end;
    }

    public double getCurrent() {
        return current;
    }
}
