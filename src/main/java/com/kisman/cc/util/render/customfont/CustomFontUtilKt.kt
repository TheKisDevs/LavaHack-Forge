package com.kisman.cc.util.render.customfont

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.client.Changer
import com.kisman.cc.features.module.client.CustomFontModule
import com.kisman.cc.util.render.customfont.CustomFontUtil.*
import com.kisman.cc.util.enums.FontStyles
import net.minecraft.client.Minecraft

class CustomFontUtilKt {
    companion object {
        fun getCustomFont(name: String, gui: Boolean): AbstractFontRenderer? {
            return when(name) {
                "Verdana" -> verdana18
                "Comfortaa" -> comfortaa18
                "Comfortaa Light" -> comfortaal18
                "Comfortaa Bold" -> comfortaab18
                "Consolas" -> if(gui) consolas15 else consolas18
                "LexendDeca" -> lexendDeca18
                "Futura" -> futura20
                "SfUi" -> sfui19
                "Century" -> century18
                "Jellee Bold" -> jelleeb18
                else -> null
            }
        }

        fun getFallbackFont() : AbstractFontRenderer? {
            return getCustomFont(CustomFontModule.instance.fallbackMode.valString, false)
        }

        fun getCustomFont(name: String): AbstractFontRenderer? {
            return getCustomFont(name, false)
        }

        fun getStringWidth(name: String, text: String, gui: Boolean): Int {
            if(name == null || !CustomFontModule.turnOn) return Minecraft.getMinecraft().fontRenderer.getStringWidth(text)
            return getCustomFont(name, gui)?.getStringWidth(text) ?: 0
        }

        fun getStringWidth(name: String, text: String): Int {
            return getStringWidth(name, text, false)
        }

        fun getHeight(name: String, gui: Boolean): Int {
            if(name == null || !CustomFontModule.turnOn) return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
            return getCustomFont(name, gui)?.getHeight() ?: Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
        }

        fun getHeight(name: String): Int {
            return getHeight(name, false)
        }

        fun setAntiAlias(antiAlias: Boolean) {
            val font = getCustomFont(getCustomFontName())
            font?.setAntiAlias(antiAlias)
        }

        fun setFractionalMetrics(fractionalMetrics: Boolean) {
            val font = getCustomFont(getCustomFontName())
            font?.setFractionalMetrics(fractionalMetrics)
        }

        fun getAntiAlias(): Boolean {
            val font = getCustomFont(getCustomFontName());
            return font?.getAntiAlias() ?: true
        }

        fun getFractionMetrics(): Boolean {
            val font = getCustomFont(getCustomFontName())
            return font?.getFractionMetrics() ?: true
        }

        fun getShadowX() : Double {
            return if(getChanger().shadowTextModifier.valBoolean) getChanger().shadowX.valDouble else 1.0
        }

        fun getShadowY() : Double {
            return if(getChanger().shadowTextModifier.valBoolean) getChanger().shadowY.valDouble else 1.0
        }

        private fun getChanger() : Changer {
            return Kisman.instance.moduleManager.getModule("Changer") as Changer
        }
    }
}
