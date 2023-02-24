package com.kisman.cc.util.entity;

import com.kisman.cc.Kisman;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {
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

    public static boolean antibotCheck(EntityLivingBase entity) {
        return (Kisman.target_by_click != null && Kisman.target_by_click.equals(entity));
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
        double d0 = p_X - x;
        double d1 = p_Y - y;
        double d2 = p_Z - z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
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

    public static boolean eating(EntityPlayer player, EnumHand hand) {
        return player.getHeldItem(hand).item instanceof ItemFood && mc.player.isHandActive() && mc.player.getActiveHand() == hand;
    }

    public static boolean eating(EnumHand hand) {
        return mc.player != null && eating(mc.player, hand);
    }
}
