package com.kisman.cc.mixin.mixins.baritone;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.event.events.ChunkEvent;
import baritone.api.event.events.type.EventState;
import baritone.cache.CachedChunk;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Brady
 * @since 8/3/2018
 */
@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Inject(
            method = "handleChunkData",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/world/chunk/Chunk.read(Lnet/minecraft/network/PacketBuffer;IZ)V"
            )
    )
    private void preRead(SPacketChunkData packetIn, CallbackInfo ci) {
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            EntityPlayerSP player = ibaritone.getPlayerContext().player();
            if (player != null && player.connection == this0()) {
                ibaritone.getGameEventHandler().onChunkEvent(
                        new ChunkEvent(
                                EventState.PRE,
                                packetIn.isFullChunk() ? ChunkEvent.Type.POPULATE_FULL : ChunkEvent.Type.POPULATE_PARTIAL,
                                packetIn.getChunkX(),
                                packetIn.getChunkZ()
                        )
                );
            }
        }
    }

    @Inject(
            method = "handleChunkData",
            at = @At("RETURN")
    )
    private void postHandleChunkData(SPacketChunkData packetIn, CallbackInfo ci) {
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            EntityPlayerSP player = ibaritone.getPlayerContext().player();
            if (player != null && player.connection == this0()) {
                ibaritone.getGameEventHandler().onChunkEvent(
                        new ChunkEvent(
                                EventState.POST,
                                packetIn.isFullChunk() ? ChunkEvent.Type.POPULATE_FULL : ChunkEvent.Type.POPULATE_PARTIAL,
                                packetIn.getChunkX(),
                                packetIn.getChunkZ()
                        )
                );
            }
        }
    }

    @Inject(
            method = "handleBlockChange",
            at = @At("RETURN")
    )
    private void postHandleBlockChange(SPacketBlockChange packetIn, CallbackInfo ci) {
        if (!Baritone.settings().repackOnAnyBlockChange.value) {
            return;
        }
        if (!CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.contains(packetIn.getBlockState().getBlock())) {
            return;
        }
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            EntityPlayerSP player = ibaritone.getPlayerContext().player();
            if (player != null && player.connection == this0()) {
                ibaritone.getGameEventHandler().onChunkEvent(
                        new ChunkEvent(
                                EventState.POST,
                                ChunkEvent.Type.POPULATE_FULL,
                                packetIn.getBlockPosition().getX() >> 4,
                                packetIn.getBlockPosition().getZ() >> 4
                        )
                );
            }
        }
    }

    @Inject(
            method = "handleMultiBlockChange",
            at = @At("RETURN")
    )
    private void postHandleMultiBlockChange(SPacketMultiBlockChange packetIn, CallbackInfo ci) {
        if (!Baritone.settings().repackOnAnyBlockChange.value) {
            return;
        }
        if (packetIn.getChangedBlocks().length == 0) {
            return;
        }
        https://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.15
        {
            for (SPacketMultiBlockChange.BlockUpdateData update : packetIn.getChangedBlocks()) {
                if (CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.contains(update.getBlockState().getBlock())) {
                    break https;
                }
            }
            return;
        }
        ChunkPos pos = new ChunkPos(packetIn.getChangedBlocks()[0].getPos());
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            EntityPlayerSP player = ibaritone.getPlayerContext().player();
            if (player != null && player.connection == this0()) {
                ibaritone.getGameEventHandler().onChunkEvent(
                        new ChunkEvent(
                                EventState.POST,
                                ChunkEvent.Type.POPULATE_FULL,
                                pos.x,
                                pos.z
                        )
                );
            }
        }
    }

    @Inject(
            method = "handleCombatEvent",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/Minecraft.displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"
            )
    )
    private void onPlayerDeath(SPacketCombatEvent packetIn, CallbackInfo ci) {
        for (IBaritone ibaritone : BaritoneAPI.getProvider().getAllBaritones()) {
            EntityPlayerSP player = ibaritone.getPlayerContext().player();
            if (player != null && player.connection == this0()) {
                ibaritone.getGameEventHandler().onPlayerDeath();
            }
        }
    }

    private NetHandlerPlayClient this0() {
        return (NetHandlerPlayClient) (Object) this;
    }
}
