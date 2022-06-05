package com.kisman.cc.features.module.Debug

import baritone.api.BaritoneAPI
import baritone.api.pathing.goals.GoalXZ
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module

/**
 * @author _kisman_
 * @since 19:54 of 20.05.2022
 */
class BaritoneTest : Module(
    "BaritoneTest",
    "Test of baritone",
    Category.DEBUG
) {
    override fun onEnable() {
        BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.setGoalAndPath(GoalXZ(1000, 1000))
    }
}