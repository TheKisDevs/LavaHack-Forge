package com.kisman.cc.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class RotationUtils
{
    private static Minecraft mc = Minecraft.getMinecraft();

    public static
    double yawDist ( BlockPos pos ) {
        if ( pos != null ) {
            Vec3d difference = new Vec3d ( pos ).subtract ( mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) );
            double d = Math.abs ( (double) mc.player.rotationYaw - ( Math.toDegrees ( Math.atan2 ( difference.z , difference.x ) ) - 90.0 ) ) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static
    double yawDist ( Entity e ) {
        if ( e != null ) {
            Vec3d difference = e.getPositionVector ( ).add (new Vec3d( 0.0f , e.getEyeHeight ( ) / 2.0f , 0.0f )).subtract ( mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) );
            double d = Math.abs ( (double) mc.player.rotationYaw - ( Math.toDegrees ( Math.atan2 ( difference.z , difference.x ) ) - 90.0 ) ) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static boolean isInFov ( BlockPos pos ) {
        return pos != null && ( mc.player.getDistanceSq ( pos ) < 4.0 || yawDist ( pos ) < (double) (getHalvedfov ( ) + 2.0f ) );
    }

    public static
    boolean isInFov ( Entity entity ) {
        return entity != null && ( mc.player.getDistanceSq ( entity ) < 4.0 || yawDist ( entity ) < (double) (getHalvedfov ( ) + 2.0f ) );
    }

    public static
    float getFov ( ) {
        return mc.gameSettings.fovSetting;
    }

    public static
    float getHalvedfov ( ) {
        return getFov ( ) / 2.0f;
    }

    public static
    boolean isInFov ( Vec3d vec3d , Vec3d other ) {
        if ( mc.player.rotationPitch > 30.0f ? other.y > mc.player.posY : mc.player.rotationPitch < - 30.0f && other.y < mc.player.posY ) {
            return true;
        }
        float angle = calcAngleNoY ( vec3d , other )[0] - transformYaw ( );
        if ( angle < - 270.0f ) {
            return true;
        }
        float fov = (mc.gameSettings.fovSetting) / 2.0f;
        return angle < fov + 10.0f && angle > - fov - 10.0f;
    }

    public static
    float[] calcAngleNoY ( Vec3d from , Vec3d to ) {
        double difX = to.x - from.x;
        double difZ = to.z - from.z;
        return new float[]{(float) MathHelper.wrapDegrees ( Math.toDegrees ( Math.atan2 ( difZ , difX ) ) - 90.0 )};
    }

    public static
    float transformYaw ( ) {
        float yaw = mc.player.rotationYaw % 360.0f;
        if ( mc.player.rotationYaw > 0.0f ) {
            if ( yaw > 180.0f ) {
                yaw = - 180.0f + ( yaw - 180.0f );
            }
        } else if ( yaw < - 180.0f ) {
            yaw = 180.0f + ( yaw + 180.0f );
        }
        if ( yaw < 0.0f ) {
            return 180.0f + yaw;
        }
        return - 180.0f + yaw;
    }

    public static float[] getAverageRotations(final List list) {
        double d = 0.0;
        double d2 = 0.0;
        double d3 = 0.0;
        for (final Object entityw : list) {
            final Entity entity = (Entity)entityw;
            d += entity.posX;
            d2 += entity.getEntityBoundingBox().maxY - 2.0;
            d3 += entity.posZ;
        }
        final float[] array = new float[2];
        final int n = 0;
        d /= list.size();
        d3 /= list.size();
        array[n] = getRotationFromPosition(d, d3, d2 /= list.size())[0];
        array[1] = getRotationFromPosition(d, d3, d2)[1];
        return array;
    }

    public static float[] getRotation(Entity entity){
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ );
        final double X = entity.getPositionVector().x - eyesPos.x + (Math.random() / 4);
        final double Y = entity.getPositionVector().y + entity.getEyeHeight() - eyesPos.y + (Math.random() / 4);
        final double Z = entity.getPositionVector().z - eyesPos.z + (Math.random() / 4);
        final double XZ = Math.sqrt(X * X + Z * Z);
        float yaw = MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(Z, X)) - 90.0f );
        float pitch = MathHelper.wrapDegrees((float)(-Math.toDegrees(Math.atan2(Y, XZ))) + 5);

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = (f * f * f) * 10;
        yaw -= yaw % gcd;
        pitch -= pitch % gcd;

        return new float[]{MathHelper.clamp(yaw,-360,360), MathHelper.clamp(pitch,-90,90)};
    }
    
    public static float getDistanceBetweenAngles(final float f, final float f2) {
        float f3 = Math.abs(f - f2) % 360.0f;
        if (f3 > 180.0f) {
            f3 = 360.0f - f3;
        }
        return f3;
    }
    
    public static float getTrajAngleSolutionLow(final float f, final float f2, final float f3) {
        final float f4 = f3 * f3 * f3 * f3 - 0.006f * (0.006f * (f * f) + 2.0f * f2 * (f3 * f3));
        return (float)Math.toDegrees(Math.atan((f3 * f3 - Math.sqrt(f4)) / (0.006f * f)));
    }
    
    public static float[] getRotations(double x, double y, double z) {
        double diffX = x + 0.5 - mc.player.posX;
        double diffY = (y + 0.5) / 2.0 - (mc.player.posY + (double) mc.player.getEyeHeight());
        double diffZ = z + 0.5 - mc.player.posZ;
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }
    
    public static float[] getRotationFromPosition(final double d, final double d2, final double d3) {
        final double d4 = d - Minecraft.getMinecraft().player.posX;
        final double d5 = d2 - Minecraft.getMinecraft().player.posZ;
        final double d6 = d3 - Minecraft.getMinecraft().player.posY - 0.6;
        final double d7 = MathHelper.sqrt(d4 * d4 + d5 * d5);
        final float f = (float)(Math.atan2(d5, d4) * 180.0 / 3.141592653589793) - 90.0f;
        final float f2 = (float)(-(Math.atan2(d6, d7) * 180.0 / 3.141592653589793));
        return new float[] { f, f2 };
    }

    public static
    int getDirection4D ( ) {
        return MathHelper.floor ( ( mc.player.rotationYaw * 4.0F / 360.0F ) + 0.5D ) & 3;
    }

    public static
    String getDirection4D ( boolean northRed ) {
        int dirnumber = getDirection4D ( );
        if ( dirnumber == 0 ) {
            return "South (+Z)";
        }
        if ( dirnumber == 1 ) {
            return "West (-X)";
        }
        if ( dirnumber == 2 ) {
            return ( northRed ? TextFormatting.RED : "" ) + "North (-Z)";
        }
        if ( dirnumber == 3 ) {
            return "East (+X)";
        }
        return "Loading...";
    }
    
    public static float[] getNeededRotations(final Entity entityLivingBase) {
        final double d = entityLivingBase.posX - Minecraft.getMinecraft().player.posX;
        final double d2 = entityLivingBase.posZ - Minecraft.getMinecraft().player.posZ;
        final double d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (Minecraft.getMinecraft().player.getEntityBoundingBox().minY + (Minecraft.getMinecraft().player.getEntityBoundingBox().maxY - Minecraft.getMinecraft().player.getEntityBoundingBox().minY));
        final double d4 = MathHelper.sqrt(d * d + d2 * d2);
        final float f = (float)(MathHelper.atan2(d2, d) * 180.0 / 3.141592653589793) - 90.0f;
        final float f2 = (float)(-(MathHelper.atan2(d3, d4) * 180.0 / 3.141592653589793));
        return new float[] { f, f2 };
    }
    
    public static float[] getRotations(final EntityLivingBase entityLivingBase, final String string) {
        if (string == "Head") {
            final double d = entityLivingBase.posX;
            final double d2 = entityLivingBase.posZ;
            final double d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f;
            return getRotationFromPosition(d, d2, d3);
        }
        if (string == "Chest") {
            final double d = entityLivingBase.posX;
            final double d4 = entityLivingBase.posZ;
            final double d5 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 0.75;
            return getRotationFromPosition(d, d4, d5);
        }
        if (string == "Dick") {
            final double d = entityLivingBase.posX;
            final double d6 = entityLivingBase.posZ;
            final double d7 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 1.2;
            return getRotationFromPosition(d, d6, d7);
        }
        if (string == "Legs") {
            final double d = entityLivingBase.posX;
            final double d8 = entityLivingBase.posZ;
            final double d9 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 1.5;
            return getRotationFromPosition(d, d8, d9);
        }
        final double d = entityLivingBase.posX;
        final double d10 = entityLivingBase.posZ;
        final double d11 = entityLivingBase.posY + entityLivingBase.getEyeHeight() / 2.0f - 0.5;
        return getRotationFromPosition(d, d10, d11);
    }
    
    public static float getNewAngle(float f) {
        if ((f %= 360.0f) >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }
}