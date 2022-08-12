package com.kisman.cc.mixin.mixins.baritone;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.utils.IPlayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkRenderWorker.class)
public class MixinChunkRenderWorker {
    @Shadow private boolean isChunkExisting(BlockPos pos, World worldIn) {return false;}

    @Redirect(
            method = "processTask",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/renderer/chunk/ChunkRenderWorker.isChunkExisting(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;)Z"
            )
    )
    private boolean isChunkExisting(ChunkRenderWorker worker, BlockPos pos, World world) {
        if (Baritone.settings().renderCachedChunks.value && !Minecraft.getMinecraft().isSingleplayer()) {
            Baritone baritone = (Baritone) BaritoneAPI.getProvider().getPrimaryBaritone();
            IPlayerContext ctx = baritone.getPlayerContext();
            if (ctx.player() != null && ctx.world() != null && baritone.bsi != null) {
                return baritone.bsi.isLoaded(pos.getX(), pos.getZ()) || this.isChunkExisting(pos, world);
            }
        }

        return this.isChunkExisting(pos, world);
    }
}

