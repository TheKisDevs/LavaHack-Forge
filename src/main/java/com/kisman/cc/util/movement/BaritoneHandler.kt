package com.kisman.cc.util.movement

import baritone.api.BaritoneAPI
import baritone.api.IBaritone
import baritone.api.behavior.IPathingBehavior
import baritone.api.pathing.goals.GoalBlock
import baritone.api.pathing.goals.GoalInverted
import baritone.api.pathing.goals.GoalXZ
import baritone.api.pathing.goals.GoalYLevel
import baritone.api.process.ICustomGoalProcess
import net.minecraft.util.math.BlockPos


/**
 * @author _kisman_
 * @since 20:33 of 12.08.2022
 */

fun baritone() : IBaritone = BaritoneAPI.getProvider().primaryBaritone

fun goalProcess() : ICustomGoalProcess = baritone().customGoalProcess

fun pathingBehavior() : IPathingBehavior = baritone().pathingBehavior

fun gotoXZ(
    x : Int,
    z : Int
) {
    goalProcess().setGoalAndPath(GoalXZ(x, z))
}

fun gotoXYZ(
    x : Int,
    y : Int,
    z : Int
) {
    goalProcess().setGoalAndPath(GoalBlock(BlockPos(x, y, z)))
}

fun gotoPos(
    pos : BlockPos
) {
    goalProcess().setGoalAndPath(GoalBlock(pos))
}

fun gotoY(
    y : Int
) {
    goalProcess().setGoalAndPath(GoalYLevel(y))
}

fun gotoXZInverted(
    x : Int,
    z : Int
) {
    goalProcess().setGoalAndPath(GoalInverted(GoalXZ(x, z)))
}

fun stop() {
    pathingBehavior().cancelEverything()
}

fun active() : Boolean = goalProcess().isActive