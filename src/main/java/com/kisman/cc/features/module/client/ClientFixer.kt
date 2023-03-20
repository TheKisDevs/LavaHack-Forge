package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.mixin.mixins.accessor.AccessorFontRenderer
import net.minecraft.util.ResourceLocation

/**
 * @author _kisman_
 * @since 23:46 of 03.09.2022
 */
@ModuleInfo(
    name = "ClientFixer",
    desc = "Implementation of Client Fixer mod",
    category = Category.CLIENT,
    wip = true
)
class ClientFixer : Module() {
    init {
        instance = this
    }

    companion object {
        @JvmField var instance : ClientFixer? = null
    }

    override fun onEnable() {
        super.onEnable()
        (mc.fontRenderer as AccessorFontRenderer).locationFontTexture(ResourceLocation("textures/font/ascii_fat.png"))
    }

    override fun onDisable() {
        super.onDisable()
        (mc.fontRenderer as AccessorFontRenderer).locationFontTexture(ResourceLocation("textures/font/ascii.png"))
    }
}