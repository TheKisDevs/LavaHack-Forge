package com.kisman.cc.features.hud

import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.settings.util.ColorPattern
import com.kisman.cc.util.client.AnimateableFeature
import com.kisman.cc.util.client.collections.Sorter
import com.kisman.cc.util.enums.Orientations
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.fix
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.customfont.CustomFontUtil

/**
 * @author _kisman_
 * @since 23:14 of 26.03.2023
 */
@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA", "PropertyName", "LeakingThis", "unused")
abstract class AverageMultiLineHudModule(
    name : String,
    desc : String
) : ShaderableHudModule(
    name,
    desc,
    true,
    true,
    false
) {
    private val sorter = Sorter<MultiLineElement> { it.name }

    private val color = ColorPattern(this).prefix("Text").preInit().init()
    private val offsetX = register(Setting("Offset X", this, 2.0, 0.0, 5.0, true))
    private val offsetY = register(Setting("Offset Y", this, 2.0, 0.0, 5.0, true))
    private val sort = register(SettingArray("Sort", this, sorter.length(), sorter.array()))
    private val reverse = register(Setting("Reverse", this, false))
    private val animationGroup = register(SettingGroup(Setting("Animation", this)))
    private val animation = register(animationGroup.add(Setting("Animation", this, true)))
    private val easing = register(animationGroup.add(SettingEnum("Easing", this, EasingEnum.Easing.Linear).onChange0 { EASING = it.valEnum }))
    private val length = register(animationGroup.add(Setting("Length", this, 750.0, 0.0, 1000.0, NumberType.TIME).onChange { LENGTH = it.valLong }))
    private val background = register(register(SettingGroup(Setting("Background", this))).add(SettingsList("state", Setting("Background", this, false), "color", ColorPattern(this).prefix("Background"), "shadered", Setting("Shadered Background", this, false).setTitle("Shadered"))))
    private val orientation = register(SettingEnum("Orientation", this, Orientations.Right))

    var LENGTH = 0L
    var EASING = EasingEnum.Easing.Linear

    init {
        placeSetting.onChange0 {
            it.valEnum.move(this)

            if(it.valEnum.reverse != null) {
                reverse.valBoolean = it.valEnum.reverse!!
                orientation.valEnum = if (it.valEnum.right!!) {
                    Orientations.Right
                } else {
                    Orientations.Left
                }
            }
        }
    }

    abstract fun elements(
        elements : ArrayList<MultiLineElement>
    )

    override fun draw() {
        val elements = ArrayList<MultiLineElement>()
        val offsetX = offsetX.valDouble
        val offsetY = offsetY.valDouble
        val height = CustomFontUtil.getFontHeight() + offsetY * 2
        var maxLength = -1.0
        var count0 = 0

        elements(elements)
        elements.sortWith(sort.valElement.comparator)

        if(reverse.valBoolean) {
            elements.reverse()
        }

        for(element in elements) {
            var coeff = if(element.active()) {
                1.0
            } else {
                0.0
            }

            if(element.active()) {
                if(animation.valBoolean) {
                    coeff = element.element.ENABLE_ANIMATION().current
                }

                element.element.ENABLE_ANIMATION().update()
                element.element.DISABLE_ANIMATION().reset()
            } else {
                if(animation.valBoolean) {
                    coeff = element.element.DISABLE_ANIMATION().current
                }

                element.element.ENABLE_ANIMATION().reset()
                element.element.DISABLE_ANIMATION().update()
            }

            if(coeff != 0.0) {
                fun x(
                    string : Boolean = false
                ) = getX().toInt() + when(orientation.valEnum) {
                    Orientations.Left -> if(string) {
                        -(CustomFontUtil.getStringWidth(element.name) * (1 - coeff))
                    } else {
                        0.0
                    }

                    Orientations.Right -> {
                        getW() - CustomFontUtil.getStringWidth(element.name) * if(string) {
                            coeff
                        } else {
                            1.0
                        } - offsetX * 2
                    }
                }

                val count1 = count0

                if(background["state"].valBoolean) {
                    val runnable = Runnable {
                        Render2DUtil.drawRectWH(
                            x(),
                            getY() + height * count1,
                            CustomFontUtil.getStringWidth(element.name) + offsetX * 2,
                            CustomFontUtil.getFontHeight() + offsetY * 2,
                            background.pattern<ColorPattern>("color").color(count1, (getY() + height * count1).toInt()).rgb
                        )
                    }

                    if(background["shadered"].valBoolean) {
                        addShader(runnable)
                    } else {
                        addPreNormal(runnable)
                    }
                }

                addShader(Runnable {
                    drawStringWithShadow(
                        element.name,
                        x(true) + offsetX,
                        getY() + height * count1 + offsetY - 1,
                        color.color(count1, (getY() + height * count1).toInt()).rgb
                    )
                })

                val length = CustomFontUtil.getStringWidth(element.name)

                if(length > maxLength) {
                    maxLength = length.toDouble()
                }

                count0++
            }
        }

        setW(maxLength + offsetX * 2)
        setH(height * count0.toDouble())
        place.refresh()
        fix(this)
    }
}

class MultiLineElement(
    val element : AnimateableFeature,
    val name : String,
    val active : () -> (Boolean)
)