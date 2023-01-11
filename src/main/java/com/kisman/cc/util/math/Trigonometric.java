package com.kisman.cc.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * @author Cubic
 * TODO: Add documentation
 */
public class Trigonometric {
    public static Vec3d position(double yaw, double pitch, double radius){
        double s = yaw / 180.0 * Math.PI;
        double t = pitch / 180.0 * Math.PI;
        double x = radius * Math.cos(s) * Math.sin(t);
        double y = radius * Math.cos(t);
        double z = radius * Math.sin(s) * Math.sin(t);
        return new Vec3d(x, y, z);
    }

    public static Vec3d entityLookOffset(Entity entity, double radius){
        return position(entity.rotationYaw + 90, entity.rotationPitch + 90, radius);
    }

    public static Vec3d entityLookOffset(Entity entity, double radius, float partialTicks){
        return position(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, radius);
    }

    public static BlockPos entityObjectMouseOver(Entity entity, double radius, boolean raytrace){
        Vec3d eyePos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d offset = position(MathHelper.normalizeAngle((int) entity.rotationYaw, 360) + 90, entity.rotationPitch + 90, radius);
        if(raytrace){
            RayTraceResult result = Minecraft.getMinecraft().world.rayTraceBlocks(eyePos, eyePos.add(offset));
            if(result == null)
                return null;
            return result.getBlockPos();
        }
        return new BlockPos(eyePos.add(offset));
    }

    public static Entity entityEntityMouseOver(Entity entity, double radius){
        Vec3d eyePos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d offset = entityLookOffset(entity, radius);
        RayTraceResult result = Minecraft.getMinecraft().world.rayTraceBlocks(eyePos, eyePos.add(offset));
        if(result == null)
            return null;
        return result.entityHit;
    }

    public static boolean isEntityLookingAtEntity(Entity entityIn, Entity possibleEntity, double radius){
        if(possibleEntity == null)
            return false;
        Entity entity = entityEntityMouseOver(entityIn, radius);
        if(entity == null)
            return false;
        return entity.equals(possibleEntity);
    }
}
