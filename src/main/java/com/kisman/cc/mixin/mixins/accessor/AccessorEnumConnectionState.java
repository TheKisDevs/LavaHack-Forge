package com.kisman.cc.mixin.mixins.accessor;

import com.google.common.collect.BiMap;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author _kisman_
 * @since 22:13 of 24.08.2022
 */
@Mixin(EnumConnectionState.class)
public interface AccessorEnumConnectionState {
    @Accessor("directionMaps") Map<EnumPacketDirection, BiMap<Integer, Class<? extends Packet<?>>>> directionMaps();
}
