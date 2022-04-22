package com.kisman.cc.util;

import net.minecraft.util.math.Vec3d;

public class VecCircle {
    public Vec3d vec3d;
    public float yaw;
    public float pitch;
    public long time;

    public static Vec3d Method719(VecCircle class442) {
        return class442.vec3d;
    }

    public VecCircle(Vec3d vec3d, float f, float f2) {
        this.vec3d = vec3d;
        this.yaw = f;
        this.pitch = f2;
        this.time = System.currentTimeMillis();
    }

    public static float Method720(VecCircle class442) {
        return class442.pitch;
    }

    public static float Method721(VecCircle class442) {
        return class442.yaw;
    }

    public static long Method722(VecCircle class442) {
        return class442.time;
    }
}
