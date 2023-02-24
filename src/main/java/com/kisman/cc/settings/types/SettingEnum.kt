package com.kisman.cc.settings.types

import com.kisman.cc.features.module.Module
//import com.kisman.cc.mixin.mixins.accessor.AccessorClass
import com.kisman.cc.settings.Setting
//import com.kisman.cc.settings.types.enums.Exclude
//import java.util.*
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 12:58 of 04.08.2022
 */
@Suppress("UNCHECKED_CAST")
class SettingEnum<T : Enum<*>>(
    name : String,
    module : Module,
    /*private val*/ t : T
) : Setting(
    name,
    module,
    t
) {
    override fun getValEnum() : T = super.getValEnum() as T

    override fun setTitle(title : String) : SettingEnum<T> = super.setTitle(title) as SettingEnum<T>

    override fun setVisible(visible : Supplier<Boolean>) : SettingEnum<T> = super.setVisible(visible) as SettingEnum<T>

    fun getSupplierEnum0() : Supplier<T> = Supplier { valEnum }

    fun register() : SettingEnum<T> = super.parent.register(this) as SettingEnum<T>

    fun group(group : SettingGroup) : SettingEnum<T> = group.add(this)

    fun onChange(
        onChange0 : (SettingEnum<T>) -> Any
    ) : SettingEnum<T> = super.onChange { onChange0(this) } as SettingEnum<T>
}