package com.kisman.cc.ai.cpvp.action.actions

import com.kisman.cc.ai.cpvp.action.Action
import com.kisman.cc.ai.cpvp.util.CPvPAIHole
import com.kisman.cc.util.enums.CPvPAIActionTriggers

/**
 * @author _kisman_
 */
class MoveToHoleAction(
        val hole : CPvPAIHole
) : Action() {
    override val triggers: ArrayList<CPvPAIActionTriggers> = arrayListOf(
            CPvPAIActionTriggers.SurroundGotBreak
    )

    override fun run() {

    }

    override fun name() : String {
        return "MoveToHole"
    }
}