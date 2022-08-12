package com.kisman.cc.mixin.mixins.baritone;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.ChunkEvent;
import baritone.api.event.events.type.EventState;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Brady
 * @since 8/2/2018
 */
@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Inject(
            method = "doPreChunk",
            at = @At("HEAD")
    )
    private void preDoPreChunk(int chunkX, int chunkZ, boolean loadChunk, CallbackInfo ci) {
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            if (ibaritone.getPlayerContext().world() == (WorldClient) (Object) this) {
                ibaritone.getGameEventHandler().onChunkEvent(
                        new ChunkEvent(
                                EventState.PRE,
                                loadChunk ? ChunkEvent.Type.LOAD : ChunkEvent.Type.UNLOAD,
                                chunkX,
                                chunkZ
                        )
                );
            }
        }

    }

    @Inject(
            method = "doPreChunk",
            at = @At("RETURN")
    )
    private void postDoPreChunk(int chunkX, int chunkZ, boolean loadChunk, CallbackInfo ci) {
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            if (ibaritone.getPlayerContext().world() == (WorldClient) (Object) this) {
                ibaritone.getGameEventHandler().onChunkEvent(
                        new ChunkEvent(
                                EventState.POST,
                                loadChunk ? ChunkEvent.Type.LOAD : ChunkEvent.Type.UNLOAD,
                                chunkX,
                                chunkZ
                        )
                );
            }
        }
    }
}
