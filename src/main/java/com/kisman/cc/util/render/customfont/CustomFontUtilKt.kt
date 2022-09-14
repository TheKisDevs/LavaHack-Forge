package com.kisman.cc.util.render.customfont

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.client.Changer
import com.kisman.cc.features.module.client.CustomFontModule
import com.kisman.cc.util.render.customfont.CustomFontUtil.*
import net.minecraft.client.Minecraft

class CustomFontUtilKt {
    companion object {
        fun getCustomFont(
            gui : Boolean
        ) : AbstractFontRenderer {
            return getFont().font
        }

        /*fun getFallbackFont() : AbstractFontRenderer? {
            return getCustomFont(CustomFontModule.instance.fallbackMode.valString, false)
        }*/

        fun getCustomFont() : AbstractFontRenderer {
            return getCustomFont(false)
        }

        fun getStringWidth(text: String, gui: Boolean): Int {
            if(!CustomFontModule.turnOn) return Minecraft.getMinecraft().fontRenderer.getStringWidth(text)
            return getCustomFont(gui).getStringWidth(text)
        }

        fun getStringWidth(text: String): Int {
            return getStringWidth(text, false)
        }

        fun getHeight(gui: Boolean): Int {
            if(!CustomFontModule.turnOn) return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
            return getCustomFont(gui).getHeight()
        }

        fun getHeight(): Int {
            return getHeight(false)
        }

        fun setAntiAlias(antiAlias: Boolean) {
            val font = getCustomFont()
            font.setAntiAlias(antiAlias)
        }

        fun setFractionalMetrics(fractionalMetrics: Boolean) {
            val font = getCustomFont()
            font.setFractionalMetrics(fractionalMetrics)
        }

        fun getAntiAlias(): Boolean {
            val font = getCustomFont();
            return font.getAntiAlias()
        }

        fun getFractionMetrics(): Boolean {
            val font = getCustomFont()
            return font.getFractionMetrics()
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
