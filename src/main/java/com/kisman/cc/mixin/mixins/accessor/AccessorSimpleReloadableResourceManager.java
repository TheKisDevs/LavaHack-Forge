package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author _kisman_
 * @since 14:57 of 24.07.2022
 */
@Mixin(SimpleReloadableResourceManager.class)
public interface AccessorSimpleReloadableResourceManager {
    @Accessor("domainResourceManagers") Map<String, FallbackResourceManager> domainResourceManagers();
}
