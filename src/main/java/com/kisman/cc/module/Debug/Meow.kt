package com.kisman.cc.module.Debug

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour

class Meow : Module(
        "Meow",
        "Test of group settings",
        Category.DEBUG
) {
    val group1 : SettingGroup = register(SettingGroup(Setting("Group1", this))) as SettingGroup
    val group1_1 : Setting = register(group1.add(Setting("Group1 Slider", this, 1.0, 1.0, 5.0, true)))
    val group1_2 : Setting = register(group1.add(Setting("Group1 Combo", this, "Meow", listOf("Meow", "Meeow", "Meowow", "Cat"))))
    val group1_3 : Setting = register(group1.add(Setting("Group1 Check", this, true)))
    val group1_4 : Setting = register(group1.add(Setting("Group1 Color", this, "Group1 Color", Colour(255, 0, 125, 255))))
}