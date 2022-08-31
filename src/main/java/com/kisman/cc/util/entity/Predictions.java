package com.kisman.cc.util.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Predictions {

    private static final String UUID = "fdee323e-7f0c-4c15-8d1c-0f277442342a";

    public static Vec3d motion(Entity entity, double ticks){
        return entity.getPositionVector().add(new Vec3d(entity.motionX * ticks, entity.motionY * ticks, entity.motionZ * ticks));
    }
}
