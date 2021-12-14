package com.kisman.cc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class CrystalUtils {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean canSeePos(BlockPos pos) {
        return CrystalUtils.mc.world.rayTraceBlocks(new Vec3d(CrystalUtils.mc.player.posX, CrystalUtils.mc.player.posY + (double)CrystalUtils.mc.player.getEyeHeight(), CrystalUtils.mc.player.posZ), new Vec3d((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), false, true, false) == null;
    }

    public static boolean CanPlaceCrystalIfObbyWasAtPos(final BlockPos pos) {
        final Minecraft mc = Minecraft.getMinecraft();

        final Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
        final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

        if (floor == Blocks.AIR && ceil == Blocks.AIR) {
            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))).isEmpty()) {
                return true;
            }
        }

        return false;
    }


    public static boolean isEntityMoving(EntityLivingBase entityLivingBase) {
        return entityLivingBase.motionX > Double.longBitsToDouble(Double.doubleToLongBits(0.5327718501168097) ^ 0x7FE10C778D0F6544L) || entityLivingBase.motionY > Double.longBitsToDouble(Double.doubleToLongBits(0.07461435496686485) ^ 0x7FB319ED266512E7L) || entityLivingBase.motionZ > Double.longBitsToDouble(Double.doubleToLongBits(0.9006325807477794) ^ 0x7FECD1FB6B00C2E7L);
    }

    public static boolean canPlaceCrystal(final BlockPos pos) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Block block = mc.world.getBlockState(pos).getBlock();

        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
            final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

            if (floor == Blocks.AIR && ceil == Blocks.AIR) {
                if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean placeUnderBlock, boolean multiPlace, boolean holePlace) {
        if (CrystalUtils.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtils.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }
        if (CrystalUtils.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() != Blocks.AIR || placeUnderBlock == false && CrystalUtils.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() != Blocks.AIR) {
            return false;
        }
        if (multiPlace != false) {
            return CrystalUtils.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0))).isEmpty() && placeUnderBlock == false && CrystalUtils.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0))).isEmpty();
        }
        for (Entity entity : CrystalUtils.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 1, 0)))) {
            if (entity instanceof EntityEnderCrystal) continue;
            return false;
        }
        if (placeUnderBlock == false) {
            for (Entity entity : CrystalUtils.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos.add(0, 2, 0)))) {
                if (entity instanceof EntityEnderCrystal || holePlace != false && entity instanceof EntityPlayer) continue;
                return false;
            }
        }
        return true;
    }

    /// Returns a BlockPos object of player's position floored.
    public static BlockPos GetPlayerPosFloored(final EntityPlayer p_Player) {
        return new BlockPos(Math.floor(p_Player.posX), Math.floor(p_Player.posY), Math.floor(p_Player.posZ));
    }

    public static List<BlockPos> findCrystalBlocks(final EntityPlayer p_Player, float p_Range) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(
                BlockInteractionHelper.getSphere(GetPlayerPosFloored(p_Player), p_Range, (int) p_Range, false, true, 0)
                        .stream().filter(CrystalUtils::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> getSphere(EntityPlayer target, float range, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int x = target.getPosition().getX() - (int)range;
        while ((float)x <= (float) target.getPosition().getX() + range) {
            int z = target.getPosition().getZ() - (int)range;
            while ((float)z <= (float) target.getPosition().getZ() + range) {
                int y;
                int n = y = sphere != false ? target.getPosition().getY() - (int)range : target.getPosition().getY();
                while ((float)y < (float) target.getPosition().getY() + range) {
                    double distance = (target.getPosition().getX() - x) * (target.getPosition().getX() - x) + (target.getPosition().getZ() - z) * (target.getPosition().getZ() - z) + (sphere != false ? (target.getPosition().getY() - y) * (target.getPosition().getY() - y) : 0);
                    if (distance < (double)(range * range) && (!hollow || distance >= ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(638.4060856917202) ^ 0x7F73F33FA9DAEA7FL)) * ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(13.015128470890444) ^ 0x7FDA07BEEB3F6D07L)))) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }

        return blocks;
    }

    public static List<BlockPos> getSphere(float range, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int x = mc.player.getPosition().getX() - (int)range;
        while ((float)x <= (float) mc.player.getPosition().getX() + range) {
            int z = mc.player.getPosition().getZ() - (int)range;
            while ((float)z <= (float) mc.player.getPosition().getZ() + range) {
                int y;
                int n = y = sphere != false ? mc.player.getPosition().getY() - (int)range : mc.player.getPosition().getY();
                while ((float)y < (float) mc.player.getPosition().getY() + range) {
                    double distance = ( mc.player.getPosition().getX() - x) * (mc.player.getPosition().getX() - x) + (mc.player.getPosition().getZ() - z) * (mc.player.getPosition().getZ() - z) + (sphere != false ? (mc.player.getPosition().getY() - y) * (mc.player.getPosition().getY() - y) : 0);
                    if (distance < (double)(range * range) && (hollow == false || distance >= ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(638.4060856917202) ^ 0x7F73F33FA9DAEA7FL)) * ((double)range - Double.longBitsToDouble(Double.doubleToLongBits(13.015128470890444) ^ 0x7FDA07BEEB3F6D07L)))) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }

        return blocks;
    }

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity, int interlopedAmount) {
        if (entity == Minecraft.getMinecraft().player) {
            if (Minecraft.getMinecraft().player.capabilities.isCreativeMode)
                return 0.0f;
        }

        float doubleExplosionSize = 12.0F;

        double dist = entity.getDistance(posX, posY, posZ);
        
        if (dist > doubleExplosionSize)
            return 0f;

        if (interlopedAmount > 0) {
            Vec3d l_Interloped = EntityUtil.getInterpolatedAmount(entity, interlopedAmount);
            dist = EntityUtil.getDistance(l_Interloped.x, l_Interloped.y, l_Interloped.z, posX, posY, posZ);
        }

        double distancedsize = dist / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (int) ((v * v + v) / 2.0D * 7.0D * doubleExplosionSize + 1.0D);
        double finald = 1.0D;
        /*
         * if (entity instanceof EntityLivingBase) finald =
         * getBlastReduction((EntityLivingBase) entity,getDamageMultiplied(damage));
         */
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(world, damage),
                    new Explosion(world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(),
                    (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage -= damage / 4;
            }
            // damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        }

        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(final World p_World, float damage) {
        int diff = p_World.getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(final World world, EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(world, crystal.posX, crystal.posY, crystal.posZ, entity, 0);
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean check) {
        return canPlaceCrystal(blockPos, check, true);
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean check, final boolean entity) {
        if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }
        final BlockPos boost = blockPos.add(0, 1, 0);
        return mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() == Blocks.AIR && (!entity || mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB((double)boost.getX(), (double)boost.getY(), (double)boost.getZ(), (double)(boost.getX() + 1), (double)(boost.getY() + (check ? 2 : 1)), (double)(boost.getZ() + 1)), e -> !(e instanceof EntityEnderCrystal)).size() == 0);
    }

    public static float calculateDamage(World world, BlockPos pos, Entity entity) {
        return calculateDamage(world, pos.getX(), pos.getY(), pos.getZ(), entity, 0);
    }
}