package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.enums.AntiCrystalAngles
import com.kisman.cc.util.enums.RotationModes
import com.kisman.cc.util.enums.Rotations
import com.kisman.cc.util.enums.SwitchModes

/**
 * @author _kisman_
 * @since 15.05.2022
 */
class AntiCrystal : Module(
        "AntiCrystal",
        "New crystal pvp meta btw))",
        Category.COMBAT
) {
    private val bowGroup = register(SettingGroup(Setting("BowWithArrows", this)))

    private val bSmart = register(bowGroup.add(Setting("Smart", this, true)))
    private val bVector = register(bowGroup.add(Setting("Vector", this, AntiCrystalAngles.SurroundBlocks).setVisible { bSmart.valBoolean }))
    private val bSwitch = register(bowGroup.add(Setting("Switch", this, SwitchModes.Silent)))
    private val bRotationMode = register(bowGroup.add(Setting("Rotation Mode", this, RotationModes.Silent)))
    private val bRotation = register(bowGroup.add(Setting("Rotation", this, Rotations.Packet)))

    private fun doNewCPvPMetaBtw() {

    }
}