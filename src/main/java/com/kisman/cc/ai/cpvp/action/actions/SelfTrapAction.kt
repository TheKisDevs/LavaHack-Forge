package com.kisman.cc.ai.cpvp.action.actions

import com.kisman.cc.ai.cpvp.action.Action
import com.kisman.cc.util.enums.CPvPAIActionTriggers

/**
 * @author _kisman_
 */
class SelfTrapAction : Action() {
    override val triggers: ArrayList<CPvPAIActionTriggers> = arrayListOf(
            CPvPAIActionTriggers.TargetIsMovingToYourHole
    )

    override fun run() {

    }

    override fun name() : String {
        return "MoveToHole"
    }
}