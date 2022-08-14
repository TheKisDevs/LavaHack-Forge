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
    module : Module
) : AbstractPattern<MultiThreaddableModulePattern>(
    module
) {
    val group_ = setupGroup(SettingGroup(Setting("Multi Thread", module)))

    val delay = setupSetting(group_.add(Setting("Delay", module, 15.0, 0.0, 100.0, NumberType.TIME)))
    val multiThread = setupSetting(group_.add(Setting("Multi Thread", module, false)))

    private val handler = ThreadHandler(Supplier { delay.valLong }, Supplier { multiThread.valBoolean })

    override fun preInit(): MultiThreaddableModulePattern {
        if(group != null) {
            group?.add(group_)
        }
        
        return this
    }

    override fun init(): MultiThreaddableModulePattern {
        module.register(group_)
        module.register(delay)
        module.register(multiThread)
        
        return this
    }

    fun reset() {
        handler.reset()
    }

    fun update(task : Runnable) {
        handler.update(task)
    }
}