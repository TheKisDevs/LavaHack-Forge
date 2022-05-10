package com.kisman.cc.ai.cpvp.action

import com.kisman.cc.util.enums.CPvPAIActionTriggers

/**
 * @author _kisman_
 */
abstract class Action {
    abstract val triggers : ArrayList<CPvPAIActionTriggers>

    abstract fun run()
    abstract fun name() : String
}