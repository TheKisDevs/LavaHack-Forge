package com.kisman.cc.mixin.mixins.baritone;

import baritone.utils.accessor.IBitArray;
import baritone.utils.accessor.IBlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BitArray;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.IBlockStatePalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockStateContainer.class)
public class MixinBlockStateContainer implements IBlockStateContainer {
    @Shadow protected BitArray storage;
    @Shadow protected IBlockStatePalette palette;
    @Override public IBlockState getAtPalette(int index) {
        return palette.getBlockState(index);
    }
    @Override public int[] storageArray() {
        return ((IBitArray) storage).toArray();
    }
}
