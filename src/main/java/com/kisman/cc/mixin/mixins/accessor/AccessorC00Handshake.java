package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 1:40 of 21.08.2022
 */
@Mixin(C00Handshake.class)
public interface AccessorC00Handshake {
    @Accessor("ip") String ip();
    @Accessor("port") int port();
}
