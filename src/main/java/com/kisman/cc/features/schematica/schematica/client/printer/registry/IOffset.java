package com.kisman.cc.features.schematica.schematica.client.printer.registry;

import net.minecraft.block.state.IBlockState;

public interface IOffset {
    float getOffset(IBlockState blockState);
}
