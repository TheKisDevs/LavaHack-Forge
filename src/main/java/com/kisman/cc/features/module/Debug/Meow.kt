package com.kisman.cc.features.module.Debug

import com.kisman.cc.Kisman
import com.kisman.cc.gui.MainGui
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour

@Suppress("unused", "PrivatePropertyName")
class Meow : Module(
        "Meow",
        "Test of group settings",
        Category.DEBUG
) {
    private val group1 : SettingGroup = register(SettingGroup(Setting("Group1", this)))
    private val group1_1 : Setting = register(group1.add(Setting("Group1 Slider", this, 1.0, 1.0, 5.0, true)))
    private val group1_2 : Setting = register(group1.add(Setting("Group1 Combo", this, "Meow", listOf("Meow", "Meeow", "Meowow", "Cat"))))
    private val group1_3 : Setting = register(group1.add(Setting("Group1 Check", this, true)))
    private val group1_4 : Setting = register(group1.add(Setting("Group1 Color", this, Colour(255, 0, 125, 255))))
    private val group1_5 : SettingGroup = register(group1.add(SettingGroup(Setting("Group1 Group2", this))))
    private val group1_5_1 : Setting = register(group1_5.add(Setting("Group1 Group2 Slider", this, 1.0, 1.0, 5.0, true)))
    private val group1_5_2 : Setting = register(group1_5.add(Setting("Group1 Group2 Combo", this, "Meow", listOf("Meow", "Meeow", "Meowow", "Cat"))))
    private val group1_5_3 : Setting = register(group1_5.add(Setting("Group1 Group2 Check", this, true)))
    private val group1_5_4 : Setting = register(group1_5.add(Setting("Group1 Group2 Color", this, Colour(255, 255, 125, 255))))


    override fun onEnable() {
        Kisman.instance.selectionBar.selection = MainGui.Guis.Music
        mc.displayGuiScreen(Kisman.instance.musicGui)
        isToggled = false
    }
}