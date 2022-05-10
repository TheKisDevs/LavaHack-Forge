package com.kisman.cc.ai.cpvp.action.actions

import com.kisman.cc.ai.cpvp.action.Action
import com.kisman.cc.util.enums.CPvPAIActionTriggers

/**
 * @author _kisman_
 */
class MoveToTargetAction(
        minDistanceToTarget: Int //TODO
) : Action() {
    override val triggers: ArrayList<CPvPAIActionTriggers> = arrayListOf(
            CPvPAIActionTriggers.PlacePosGotOutFromPlaceRange,
            CPvPAIActionTriggers.TargetGotOutFromPlaceRange,
            CPvPAIActionTriggers.HolesDontExist
    )

    override fun run() {

    }

    override fun name() : String {
        return "MoveToHole"
    }
}