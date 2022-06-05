package com.kisman.cc.util.render.customfont

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

        //cringe code by kisman
        fun setFonts(style : FontStyles) {
            comfortaal20 = CustomFontRenderer(getFontTTF("comfortaa-light", style, 22), true, true)
            comfortaal18 = CustomFontRenderer(getFontTTF("comfortaa-light", style, 18), true, true)
            comfortaal15 = CustomFontRenderer(getFontTTF("comfortaa-light", style, 15), true, true)
            comfortaal16 = CustomFontRenderer(getFontTTF("comfortaa-light", style, 16), true, true)

            comfortaab72 = CustomFontRenderer(getFontTTF("comfortaa-bold", style, 72), true, true)
            comfortaab55 = CustomFontRenderer(getFontTTF("comfortaa-bold", style, 55), true, true)
//            comfortaab20 = CustomFontRenderer(getFontTTF("comfortaa-bold", style, 72), true, true)
            comfortaab18 = CustomFontRenderer(getFontTTF("comfortaa-bold", style, 18), true, true)
//            comfortaab16 = CustomFontRenderer(getFontTTF("comfortaa-bold", style, 72), true, true)

            //20
            comfortaa18 = CustomFontRenderer(getFontTTF("comfortaa-regular", style, 18), true, true)
            //15

            consolas18 = CustomFontRenderer(getFontTTF("consolas", style, 18), true, true)
            consolas16 = CustomFontRenderer(getFontTTF("consolas", style, 16), true, true)
            consolas15 = CustomFontRenderer(getFontTTF("consolas", style, 15), true, true)

            sfui19 = CustomFontRenderer(getFontTTF("sf-ui", style, 19), true, true)
            //18

            futura20 = CustomFontRenderer(getFontTTF("futura-normal", style, 20), true, true)
            //18

            lexendDeca18 = CustomFontRenderer(getFontTTF("lexenddeca-regular", style, 18), true, true)

            century18 = CustomFontRenderer(getFontTTF("main", style, 18), true, true)
        }
    }
}
