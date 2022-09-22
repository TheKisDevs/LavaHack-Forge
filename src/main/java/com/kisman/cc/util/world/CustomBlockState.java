package com.kisman.cc.util.world;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("ALL")
public class CustomBlockState implements IBlockState {

    private final Block block;

    private final IBlockState blockState;

    public CustomBlockState(Block block, IBlockState blockState) {
        this.block = block;
        this.blockState = blockState;
    }

    @Override
    public Collection<IProperty<?>> getPropertyKeys() {
        return blockState.getPropertyKeys();
    }

    @Override
    public <T extends Comparable<T>> T getValue(IProperty<T> iProperty) {
        return blockState.getValue(iProperty);
    }

    @Override
    public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> iProperty, V v) {
        return blockState.withProperty(iProperty, v);
    }

    @Override
    public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> iProperty) {
        return blockState.cycleProperty(iProperty);
    }

    @Override
    public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
        return blockState.getProperties();
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public boolean onBlockEventReceived(World world, BlockPos blockPos, int i, int i1) {
        return blockState.onBlockEventReceived(world, blockPos, i, i1);
    }

    @Override
    public void neighborChanged(World world, BlockPos blockPos, Block block, BlockPos blockPos1) {
        blockState.neighborChanged(world, blockPos, block, blockPos1);
    }

    @Override
    public Material getMaterial() {
        return blockState.getMaterial();
    }

    @Override
    public boolean isFullBlock() {
        return blockState.isFullBlock();
    }

    @Override
    public boolean canEntitySpawn(Entity entity) {
        return blockState.canEntitySpawn(entity);
    }

    @Override
    @Deprecated
    public int getLightOpacity() {
        return blockState.getLightOpacity();
    }

    @Override
    public int getLightOpacity(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getLightOpacity(iBlockAccess, blockPos);
    }

    @Override
    @Deprecated
    public int getLightValue() {
        return blockState.getLightValue();
    }

    @Override
    public int getLightValue(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getLightValue(iBlockAccess, blockPos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return blockState.isTranslucent();
    }

    @Override
    public boolean useNeighborBrightness() {
        return blockState.useNeighborBrightness();
    }

    @Override
    public MapColor getMapColor(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getMapColor(iBlockAccess, blockPos);
    }

    @Override
    public IBlockState withRotation(Rotation rotation) {
        return blockState.withRotation(rotation);
    }

    @Override
    public IBlockState withMirror(Mirror mirror) {
        return blockState.withMirror(mirror);
    }

    @Override
    public boolean isFullCube() {
        return blockState.isFullCube();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress() {
        return blockState.hasCustomBreakingProgress();
    }

    @Override
    public EnumBlockRenderType getRenderType() {
        return blockState.getRenderType();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getPackedLightmapCoords(iBlockAccess, blockPos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue() {
        return blockState.getAmbientOcclusionLightValue();
    }

    @Override
    public boolean isBlockNormalCube() {
        return blockState.isBlockNormalCube();
    }

    @Override
    public boolean isNormalCube() {
        return blockState.isNormalCube();
    }

    @Override
    public boolean canProvidePower() {
        return blockState.canProvidePower();
    }

    @Override
    public int getWeakPower(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
        return blockState.getWeakPower(iBlockAccess, blockPos, enumFacing);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return blockState.hasComparatorInputOverride();
    }

    @Override
    public int getComparatorInputOverride(World world, BlockPos blockPos) {
        return blockState.getComparatorInputOverride(world, blockPos);
    }

    @Override
    public float getBlockHardness(World world, BlockPos blockPos) {
        return blockState.getBlockHardness(world, blockPos);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer entityPlayer, World world, BlockPos blockPos) {
        return blockState.getPlayerRelativeBlockHardness(entityPlayer, world, blockPos);
    }

    @Override
    public int getStrongPower(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
        return blockState.getStrongPower(iBlockAccess, blockPos, enumFacing);
    }

    @Override
    public EnumPushReaction getMobilityFlag() {
        return blockState.getMobilityFlag();
    }

    @Override
    public IBlockState getActualState(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getActualState(iBlockAccess, blockPos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos blockPos) {
        return blockState.getSelectedBoundingBox(world, blockPos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
        return blockState.shouldSideBeRendered(iBlockAccess, blockPos, enumFacing);
    }

    @Override
    public boolean isOpaqueCube() {
        return blockState.isOpaqueCube();
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getCollisionBoundingBox(iBlockAccess, blockPos);
    }

    @Override
    public void addCollisionBoxToList(World world, BlockPos blockPos, AxisAlignedBB axisAlignedBB, List<AxisAlignedBB> list, @org.jetbrains.annotations.Nullable Entity entity, boolean b) {
        blockState.addCollisionBoxToList(world, blockPos, axisAlignedBB, list, entity, b);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getBoundingBox(iBlockAccess, blockPos);
    }

    @Override
    public RayTraceResult collisionRayTrace(World world, BlockPos blockPos, Vec3d vec3d, Vec3d vec3d1) {
        return blockState.collisionRayTrace(world, blockPos, vec3d, vec3d1);
    }

    @Override
    @Deprecated
    public boolean isTopSolid() {
        return blockState.isTopSolid();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
        return blockState.doesSideBlockRendering(iBlockAccess, blockPos, enumFacing);
    }

    @Override
    public boolean isSideSolid(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
        return blockState.isSideSolid(iBlockAccess, blockPos, enumFacing);
    }

    @Override
    public boolean doesSideBlockChestOpening(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
        return blockState.doesSideBlockChestOpening(iBlockAccess, blockPos, enumFacing);
    }

    @Override
    public Vec3d getOffset(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return blockState.getOffset(iBlockAccess, blockPos);
    }

    @Override
    public boolean causesSuffocation() {
        return blockState.causesSuffocation();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing) {
        return blockState.getBlockFaceShape(iBlockAccess, blockPos, enumFacing);
    }
}
