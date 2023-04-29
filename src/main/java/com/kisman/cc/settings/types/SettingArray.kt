package com.kisman.cc.settings.types

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 23:07 of 11.10.2022
 */
@Suppress("UNCHECKED_CAST")
class SettingArray<T>(
    name : String,
    module : Module,
    private var value : T,
    private val list : List<T>
) : Setting(
    name,
    module
) {
    val valElement : T
        get() = value
    init {
        mode = "Combo"
        setupBinders(options)
    }

    /*fun getValElement() : T = value

    fun setValElement(t : T) : SettingArray<T> {
        value = t

        return this
    }*/

    override fun getValString() : String = value.toString()

    override fun setValString(
        `in` : String
    ) : Setting {
        for(element in list) {
            if(`in` == element.toString()) {
                value = element
                break
            }
        }

        return this
    }

    override fun checkValString(
        str : String
    ) : Boolean = value.toString().equals(str, true)

    override fun getStringValues() : Array<String> = options.toTypedArray()


    override fun getOptions() : ArrayList<String> {
        val values = ArrayList<String>()

        for(element in list) {
            values += element.toString()
        }

        return values
    }

    override fun getStringFromIndex(
        index : Int
    ) : String = stringValues[index]


    override fun getSupplierString() : Supplier<String> = Supplier { valString }

    override fun setTitle(
        title : String
    ) : SettingArray<T> = super.setTitle(title) as SettingArray<T>

    override fun setVisible(
        setting : Setting
    ) : SettingArray<T> = super.setVisible(setting) as SettingArray<T>

    override fun setVisible(
        suppliner : Supplier<Boolean>
    ) : Setting = super.setVisible(suppliner) as SettingArray<T>

    fun register() : SettingArray<T> = this.also { parentMod.register(it) }
}