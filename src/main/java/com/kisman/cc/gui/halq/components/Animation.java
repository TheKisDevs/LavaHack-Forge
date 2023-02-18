package com.kisman.cc.gui.halq.components;

import com.kisman.cc.util.math.Interpolation;
import com.kisman.cc.util.math.MathUtil;

public class Animation {

    private final double begin;

    private final double end;

    private double current;

    private long startTime;

    private final long time;

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
        current = begin + (MathUtil.curve((System.currentTimeMillis() - startTime) / (double) time) * (end - begin));
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
