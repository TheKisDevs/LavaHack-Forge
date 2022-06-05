package com.kisman.cc.ai.cpvp

import baritone.api.BaritoneAPI
import com.google.common.collect.Sets
import com.kisman.cc.ai.cpvp.action.Action
import com.kisman.cc.ai.cpvp.action.actions.HoleFillerAction
import com.kisman.cc.ai.cpvp.action.actions.MoveToHoleAction
import com.kisman.cc.ai.cpvp.action.actions.MoveToTargetAction
import com.kisman.cc.ai.cpvp.util.CPvPAIHole
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.world.HoleUtil
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.enums.CPvPAIMovePriorities
import com.kisman.cc.util.enums.CPvPAIMoveTriggers
import com.kisman.cc.util.enums.CPvPAIStages
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.function.Supplier

/**
 * @author _kisman_
 */
class CrystalPvPAI(
        val canUseHoleFiller : Supplier<Boolean>,
        val canUseSurround : Supplier<Boolean>,
        val canUseAutoTrap : Supplier<Boolean>,
        val canBreakSurround : Supplier<Boolean>,
        val canUseKillAura : Supplier<Boolean>,
        val canBeInHoles : Supplier<Boolean>,
        val singleHoles : Supplier<Boolean>,
        val doubleHoles : Supplier<Boolean>,
        val customHoles : Supplier<Boolean>,
        val safeHoles : Supplier<Boolean>,
        val unsafeHoles : Supplier<Boolean>,

        val debug : Supplier<Boolean>,

        val noMoveTargetRange : Supplier<Int>,
        val safeTargetDistance : Supplier<Int>,
        val targetRange : Supplier<Int>,
        val holeRange : Supplier<Int>,
        val minDistanceToTarget : Supplier<Int>,

        val movePriority : Supplier<CPvPAIMovePriorities>,
        val moveTrigger : Supplier<CPvPAIMoveTriggers>
) {
    val mc : Minecraft = Minecraft.getMinecraft()

    var enabled = false

    var target : EntityPlayer? = null

    var stage = CPvPAIStages.None

    var nearestHole : CPvPAIHole? = null

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun init() {
        BaritoneAPI.getProvider().primaryBaritone
        target = null
    }

    @SubscribeEvent fun onClientTick(event : TickEvent.ClientTickEvent) {
        if(mc.player == null || mc.world == null) return

        target = EntityUtil.getTarget(targetRange.get().toFloat())

        if(target == null) return
        if(stage == CPvPAIStages.None) {
            var holes = getHoles(holeRange.get())

            if (holes.isEmpty()) {
                holes = getHoles(50)

                if (holes.isEmpty()) {
                    doAction(MoveToTargetAction(minDistanceToTarget.get()))
                    return
                }
            }

            val nearestHole = getNearestHole(holes)

            if (nearestHole == null) {
                doAction(MoveToTargetAction(minDistanceToTarget.get()))
                return
            }

            this.nearestHole = nearestHole

            doAction(MoveToHoleAction(nearestHole))
            //TODO stage = CPvPAIStages.WaitingForFinishMovingToHole

            return
        }

        if(stage == CPvPAIStages.WaitingForFinishMovingToHole) {
            doAction(HoleFillerAction(target!!, nearestHole))
        }

        //TODO
    }

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        //TODO
    }

    private fun isItSelfHole(hole : CPvPAIHole) : Boolean {
//        var list1 = mc.world.getEntitiesWithinAABBExcludingEntity(null, hole.info.centre)
//        for(pos in EntityUtil.getSphere(mc.player.position, 20f, 20, false, true, 0)) {
//            if(mc.world.getBlockState(pos).block != Blocks.AIR) continue
//            val list2 = mc.world.getEntitiesWithinAABBExcludingEntity(null, mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos))
//
//        }

        return mc.world.getEntitiesWithinAABBExcludingEntity(null, hole.info.centre).contains(mc.player)
    }

    private fun getNearestHole(holes : ArrayList<CPvPAIHole>) : CPvPAIHole? {
        var hole : CPvPAIHole? = null
        var minDistance = 100.0

        for(hole_ in holes) {
            if(isItSelfHole(hole_)) continue
            val distance = mc.player.getDistance(hole_.info.centre.center.x, hole_.info.centre.center.y, hole_.info.centre.center.z)
            if(distance < minDistance) {
                hole = hole_
                minDistance = distance
            }
        }

        return hole
    }

    private fun doAction(action : Action) {
        action.run()
        if(debug.get()) ChatUtility.info().printClientClassMessage("doAction: ${action.name()}")
        //TODO
    }

    fun getHoles(range : Int) : ArrayList<CPvPAIHole> {
        val possibleHoles = Sets.newHashSet<BlockPos>()
        val holes = ArrayList<CPvPAIHole>()

        for(pos in EntityUtil.getSphere(mc.player.position, range.toFloat(), range, false, true, 0)) {
            if(mc.world.getBlockState(pos).block != Blocks.AIR) continue
            if(mc.world.getBlockState(pos.add(0, -1, 0)).block == Blocks.AIR) continue
            if(mc.world.getBlockState(pos.add(0, 1, 0)).block != Blocks.AIR) continue
            if(mc.world.getBlockState(pos.add(0, 2, 0)).block == Blocks.AIR) possibleHoles += pos
        }

        for(pos in possibleHoles) {
            val holeInfo = HoleUtil.isHole(pos, false, false)
            val holeType = holeInfo.type
            if(holeType != HoleUtil.HoleType.NONE) {
                val holeSafety = holeInfo.safety
                if(holeInfo.centre == null) continue

                holes.add(CPvPAIHole(
                        pos,
                        holeInfo,
                        holeType,
                        holeSafety
                ))
            }
        }

        return holes
    }
}