package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.world.DamageSyncHandler

/**
 * @author _kisman_
 * @since 17:09 of 11.08.2022
 */
@Suppress("PropertyName")
class DamageSyncPattern(
    module : Module
) : AbstractPattern<DamageSyncPattern>(
    module
) {
    val group_ = setupGroup(SettingGroup(Setting("Damage Sync", module)))

    private val damageSync = setupSetting(group_.add(Setting("Damage Sync", module, false).setTitle("State")))
    private val damageSyncDelay = setupSetting(group_.add(Setting("Damage Sync Delay", module, 500.0, 0.0, 2000.0, NumberType.TIME).setTitle("Delay")))
    private val damageSyncMinDamageOffset = setupSetting(group_.add(Setting("Damage Sync Min Damage Offset", module, 5.0, 0.0, 37.0, true).setTitle("Min Offset")))

    val handler = DamageSyncHandler(
        damageSync.supplierBoolean,
        damageSync.supplierLong,
        damageSync.supplierDouble
    )

    override fun preInit(): DamageSyncPattern {
        if(group != null) {
            group?.add(group_)
        }

        return this
    }

    override fun init(): DamageSyncPattern {
        module.register(group_)
        module.register(damageSync)
        module.register(damageSyncDelay)
        module.register(damageSyncMinDamageOffset)

        return this
    }
}