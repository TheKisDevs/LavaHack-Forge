@file:Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")

package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageHudModule
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.util.enums.SpeedUnits
import com.kisman.cc.util.math.sqrt2
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 14.05.2022
 */

var speed = ""

class Speed : AverageHudModule(
    "Speed",
    "Displays your current speed.",
    { "Speed: ${TextFormatting.GRAY}$speed" }
) {
    private val speedUnit = register(SettingEnum<SpeedUnits>("Speed Unit", this, SpeedUnits.KMH))

    private val timer = timer()

    private var prevPosX = 0.0
    private var prevPosZ = 0.0

    init {
        super.setDisplayInfo { "[${(speedUnit.valEnum as SpeedUnits).displayInfo}]" }
    }

    override fun onEnable() {
        super.onEnable()
        timer.reset()
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        if(timer.passedMillis(1000L)) {
            prevPosX = mc.player.prevPosX
            prevPosZ = mc.player.prevPosZ
        }

        val deltaX = mc.player.posX - prevPosX
        val deltaZ = mc.player.posZ - prevPosZ

        val distance = sqrt2(deltaX * deltaX + deltaZ * deltaZ)

        speed = "${speedUnit.valEnum.formatter.format(
            when(speedUnit.valEnum) {
                SpeedUnits.BPS -> distance * 20
                SpeedUnits.KMH -> (distance / 1000) / (0.05 / 3600)
            }
        )} ${speedUnit.valEnum.displayInfo}"
    }
}