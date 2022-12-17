package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.enums.dynamic.EasingEnum.Easing
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 23:01 of 11.10.2022
 */
class SlideRenderingRewritePattern(
    module : Module
) : RenderingRewritePattern(
    module
) {
    private val slideGroup = setupGroup(SettingGroup(Setting("Slide", module)))

    private val lengthsGroup = setupGroup(slideGroup.add(SettingGroup(Setting("Lengths", module))))

    @JvmField val movingLength = setupSetting(lengthsGroup.add(Setting("Moving Length", module, 400.0, 0.0, 1000.0, NumberType.TIME).setTitle("Moving")))
    @JvmField val fadeLength = setupSetting(lengthsGroup.add(Setting("Fade Length", module, 200.0, 0.0, 1000.0, NumberType.TIME).setTitle("Fade")))
    @JvmField val alphaFadeLength = setupSetting(lengthsGroup.add(Setting("Alpha Fade Length", module, 0.0, 0.0, 1000.0, NumberType.TIME).setTitle("Alpha Fade")))

    private val easingGroup = setupGroup(slideGroup.add(SettingGroup(Setting("Easing", module))))

    @JvmField val movingOutEasing = setupArray(easingGroup.add(SettingArray("Moving Out Easing", module, Easing.Linear, EasingEnum.outEasings).setTitle("Moving Out")))
    @JvmField val fadeInEasing = setupArray(easingGroup.add(SettingArray("Fade In Easing", module, Easing.Linear, EasingEnum.inEasings).setTitle("Fade In")))

    @JvmField val fadeOutEasing = setupArray(easingGroup.add(SettingArray("Fade Out Easing", module, Easing.Linear, EasingEnum.outEasings).setTitle("Fade Out")))
    //    @JvmField val movingInEasing = setupArray(easingGroup.add(SettingArray("Moving In Easing", module, Easing.Linear, EasingEnum.inEasings).setTitle("Moving In")))
    @JvmField val alphaFadeEasing = SettingEnum("Alpha Fade Easing", module, EasingEnum.EasingReverse.Linear).setTitle("Alpha Fade").also {
        easingGroup.add(it)
        setupEnum(it)
    }


    override fun preInit() : SlideRenderingRewritePattern {
        super.preInit()

        if(group != null) {
            group?.add(slideGroup)
        }

        return this
    }

    override fun init() : SlideRenderingRewritePattern {
        super.init()

        module.register(slideGroup)
        module.register(lengthsGroup)
        module.register(movingLength)
        module.register(fadeLength)
        module.register(alphaFadeLength)
        module.register(easingGroup)
//        module.register(movingInEasing)
        module.register(movingOutEasing)
        module.register(fadeInEasing)
        module.register(fadeOutEasing)
        module.register(alphaFadeEasing)


        return this
    }

    override fun prefix(
        prefix : String
    ) : SlideRenderingRewritePattern {
        return super.prefix(prefix) as SlideRenderingRewritePattern
    }

    override fun visible(
        visible : Supplier<Boolean>
    ) : SlideRenderingRewritePattern {
        return super.visible(visible) as SlideRenderingRewritePattern
    }

    override fun group(
        group : SettingGroup
    ) : SlideRenderingRewritePattern {
        return super.group(group) as SlideRenderingRewritePattern
    }
}