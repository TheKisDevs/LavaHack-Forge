package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.ColorUtils

/**
 * @author _kisman_
 * @since 16:40 of 07.11.2022
 */
class HudModuleColorPattern(
    module : Module
) : AbstractPattern<HudModuleColorPattern>(module) {
    private val group_ = SettingGroup(Setting("Color", module))
    private val astolfo = group_.add(Setting("Astolfo", module, false))
    private val color = group_.add(Setting("Color", module, Colour(255, 0, 0, 255)))

    override fun preInit() : HudModuleColorPattern {
        if(group != null) {
            group!!.add(group_);
        }

        return this
    }

    override fun init() : HudModuleColorPattern {
        module.register(group_)
        module.register(astolfo)
        module.register(color)

        return this
    }

    fun color() : Colour = if(astolfo.valBoolean) Colour(ColorUtils.astolfoColors(100, 100)) else color.colour
}