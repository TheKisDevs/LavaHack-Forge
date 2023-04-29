package com.kisman.cc.features.module.client.guimodifier

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderGui
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.client.interfaces.IPositionableGui
import com.kisman.cc.util.enums.GuiAnimations
import com.kisman.cc.util.enums.dynamic.EasingEnum
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 13:38 of 23.04.2023
 */
@ModuleInfo(
    name = "GuiAnimator",
    display = "Animations",
    desc = "Custom open/close gui animations",
    submodule = true
)
class GuiAnimator : Module() {
    private val open = register(register(SettingGroup(Setting("Open", this))).add(SettingsList("mode", SettingEnum("Open", this, GuiAnimations.None).setTitle("Mode"), "easing", SettingArray("Open Easing", this, EasingEnum.Easing.Linear, EasingEnum.allEasings).setTitle("Easing"), "length", Setting("Open Length", this, 1000.0, 100.0, 10000.0, NumberType.TIME).setTitle("Length"))))
    private val close = register(register(SettingGroup(Setting("Close", this))).add(SettingsList("mode", SettingEnum("Close", this, GuiAnimations.None).setTitle("Mode"), "easing", SettingArray("Close Easing", this, EasingEnum.Easing.Linear, EasingEnum.allEasings).setTitle("Easing"), "length", Setting("Close Length", this, 1000.0, 100.0, 10000.0, NumberType.TIME).setTitle("Length"))))

    private val map1 = mutableMapOf<GuiScreen, Long>()
    private val map2 = mutableMapOf<GuiScreen, Long>()

    private var flag = false

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(renderGuiPre)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(renderGuiPre)
    }

    @SubscribeEvent
    fun onGuiOpen(
        event : GuiOpenEvent
    ) {
        if(!flag) {
            val gui = event.gui

            if (gui == null) {
                if (mc.currentScreen != null && mc.currentScreen is IPositionableGui) {
                    map1.remove(mc.currentScreen!!)
                    map2[mc.currentScreen!!] = System.currentTimeMillis()
                    event.isCanceled = true
                }
            } else if (gui is IPositionableGui) {
                map1[gui] = System.currentTimeMillis()
                map2.remove(gui)
            }
        }
    }

    private val renderGuiPre = Listener<EventRenderGui.Pre>(EventHook {
        val gui = mc.currentScreen

        if(gui is IPositionableGui) {
            if (map1.contains(gui)) {
                val animation = open.get0<SettingEnum<GuiAnimations>>("mode").valEnum.animation()
                val progress = open.get0<SettingArray<EasingEnum.IEasing>>("easing").valElement.task.doTask((System.currentTimeMillis() - map1[gui]!!).toDouble() / open["length"].valDouble)

                if(System.currentTimeMillis() - map1[gui]!! > open["length"].valDouble) {
                    map1.remove(gui)
                }

                animation?.handler?.open(gui, progress)
            }

            if(map2.contains(gui)) {
                val animation = close.get0<SettingEnum<GuiAnimations>>("mode").valEnum.animation()
                val progress = close.get0<SettingArray<EasingEnum.IEasing>>("easing").valElement.task.doTask((System.currentTimeMillis() - map2[gui]!!).toDouble() / close["length"].valDouble)

                if(System.currentTimeMillis() - map2[gui]!! > close["length"].valDouble) {
                    flag = true
                    mc.displayGuiScreen(null)
                    flag = false
                }

                animation?.handler?.close(gui, progress)
            }
        }
    })
}