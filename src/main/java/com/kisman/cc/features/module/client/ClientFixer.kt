package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.mixin.mixins.accessor.AccessorFontRenderer
import net.minecraft.util.ResourceLocation

/**
 * @author _kisman_
 * @since 23:46 of 03.09.2022
 */
object ClientFixer : Module(
    "ClientFixer",
    "Implementation of Client Fixer mod",
    Category.CLIENT
) {
    override fun onEnable() {
        super.onEnable()
        (mc.fontRenderer as AccessorFontRenderer).locationFontTexture(ResourceLocation("textures/font/ascii_fat.png"))
    }

    override fun onDisable() {
        super.onDisable()
        (mc.fontRenderer as AccessorFontRenderer).locationFontTexture(ResourceLocation("textures/font/ascii.png"))
    }
}