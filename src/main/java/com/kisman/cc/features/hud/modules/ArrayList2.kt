package com.kisman.cc.features.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.AverageMultiLineHudModule
import com.kisman.cc.features.hud.MultiLineElement
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import net.minecraft.util.text.TextFormatting
import org.lwjgl.input.Keyboard

/**
 * @author _kisman_
 * @since 5:48 of 29.03.2023
 */
class ArrayList2 : AverageMultiLineHudModule(
    "ArrayList",
    "Displays feature list"
) {
    private val elements = register(SettingGroup(Setting("Elements", this)))
    private val modules = register(elements.add(Setting("Modules", this, true)))
    private val huds = register(elements.add(Setting("Huds", this, false)))
    private val settings = register(elements.add(Setting("Settings", this, true)))
    private val info = register(Setting("Info", this, true))

    companion object {
        @JvmField val instance = ArrayList2()
    }

    override fun elements(
        elements : ArrayList<MultiLineElement>
    ) {
        if(modules.valBoolean) {
            for(module in Kisman.instance.moduleManager.modules) {
                if(module != null && module.visible) {
                    elements.add(MultiLineElement(module, module.toDisplayString() + if(module.displayInfo.isNotEmpty() && info.valBoolean) " " + TextFormatting.GRAY + module.displayInfo else "") { module.isToggled })
                }
            }
        }

        if(huds.valBoolean) {
            for(module in Kisman.instance.hudModuleManager.modules) {
                if(module != null && module.visible) {
                    elements.add(MultiLineElement(module, module.toDisplayString() + if(module.displayInfo.isNotEmpty() && info.valBoolean) " " + TextFormatting.GRAY + module.displayInfo else "") { module.isToggled })
                }
            }
        }

        if(settings.valBoolean) {
            for(setting in Kisman.instance.settingsManager.settings) {
                if(setting.isCheck && setting.key != Keyboard.KEY_NONE) {
                    elements.add(MultiLineElement(setting, setting.toDisplayString() + if(setting.displayInfo.isNotEmpty() && info.valBoolean) " " + TextFormatting.GRAY + setting.displayInfo else "") { setting.valBoolean })
                }
            }
        }
    }
}