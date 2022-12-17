package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 17:51 of 17.12.2022
 */
@Mixin(CPacketUseEntity.class)
public interface AccessorCPacketUseEntity {
    @Accessor("entityId") int entityId();
}
