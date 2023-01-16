package com.kisman.cc.util.entity;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.combat.AntiBot;
import com.kisman.cc.util.manager.friend.FriendManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {
    private static final DamageSource EXPLOSION_SOURCE;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isFluid(double y) {
        return mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + y, mc.player.posZ)).getBlock().equals(Blocks.WATER);
    }

    public static boolean canSee(BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5), false, true, false) == null;
    }

    public static boolean isOnLiquid() {
        final double y = EntityUtil.mc.player.posY - 0.03;
        for (int x = MathHelper.floor(EntityUtil.mc.player.posX); x < MathHelper.ceil(EntityUtil.mc.player.posX); ++x) {
            for (int z = MathHelper.floor(EntityUtil.mc.player.posZ); z < MathHelper.ceil(EntityUtil.mc.player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (EntityUtil.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) return true;
            }
        }
        return false;
    }

    public static boolean stopSneaking(final boolean isSneaking) {
        if (isSneaking && mc.player != null) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        return false;
    }

    public static EntityPlayer getTarget(final float range, float wallRange) {
        EntityPlayer currentTarget = null;
        for (int size = mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if(!antibotCheck(player) && AntiBot.instance.isToggled() && AntiBot.instance.mode.checkValString("Zamorozka")) continue;
            if (!isntValid(player, range, wallRange)) {
                if (currentTarget == null) currentTarget = player;
                else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) currentTarget = player;
            }
        }
        return currentTarget;
    }

    public static EntityPlayer getTarget(float range) {
        EntityPlayer currentTarget = null;

        for (EntityPlayer player : mc.world.playerEntities) {
            if(!antibotCheck(player) && AntiBot.instance.isToggled() && AntiBot.instance.mode.checkValString("Zamorozka")) continue;
            if (currentTarget == null || mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) currentTarget = player;
        }

        return currentTarget;
    }

    public static Entity getTarget(float range, float wallRange, boolean players, boolean passive, boolean monsters) {
        Entity currentTarget = null;
        for (Entity entity1 : mc.world.loadedEntityList) {
            if(!(entity1 instanceof EntityLivingBase)) continue;
            EntityLivingBase entity = (EntityLivingBase) entity1;
            if(!antibotCheck(entity) && AntiBot.instance.isToggled() && AntiBot.instance.mode.checkValString("Zamorozka")) continue;
            if (!isntValid(entity, range, wallRange) && !isntValid2(entity, players, passive, monsters)) {
                if (currentTarget == null) currentTarget = entity;
                else if (mc.player.getDistanceSq(entity) < mc.player.getDistanceSq(currentTarget)) currentTarget = entity;
            }
        }
        return currentTarget;
    }

    public static boolean isntValid(final EntityLivingBase entity, final double range, double wallRange) {
        return (mc.player.getDistance(entity) > (mc.player.canEntityBeSeen(entity) ? range : wallRange)) || entity == mc.player || entity.getHealth() <= 0.0f || entity.isDead || FriendManager.instance.isFriend(entity.getName());
    }

    public static boolean antibotCheck(EntityLivingBase entity) {
        return (Kisman.target_by_click != null && Kisman.target_by_click.equals(entity));
    }

    public static boolean isntValid2(final EntityLivingBase entity, boolean players, boolean animals, boolean monsters) {
        return (players && !(entity instanceof EntityPlayer)) || (animals && !isPassive(entity)) || (monsters && !isMobAggressive(entity));
    }

    public static boolean isPassive(Entity e) {
        if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) return false;
        if (e instanceof EntityAgeable || e instanceof EntityAmbientCreature || e instanceof EntitySquid) return true;
        return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
    }

    public static boolean isMobAggressive(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            // arms raised = aggressive, angry = either game or we have set the anger
            // cooldown
            if (((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()) return true;
        } else if (entity instanceof EntityWolf) return ((EntityWolf) entity).isAngry() && !Minecraft.getMinecraft().player.equals(((EntityWolf) entity).getOwner());
        else if (entity instanceof EntityEnderman) return ((EntityEnderman) entity).isScreaming();
        return isHostileMob(entity);
    }

    /**
     * If the mob by default wont attack the player, but will if the player attacks
     * it
     */
    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }
    /**
     * If the mob is hostile
     */
    public static boolean isHostileMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !EntityUtil.isNeutralMob(entity));
    }

    public static boolean isInLiquid(boolean feet) {
        if (mc.player != null) {
            if (mc.player.fallDistance >= 3.0f) return false;
            boolean inLiquid = false;
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
            int y = MathHelper.floor(bb.minY - (feet ? 0.03 : 0.2));
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) return false;
                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
    }
    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }

    public static List<BlockPos> getSquare(BlockPos pos1, BlockPos pos2) {
        List<BlockPos> squareBlocks = new ArrayList<>();
        int x1 = pos1.getX();
        int y1 = pos1.getY();
        int z1 = pos1.getZ();
        int x2 = pos2.getX();
        int y2 = pos2.getY();
        int z2 = pos2.getZ();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x += 1) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z += 1) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y += 1) {
                    squareBlocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return squareBlocks;
    }

    public static boolean basicChecksEntity(Entity pl) {
        return pl.getName().equals(mc.player.getName()) || pl.isDead;
    }

    public static List<BlockPos> getBlocksIn(Entity pl) {
        List<BlockPos> blocks = new ArrayList<>();
        AxisAlignedBB bb = pl.getEntityBoundingBox();
        for (double x = Math.floor(bb.minX); x < Math.ceil(bb.maxX); x++) {
            for (double y = Math.floor(bb.minY); y < Math.ceil(bb.maxY); y++) {
                for (double z = Math.floor(bb.minZ); z < Math.ceil(bb.maxZ); z++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static void setTimer(float speed) {
        mc.timer.tickLength = 50.0f / speed;
    }

    public static void resetTimer() {
        mc.timer.tickLength = 50;
    }

    public static double getDistance(double p_X, double p_Y, double p_Z, double x, double y, double z) {
        return MathHelper.sqrt(getDistanceSq(p_X, p_Y, p_Z, x, y, z));
    }

    public static double getDistanceSq(double p_X, double p_Y, double p_Z, double x, double y, double z) {
        double d0 = p_X - x;
        double d1 = p_Y - y;
        double d2 = p_Z - z;
        return (d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static List<BlockPos> getDynamicTrapBlocks(boolean feet, boolean support){
        List<BlockPos> list1 = new ArrayList<>();
        list1.add(new BlockPos(mc.player.posX + 0.3, mc.player.posY, mc.player.posZ + 0.3));
        list1.add(new BlockPos(mc.player.posX + 0.3, mc.player.posY, mc.player.posZ - 0.3));
        list1.add(new BlockPos(mc.player.posX - 0.3, mc.player.posY, mc.player.posZ - 0.3));
        list1.add(new BlockPos(mc.player.posX - 0.3, mc.player.posY, mc.player.posZ + 0.3));
        List<BlockPos> list2 = new ArrayList<>();
        for(BlockPos pos : list1){
            if(!list2.contains(pos.north()))
                list2.add(pos.north());
            if(!list2.contains(pos.east()))
                list2.add(pos.east());
            if(!list2.contains(pos.south()))
                list2.add(pos.south());
            if(!list2.contains(pos.west()))
                list2.add(pos.west());
        }
        list2.removeAll(list1);
        double height = mc.player.boundingBox.maxX - mc.player.boundingBox.minY;
        int h = (int) (height);
        List<BlockPos> list3 = new ArrayList<>();
        if(feet){
            for(BlockPos pos : list1){
                list3.add(pos.down());
            }
        }
        if(support){
            for(BlockPos pos : list2){
                list3.add(pos.down());
            }
        }
        for(int i = 0; i < h; i++){
            for(BlockPos pos : list2){
                list3.add(pos.up(i));
            }
        }
        list3.add(list3.get(list3.size() - 1).up());
        for(BlockPos pos : list1){
            list3.add(pos.up(h));
        }
        List<BlockPos> list4 = new ArrayList<>();
        for(BlockPos pos : list3){
            if(mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)){
                list4.add(pos);
            }
        }
        return list4;
    }

    /**
     * @author Cubic
     * @return the unwebbed motion
     */
    public static double[] unwebMotion(double[] motion){
        return new double[]{
                motion[0] * 4.0,
                Double.longBitsToDouble(Double.doubleToRawLongBits(motion[1] * 19.99999970197678) + 1),
                motion[2] * 4.0
        };
    }

    public static double applySpeedEffect(EntityLivingBase entity, double speed){
        double r = speed;
        PotionEffect effect;
        if((effect = entity.getActivePotionEffect(MobEffects.SPEED)) != null)
            r += speed * (effect.getAmplifier() + 1.0) * 0.2;
        if((effect = entity.getActivePotionEffect(MobEffects.SLOWNESS)) != null)
            r -= speed * (effect.getAmplifier() + 1.0) * 0.15;
        return r;
    }

    static {
        EXPLOSION_SOURCE = new DamageSource("explosion").setDifficultyScaled().setExplosion();
    }
}
