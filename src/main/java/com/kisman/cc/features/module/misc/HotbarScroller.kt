package com.kisman.cc.features.module.misc

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.TimerUtils

/**
 * @author _kisman_
 * @since 17:45 of 24.05.2022
 */
class HotbarScroller : Module(
    "HotbarScroller",
    "uwu like future.",
    Category.MISC
) {
    private val delay = register(Setting("Delay", this, 100.0, 1.0, 1000.0, NumberType.TIME))
//    private val onlyBlocks = register(Setting("Only Blocks", this, true))

    private val timer = TimerUtils()

    init {
        setDisplayInfo { "[${NumberType.TIME.formatter.apply(delay.valInt)}" }
    }

    override fun onEnable() {
        timer.reset()
    }

    override fun update() {
        var passed = false

        if(timer.passedMillis(delay.valLong)) {
            timer.reset()
            passed = true
        }

        if(mc.player == null || mc.world == null) return

        if(passed) {
            mc.player.inventory.currentItem = getNextValidSlot()
        }
    }

    private fun getNextValidSlot() : Int {
        return if(mc.player.inventory.currentItem == 9) {
            0
        } else {
            mc.player.inventory.currentItem + 1
        }
    }
}