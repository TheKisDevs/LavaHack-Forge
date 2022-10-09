package com.kisman.cc.features.module.client.baritone

import baritone.api.Settings
import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.settings.EventSettingChange
import com.kisman.cc.settings.Setting
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

/**
 * Note: only for boolean settings!!!
 *
 * @author _kisman_
 * @since 16:44 of 09.10.2022
 */
@Suppress("UNCHECKED_CAST")
/*abstract*/ class BaritoneSetting<T>(
    private val lavahackSetting : Setting,
    private val baritoneSetting : Settings.Setting<T>
) {
    private val onChange = Listener<EventSettingChange>(EventHook {
        if(it.setting == lavahackSetting) {
            /*if(lavahackSetting.isCheck)*/ baritoneSetting.value = lavahackSetting.valBoolean as T
//            onChange()
        }
    })

    init {
        if((baritoneSetting.value == true || baritoneSetting.value == false) && lavahackSetting.isCheck) {
            Kisman.EVENT_BUS.subscribe(onChange)
        }
    }

//    abstract fun onChange()
}