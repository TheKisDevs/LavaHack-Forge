package com.kisman.cc.mixin.mixins;

import com.kisman.cc.features.module.player.FreeCamRewrite;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.kisman.cc.features.module.exploit.NoGlitchBlocks;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ItemBlock.class)
public class MixinItemBlock {

    @Shadow public boolean placeBlockAt(ItemStack stack,EntityPlayer player,World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,IBlockState state) {return false;}
    private boolean mayPlaceHook1(World world,
                                  Block blockIn,
                                  BlockPos pos,
                                  boolean skip,
                                  EnumFacing sidePlacedOn,
                                  Entity placer)
    {
        if ( FreeCamRewrite.instance.isToggled())
        {
            return mayPlace(world, blockIn, pos, skip, sidePlacedOn, placer);
        }

        return world.mayPlace(blockIn, pos, skip, sidePlacedOn, placer);
    }
        /**
     * {@link ItemBlock#placeBlockAt(ItemStack, EntityPlayer, World,
     * BlockPos, EnumFacing, float, float, float, IBlockState)}
     */
    @Dynamic
    @Redirect(
        method = "onItemUse",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/item/ItemBlock.placeBlockAt" +
                    "(Lnet/minecraft/item/ItemStack;" +
                    "Lnet/minecraft/entity/player/EntityPlayer;" +
                    "Lnet/minecraft/world/World;" +
                    "Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/util/EnumFacing;" +
                    "FFF" +
                    "Lnet/minecraft/block/state/IBlockState;)Z",
            remap = false))
    private boolean onItemUseHook(ItemBlock block,
                                  ItemStack stack,
                                  EntityPlayer player,
                                  World world,
                                  BlockPos pos,
                                  EnumFacing facing,
                                  float hitX,
                                  float hitY,
                                  float hitZ,
                                  IBlockState state)
    {
        return world.isRemote
                && NoGlitchBlocks.instance.isToggled()
                && NoGlitchBlocks.instance.noPlace()
                    || this.placeBlockAt(stack,
                                         player,
                                         world,
                                         pos,
                                         facing,
                                         hitX,
                                         hitY,
                                         hitZ,
                                         state);
    }

    @Redirect(
            method = "canPlaceBlockOnSide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;" +
                            "mayPlace(Lnet/minecraft/block/Block;" +
                            "Lnet/minecraft/util/math/BlockPos;" +
                            "ZLnet/minecraft/util/EnumFacing;" +
                            "Lnet/minecraft/entity/Entity;)Z"))
    private boolean mayPlaceHook2(World world,
                                  Block blockIn,
                                  BlockPos pos,
                                  boolean skip,
                                  EnumFacing sidePlacedOn,
                                  Entity placer)
    {
        if (FreeCamRewrite.instance.isToggled())
        {
            return mayPlace(world, blockIn, pos, skip, sidePlacedOn, placer);
        }

        return world.mayPlace(blockIn, pos, skip, sidePlacedOn, placer);
    }

    private boolean mayPlace(World world,
                             Block blockIn,
                             BlockPos pos,
                             boolean skip,
                             EnumFacing sidePlacedOn,
                             Entity placer)
    {
        IBlockState state = world.getBlockState(pos);
        AxisAlignedBB bb = skip
                ? null
                : blockIn.getDefaultState().getCollisionBoundingBox(world, pos);

        if (bb != Block.NULL_AABB
                && bb != null
                && !this.checkCollision(world, bb.offset(pos), placer))
        {
            return false;
        }
        else if (state.getMaterial() == Material.CIRCUITS
                && blockIn == Blocks.ANVIL)
        {
            return true;
        }
        else
        {
            return state.getBlock().isReplaceable(world, pos)
                    && blockIn.canPlaceBlockOnSide(world, pos, sidePlacedOn);
        }
    }

    private boolean checkCollision(World world,
                                   AxisAlignedBB bb,
                                   Entity entityIn)
    {
        for (Entity entity :
                world.getEntitiesWithinAABBExcludingEntity(null, bb))
        {
            if (entity != null
                    && !entity.isDead
                    && entity.preventEntitySpawning
                    && !entity.equals(entityIn)
                    && !entity.equals(Minecraft.getMinecraft().player)
                    && (entityIn == null
                    || !entity.isRidingSameEntity(entityIn)))
            {
                return false;
            }
        }

        return true;
    }
}

