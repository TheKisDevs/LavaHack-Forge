package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.Gradients
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.customfont.CustomFontUtil

/**
 * @author _kisman_
 * @since 16:40 of 07.11.2022
 */
@Suppress("PrivatePropertyName")
class ColorPattern(
    module : Module
) : AbstractPattern<ColorPattern>(
    module
) {
    private val group_ = setupGroup(SettingGroup(Setting("Color", module)))
    private val gradient = setupList(group_.add(SettingGroup(Setting("Gradient", module)).add(SettingsList("mode", Setting("Gradient Mode", module, Gradients.None).setTitle("Mode"), "offset", Setting("Gradient YBased Offset", module, false).setTitle("Y-Based Offset"), "diff", Setting("Gradient Diff", module, 0.0, 0.0, 360.0, NumberType.TIME).setTitle("Diff")))))
    private val astolfo = setupSetting(group_.add(Setting("Astolfo", module, false)))
    private val color = setupSetting(group_.add(Setting("Color", module, Colour(-1))))
    private val pulsive = setupList(group_.add(SettingGroup(Setting("Pulsive", module)).add(SettingsList("color1", Setting("Pulsive Color 1", module, Colour(255, 0, 0, 255)).setTitle("First"), "color2", Setting("Pulsive Color 2", module, Colour(0, 0, 255, 255)).setTitle("Second")))))

    override fun preInit() : ColorPattern {
        if(group != null) {
            group!!.add(group_)
        }

        return this
    }

    override fun init() : ColorPattern {
        module.register(group_)
        module.register(gradient)
        module.register(astolfo)
        module.register(color)

        return this
    }

    fun color() = color(null, null)

    fun color(
        offset : Int
    ) = color(offset, null)

    fun color(
        offset : Int?,
        y : Int?
    ) = when(gradient["mode"].valEnum) {
        Gradients.None -> {
            if(astolfo.valBoolean) {
                Colour(ColorUtils.astolfoColors(100, 100), color.colour.alpha)
            } else {
                color.colour!!
            }
        }

        else -> {
            Colour((gradient["mode"].valEnum as Gradients).get(
                if(gradient["offset"].valBoolean && y != null) {
                    y / CustomFontUtil.getFontHeight()
                } else {
                    offset ?: 0
                } * gradient["diff"].valInt,
                color.colour.saturation,
                color.colour.brightness,
                null,
//                gradient["speed"].valFloat,
                pulsive["color1"].colour,
                pulsive["color2"].colour
            ), color.colour.alpha)
        }
    }
}