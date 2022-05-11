package com.kisman.cc.gui

import com.kisman.cc.gui.console.ConsoleGui
import com.kisman.cc.gui.csgo.ClickGuiNew
import com.kisman.cc.gui.halq.HalqGui
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation

//beta
class GuiBlur {
    var blurred = false

    fun update() {
        val mc = Minecraft.getMinecraft()
        if(mc.player == null || mc.world == null) return

        if(mc.currentScreen is HalqGui || mc.currentScreen is ClickGuiNew || mc.currentScreen is ConsoleGui) {
            if(!blurred) {
                mc.entityRenderer.loadShader(ResourceLocation("shaders/post/blur.json"))
            }
        } else if(blurred) {
            try {
                if (mc.player != null && mc.world != null) {
                    mc.entityRenderer.getShaderGroup().deleteShaderGroup()
                }
            } catch (ignored: Exception) { }
        }

        if((mc.currentScreen is HalqGui || mc.currentScreen is ClickGuiNew || mc.currentScreen is ConsoleGui) && !blurred) {

        }
    }
}