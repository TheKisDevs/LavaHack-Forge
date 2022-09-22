package com.kisman.cc.settings.types

import com.kisman.cc.features.module.Module
import com.kisman.cc.mixin.mixins.accessor.AccessorClass
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.enums.Exclude
import java.util.*
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 12:58 of 04.08.2022
 */
@Suppress("UNCHECKED_CAST")
class SettingEnum<T : Enum<*>>(
    name : String,
    module : Module,
    private val t : T
) : Setting(
    name,
    module,
    t
) {
    override fun getValEnum() : T {
        /*return try {
            if(t::class.java.getDeclaredField(valString).isAnnotationPresent(Exclude::class.java)) {
                t
            } else {
                (t::class.java as AccessorClass<T>).enumConstantDirectory()[valString]!!
            }
        } catch (_ : Exception) {
            t
        }*/
        return super.getValEnum() as T
    }

    override fun setTitle(title : String) : SettingEnum<T> {
        return super.setTitle(title) as SettingEnum<T>
    }

    override fun setVisible(visible : Supplier<Boolean>) : SettingEnum<T> {
        return super.setVisible(visible) as SettingEnum<T>
    }

    fun getSupplierEnum0() : Supplier<T> {
        return Supplier { valEnum }
    }

    fun register() : SettingEnum<T> {
        return super.parent.register(this) as SettingEnum<T>
    }

    fun group(group : SettingGroup) : SettingEnum<T> {
        return group.add(this) as SettingEnum<T>
    }

    /*override fun getStringValues() : Array<String> {
        val rawValues = ArrayList<T>()

        for(enum in valEnum::class.java.enumConstants) {
            if(!valEnum::class.java.getDeclaredField(enum.name).isAnnotationPresent(Exclude::class.java)) {
                rawValues += enum
            }
        }

        return rawValues.map{ it.name }.toTypedArray()
    }*/
}