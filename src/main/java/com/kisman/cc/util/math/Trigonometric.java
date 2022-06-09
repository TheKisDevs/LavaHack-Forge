package com.kisman.cc.util.math;

import net.minecraft.util.math.Vec3d;

/**
 * @author Cubic
 * TODO: Add documentation
 */
public class Trigonometric {

    public static final double PI = 3.14159265358979323846;

    public static Vec3d position(double yaw, double pitch, double radius){
        double s = yaw / 180.0 * PI;
        double t = pitch / 180.0 * PI;
        double x = radius * Math.cos(s) * Math.sin(t);
        double y = radius * Math.cos(t);
        double z = radius * Math.sin(s) * Math.sin(t);
        return new Vec3d(x, y, z);
    }

    public static double toRadians(double degrees){
        return degrees / 180.0 * PI;
    }

    public static double toDegrees(double radians){
        return radians * 180.0 / PI;
    }

    public static double sin(double radians){
        return Math.sin(radians);
    }

    public static double sindr(double radians){
        return toDegrees(sin(radians));
    }

    public static double sindd(double degrees){
        return toDegrees(sin(toRadians(degrees)));
    }

    public static double sinrd(double degrees){
        return sin(toRadians(degrees));
    }
}
