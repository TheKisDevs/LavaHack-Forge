package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author _kisman_
 * @since 23:43 of 03.09.2022
 */
@Mixin(FontRenderer.class)
public interface AccessorFontRenderer {
    @Accessor("locationFontTexture") void locationFontTexture(ResourceLocation location);
}
