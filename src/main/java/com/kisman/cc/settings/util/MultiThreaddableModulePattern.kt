package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.thread.kisman.*
import java.util.function.Supplier

/**
 * TODO: more modes of threads(like AutoReR)
 *
 * @author _kisman_
 * @since 13:31 of 18.06.2022
 */
class MultiThreaddableModulePattern(
    val module : Module
) {
    private val group = module.register(SettingGroup(Setting("Multi Thread", module)))

    private val delay = module.register(group.add(Setting("Delay", module, 15.0, 0.0, 100.0, NumberType.TIME)))
    private val multiThread = module.register(group.add(Setting("Multi Thread", module, false)))

    private val handler = ThreadHandler(Supplier { delay.valLong }, Supplier { multiThread.valBoolean })

    fun reset() {
        handler.reset()
    }

    fun update(task : Runnable) {
        handler.update(task)
    }
}