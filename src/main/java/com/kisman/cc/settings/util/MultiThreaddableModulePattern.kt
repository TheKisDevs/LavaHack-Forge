package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.thread.kisman.GlobalThreads

/**
 * TODO: more modes of threads(like AutoReR)
 *
 * @author _kisman_
 * @since 13:31 of 18.06.2022
 */
class MultiThreaddableModulePattern(
    val module : Module
) : GlobalThreads {
    private val group = module.register(SettingGroup(Setting("Multi Thread", module)))

    private val delay = module.register(group.add(Setting("Delay", module, 15.0, 0.0, 100.0, NumberType.TIME)))
    private val multiThread = module.register(group.add(Setting("Multi Thread", module, false)))

    private val timer = TimerUtils()

    fun reset() {
        timer.reset()
    }

    fun update(task : Runnable) {
        if(timer.passedMillis(delay.valLong)) {
            timer.reset()

            if(multiThread.valBoolean) {
                executor.submit(task)
            } else {
                task.run()
            }
        }
    }
}