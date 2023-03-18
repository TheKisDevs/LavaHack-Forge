package com.kisman.cc.mixin.mixins;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * @author _kisman_
 * @since 11:56 of 18.03.2023
 */
@Mixin(BlockPos.class)
public class MixinBlockPos extends MixinVec3i {
    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos up() {
        return new BlockPos(getX(), getY() + 1, getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos down() {
        return new BlockPos(getX(), getY() - 1, getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos north() {
        return new BlockPos(getX(), getY(), getZ() - 1);
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos east() {
        return new BlockPos(getX() + 1, getY(), getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos west() {
        return new BlockPos(getX() - 1, getY(), getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos south() {
        return new BlockPos(getX(), getY(), getZ() + 1);
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos up(int n) {
        return n == 0 ? this0() : new BlockPos(getX(), getY() + n, getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos down(int n) {
        return n == 0 ? this0() : new BlockPos(getX(), getY() - n, getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos north(int n) {
        return n == 0 ? this0() : new BlockPos(getX(), getY(), getZ() - n);
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos east(int n) {
        return n == 0 ? this0() : new BlockPos(getX() + n, getY(), getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos west(int n) {
        return n == 0 ? this0() : new BlockPos(getX() - n, getY(), getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos south(int n) {
        return n == 0 ? this0() : new BlockPos(getX(), getY(), getZ() + n);
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos offset(EnumFacing facing) {
        Vec3i direction = facing.getDirectionVec();

        return new BlockPos(getX() + direction.getX(), getY() + direction.getY(), getZ() + direction.getZ());
    }

    /**
     * @author _kisman_, leijurv
     * @reason 20% optimization
     */
    @Overwrite
    public BlockPos offset(EnumFacing facing, int n) {
        if(n == 0) return this0();

        Vec3i direction = facing.getDirectionVec();

        return new BlockPos(getX() + direction.getX() * n, getY() + direction.getY() * n, getZ() + direction.getZ() * n);
    }

    private BlockPos this0() {
        return (BlockPos) (Object) this;
    }
}
