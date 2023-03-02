package com.kisman.cc.mixin.mixins.baritone;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.utils.IPlayerContext;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Brady
 * @since 1/29/2019
 */
@Mixin(RenderChunk.class)
public class MixinRenderChunk {

    @Redirect(
            method = "rebuildChunk",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/world/ChunkCache.isEmpty()Z"
            )
    )
    private boolean isEmpty(ChunkCache chunkCache) {
        if (!chunkCache.isEmpty()) {
            postRenderBlockStartEvent();

            return false;
        }

        if (Baritone.settings().renderCachedChunks.value && !Minecraft.getMinecraft().isSingleplayer()) {
            Baritone baritone = (Baritone) BaritoneAPI.getProvider().getPrimaryBaritone();
            IPlayerContext ctx = baritone.getPlayerContext();

            if (ctx.player() != null && ctx.world() != null && baritone.bsi != null) {
                BlockPos position = ((RenderChunk) (Object) this).getPosition();
                // RenderChunk extends from -1,-1,-1 to +16,+16,+16
                // then the constructor of ChunkCache extends it one more (presumably to get things like the connected status of fences? idk)
                // so if ANY of the adjacent chunks are loaded, we are unempty
                for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) if (baritone.bsi.isLoaded(16 * dx + position.getX(), 16 * dz + position.getZ())) {
                    postRenderBlockStartEvent();

                    return false;
                }
            }
        }

        return true;
    }

    private void postRenderBlockStartEvent() {
        EventRenderBlock.Start event = new EventRenderBlock.Start();

        Kisman.EVENT_BUS.post(event);
    }

    @Redirect(
            method = "rebuildChunk",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/world/ChunkCache.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"
            )
    )
    private IBlockState getBlockState(ChunkCache chunkCache, BlockPos pos) {
        IBlockState state = chunkCache.getBlockState(pos);

        EventRenderBlock event = new EventRenderBlock(state, pos);

        Kisman.EVENT_BUS.post(event);

        if (Baritone.settings().renderCachedChunks.value && !Minecraft.getMinecraft().isSingleplayer()) {
            Baritone baritone = (Baritone) BaritoneAPI.getProvider().getPrimaryBaritone();
            IPlayerContext ctx = baritone.getPlayerContext();

            if (ctx.player() != null && ctx.world() != null && baritone.bsi != null) return baritone.bsi.get0(pos);
        }

        return state;
    }
}
