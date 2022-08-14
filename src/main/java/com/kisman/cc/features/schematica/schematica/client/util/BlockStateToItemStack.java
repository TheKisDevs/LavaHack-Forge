package com.kisman.cc.features.schematica.schematica.client.util;

import com.kisman.cc.features.schematica.schematica.client.world.SchematicWorld;
import com.kisman.cc.features.schematica.schematica.reference.Reference;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@MethodsReturnNonnullByDefault
public class BlockStateToItemStack {
    public static ItemStack getItemStack(final IBlockState blockState, final RayTraceResult rayTraceResult, final SchematicWorld world, final BlockPos pos, final EntityPlayer player) {
        final Block block = blockState.getBlock();

        try {
            final ItemStack itemStack = block.getPickBlock(blockState, rayTraceResult, world, pos, player);
            if (!itemStack.isEmpty()) {
                return itemStack;
            }
        } catch (final Exception e) {
            Reference.logger.debug("Could not get the pick block for: {}", blockState, e);
        }

        return ItemStack.EMPTY;
    }
}
