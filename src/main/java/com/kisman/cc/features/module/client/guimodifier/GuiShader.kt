package com.kisman.cc.features.module.client.guimodifier

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.gui.KismanGuiScreen
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.util.GuiShaderEntry
import com.kisman.cc.util.guiShaders
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 21:43 of 01.03.2023
 */
@ModuleInfo(
    name = "GuiShader",
    display = "Shaders",
    desc = "Allows to turn on any of built-in gui shaders.",
    submodule = true
)
class GuiShader : Module() {
    private val shader = register(SettingArray("Shader", this, GuiShaderEntry("blur", ResourceLocation("shaders/post/blur.json")), guiShaders()))
    private val minecraftGuis = register(Setting("Minecraft Guis", this, true))
    private val lavahackGuis = register(Setting("LavaHack Guis", this, true))

    @SubscribeEvent
    fun onGuiOpen(
        event : GuiOpenEvent
    ) {
        val gui = event.gui

        fun processGui() {
            if(mc.player != null && mc.world != null) {
                if (gui != null) {
                    mc.entityRenderer.loadShader(shader.valElement.location)
                } else {
                    try {
                        mc.entityRenderer.stopUseShader()
                    } catch (_ : Exception) { }
                }
            }
        }

        if(gui is KismanGuiScreen) {
            if(lavahackGuis.valBoolean) {
                processGui()
            }
        } else if(minecraftGuis.valBoolean) {
            processGui()
        }
    }
}