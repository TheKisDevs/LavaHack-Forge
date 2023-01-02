package com.kisman.cc.util.world;

import com.kisman.cc.util.AngleUtil;
import com.kisman.cc.util.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomUtils;

import static com.kisman.cc.util.Globals.mc;

@SideOnly(Side.CLIENT)
public class RotationUtils {
    public static float[] lookAtRandomed(Entity entityIn) {
        double diffX = entityIn.posX - mc.player.posX;
        double diffZ = entityIn.posZ - mc.player.posZ;
        double diffY;

        if (entityIn instanceof EntityLivingBase) diffY = entityIn.posY + entityIn.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight()) - 0.4;
        else diffY = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D - (mc.player.posY + mc.player.getEyeHeight());

        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = ((float) (((Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f)) + MathUtil.getRandom(-2, 2));
        float pitch = ((float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI)) + MathUtil.getRandom(-2, 2));
        yaw = mc.player.rotationYaw + getFixedRotation(MathHelper.wrapDegrees(yaw - mc.player.rotationYaw));
        pitch = mc.player.rotationPitch + getFixedRotation(MathHelper.wrapDegrees(pitch - mc.player.rotationPitch));
        return new float[] {yaw, pitch};
    }

    public static double yawDist(BlockPos pos) {
        if(pos != null) {
            Vec3d difference = new Vec3d(pos).subtract ( mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) );
            double d = Math.abs ( (double) mc.player.rotationYaw - ( Math.toDegrees ( Math.atan2 ( difference.z , difference.x ) ) - 90.0 ) ) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static double yawDist(Entity e) {
        if(e != null) {
            Vec3d difference = e.getPositionVector ( ).add (new Vec3d( 0.0f , e.getEyeHeight ( ) / 2.0f , 0.0f )).subtract ( mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) );
            double d = Math.abs ( (double) mc.player.rotationYaw - ( Math.toDegrees ( Math.atan2 ( difference.z , difference.x ) ) - 90.0 ) ) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static boolean isInFov(BlockPos pos) {
        return pos != null && ( mc.player.getDistanceSq ( pos ) < 4.0 || yawDist ( pos ) < (double) (getHalvedfov ( ) + 2.0f ) );
    }

    public static boolean isInFov(Entity entity) {
        return entity != null && ( mc.player.getDistanceSq ( entity ) < 4.0 || yawDist ( entity ) < (double) (getHalvedfov ( ) + 2.0f ) );
    }

    public static float getFov() {
        return mc.gameSettings.fovSetting;
    }

    public static float getHalvedfov() {
        return getFov ( ) / 2.0f;
    }

    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }

    public static void lookAtVec3d(Vec3d vec3d) {
        final float[] angle = AngleUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        setPlayerRotations(angle[0], angle[1]);
    }

    public static void lookAtEntity(Entity entity) {
        lookAtVec3d(new Vec3d(entity.getPosition()));
    }

    public static void setPlayerRotations(final float yaw, final float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    /**
     * Get rotation from a position vector to another position vector
     *
     * @param posFrom Calculate rotation from this position vector
     * @param posTo   Calculate rotation to this position vector
     */
    public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double lengthXZ = Math.hypot(vec.x, vec.z);
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, lengthXZ)));

        return new Vec2f((float) yaw, (float) pitch);
    }

    public static float[] getRotationToPos(BlockPos pos) {
        double lengthXZ = Math.hypot(pos.getX(), pos.getZ());
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(pos.getZ(), pos.getX())) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(pos.getY(), lengthXZ)));
        return new float[] {(float) yaw, (float) pitch};
    }

    public static double normalizeAngle(double angle) {
        angle %= 360.0;

        if (angle >= 180.0) angle -= 360.0;
        if (angle < -180.0) angle += 360.0;

        return angle;
    }

    public static float transformYaw ( ) {
        float yaw = mc.player.rotationYaw % 360.0f;
        if ( mc.player.rotationYaw > 0.0f ) if ( yaw > 180.0f ) yaw = - 180.0f + ( yaw - 180.0f );
        else if ( yaw < - 180.0f ) yaw = 180.0f + ( yaw + 180.0f );
        if ( yaw < 0.0f ) return 180.0f + yaw;

        return - 180.0f + yaw;
    }

    public static float[] getRotation(Entity entity){
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ );
        double X = entity.getPositionVector().x - eyesPos.x + (Math.random() / 4);
        double Y = entity.getPositionVector().y + entity.getEyeHeight() - eyesPos.y + (Math.random() / 4);
        double Z = entity.getPositionVector().z - eyesPos.z + (Math.random() / 4);
        double XZ = Math.sqrt(X * X + Z * Z);
        float yaw = MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(Z, X)) - 90.0f );
        float pitch = MathHelper.wrapDegrees((float)(-Math.toDegrees(Math.atan2(Y, XZ))) + 5);

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = (f * f * f) * 10;
        yaw -= yaw % gcd;
        pitch -= pitch % gcd;

        return new float[] { MathHelper.clamp(yaw,-360,360), MathHelper.clamp(pitch,-90,90) };
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

    public static int getDirection4D () {
        return MathHelper.floor(( mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    }

    public static String getDirection4D (boolean northRed) {
        int dirnumber = getDirection4D();
        if (dirnumber == 0) return "South (+Z)";
        if (dirnumber == 1) return "West (-X)";
        if (dirnumber == 2) return (northRed ? TextFormatting.RED : "") + "North (-Z)";
        if (dirnumber == 3) return "East (+X)";
        return "Loading...";
    }
    
    public static float[] getNeededRotations(Entity entityLivingBase) {
        double d = entityLivingBase.posX - Minecraft.getMinecraft().player.posX;
        double d2 = entityLivingBase.posZ - Minecraft.getMinecraft().player.posZ;
        double d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (Minecraft.getMinecraft().player.getEntityBoundingBox().minY + (Minecraft.getMinecraft().player.getEntityBoundingBox().maxY - Minecraft.getMinecraft().player.getEntityBoundingBox().minY));
        double d4 = MathHelper.sqrt(d * d + d2 * d2);
        float f = (float) (MathHelper.atan2(d2, d) * 180.0 / 3.141592653589793) - 90.0f;
        float f2 = (float )(-(MathHelper.atan2(d3, d4) * 180.0 / 3.141592653589793));
        return new float[] { f, f2 };
    }
    
    public static float getNewAngle(float f) {
        if ((f %= 360.0f) >= 180.0f) f -= 360.0f;
        if (f < -180.0f) f += 360.0f;
        return f;
    }

    public static float[] getMatrixRotations(Entity e, boolean oldPositionUse) {
        double diffY;
        double diffX = (oldPositionUse ? e.prevPosX : e.posX) - (oldPositionUse ? mc.player.prevPosX : mc.player.posX);
        double diffZ = (oldPositionUse ? e.prevPosZ : e.posZ) - (oldPositionUse ? mc.player.prevPosZ : mc.player.posZ);
        if (e instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)e;
            float randomed = RandomUtils.nextFloat((float)(entitylivingbase.posY + (double)(entitylivingbase.getEyeHeight() / 1.5f)), (float) (entitylivingbase.posY + (double) entitylivingbase.getEyeHeight() - (double) (entitylivingbase.getEyeHeight() / 3.0f)));
            diffY = (double) randomed - (mc.player.posY + (double) mc.player.getEyeHeight());
        } else diffY = (double) RandomUtils.nextFloat((float) e.getEntityBoundingBox().minY, (float) e.getEntityBoundingBox().maxY) - (mc.player.posY + (double) mc.player.getEyeHeight());
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI - 90.0) + RandomUtils.nextFloat(-2.0f, 2.0f);
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI)) + RandomUtils.nextFloat(-2.0f, 2.0f);
        yaw = mc.player.rotationYaw + getFixedRotation(MathHelper.wrapDegrees(yaw - mc.player.rotationYaw));
        pitch = mc.player.rotationPitch + getFixedRotation(MathHelper.wrapDegrees(pitch - mc.player.rotationPitch));
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        return new float[] {yaw, pitch};
    }

    public static boolean isInFOV(Entity player, Entity entity, double angle) {
        final double angleDiff = getAngle360(player.rotationYaw, getLookNeeded(player, entity.posX, entity.posY, entity.posZ)[0]);
        return (angleDiff > 0.0 && angleDiff < (angle *= 0.5)) || (-angle < angleDiff && angleDiff < 0.0);
    }

    public static boolean isInFOV(Entity entity, double angle) {
        return isInFOV(mc.player, entity, angle);
    }

    public static float[] getLookNeeded(final Entity entity, final double x, final double y, final double z) {
        final double d = x + 0.5 - entity.posX;
        final double g = y - entity.posY;
        final double e = z + 0.5 - entity.posZ;
        final double h = Math.sqrt(d * d + e * e);
        return new float[] {(float)(Math.atan2(e, d) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(g, h) * 180.0 / 3.141592653589793))};
    }

    private static float getAngle360(final float dir, final float yaw) {
        final float f = Math.abs(yaw - dir) % 360.0f;
        return (f > 180.0f) ? (360.0f - f) : f;
    }

    public static float getFixedRotation(float rot) {
        return getDeltaMouse(rot) * getGCDValue();
    }

    public static float getGCDValue() {
        return (float) ((double) getGCD() * 0.15);
    }

    public static float getGCD() {
        float f1 = (float) ((double) mc.gameSettings.mouseSensitivity * 0.6 + 0.2);
        return f1 * f1 * f1 * 8.0f;
    }

    public static float getDeltaMouse(float delta) {
        return Math.round(delta / getGCDValue());
    }
}