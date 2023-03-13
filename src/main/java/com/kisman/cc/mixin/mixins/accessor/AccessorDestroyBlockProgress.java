package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.renderer.DestroyBlockProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 17:54 of 13.03.2023
 */
@Mixin(DestroyBlockProgress.class)
public interface AccessorDestroyBlockProgress {
    @Accessor("miningPlayerEntId") int entityID();
}
