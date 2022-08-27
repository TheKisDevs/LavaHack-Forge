package com.kisman.cc.util.math;

import net.minecraft.client.Minecraft;

public class Interpolation {

    public static final float ALMOST_ZERO = 1.e-4f;

    public static boolean isAlmostZero(float value, float tolerance){
        return Math.abs(value) <= tolerance;
    }

    public static float interpolateTo(float current, float target, float deltaTime, float speed){
        if(speed <= 0.0f)
            return target;

        float distance = target - current;

        if(isAlmostZero((distance * distance), ALMOST_ZERO))
            return target;

        float deltaMove = distance * MathUtil.clamp(deltaTime * speed, 0.0f, 1.0f);
        return current + deltaMove;
    }
}
