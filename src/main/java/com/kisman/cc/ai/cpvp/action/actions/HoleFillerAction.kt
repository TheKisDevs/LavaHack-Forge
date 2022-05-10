package com.kisman.cc.ai.cpvp.action.actions

import com.kisman.cc.ai.cpvp.action.Action
import com.kisman.cc.ai.cpvp.util.CPvPAIHole
import com.kisman.cc.util.enums.CPvPAIActionTriggers
import net.minecraft.entity.player.EntityPlayer

/**
 * @author _kisman_
 */
class HoleFillerAction(
        val target: EntityPlayer,
        val excludedHole: CPvPAIHole?
) : Action() {
    override val triggers: ArrayList<CPvPAIActionTriggers> = arrayListOf(
            CPvPAIActionTriggers.MovingToHole
    )

    override fun run() {
        //TODO
    }

    override fun name(): String {
        return "HoleFiller"
    }
}