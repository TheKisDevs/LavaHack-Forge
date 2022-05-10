package com.kisman.cc.ai.cpvp.action.actions

import com.kisman.cc.ai.cpvp.action.Action
import com.kisman.cc.util.enums.CPvPAIActionTriggers

/**
 * @author _kisman_
 */
class SurroundAction : Action() {
    override val triggers: ArrayList<CPvPAIActionTriggers> = arrayListOf(
            CPvPAIActionTriggers.PlacePosIsLethalForSelf
    )

    override fun run() {

    }

    override fun name() : String {
        return "MoveToHole"
    }
}