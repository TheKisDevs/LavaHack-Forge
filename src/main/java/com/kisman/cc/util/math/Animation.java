package com.kisman.cc.util.math;

import net.minecraft.util.math.MathHelper;

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

    public static double animate(double target, double current, double speed) {
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * MathHelper.clamp(speed, 0, 1);;
        if (factor < 0.1) {
            factor = 0.1;
        }
        return  target > current ? current + factor : current - factor;
    }
}
