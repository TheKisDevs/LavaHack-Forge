package com.kisman.cc.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WorldUtil {
    public static String vectorToString(Vec3d vector, boolean... includeY) {
        boolean reallyIncludeY = includeY.length <= 0 || includeY[0];
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append((int) Math.floor(vector.x));
        builder.append(", ");
        if(reallyIncludeY) {
            builder.append((int) Math.floor(vector.y));
            builder.append(", ");
        }
        builder.append((int) Math.floor(vector.z));
        builder.append(")");
        return builder.toString();
    }

    public static float getDistance(Entity entityOut, BlockPos pos) {
        float f = (float)(entityOut.posX - pos.getX());
        float f1 = (float)(entityOut.posY - pos.getY());
        float f2 = (float)(entityOut.posZ - pos.getZ());
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }
}
