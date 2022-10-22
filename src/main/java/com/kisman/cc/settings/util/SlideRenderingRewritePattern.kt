package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingArray
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
    private val movingGroup = setupGroup(SettingGroup(Setting("Moving", module)))

    private val lengthsGroup = setupGroup(movingGroup.add(SettingGroup(Setting("Lengths", module))))

    @JvmField val movingLength = setupSetting(lengthsGroup.add(Setting("Moving Length", module, 400.0, 0.0, 1000.0, NumberType.TIME).setTitle("Moving")))
    @JvmField val fadeLength = setupSetting(lengthsGroup.add(Setting("Fade Length", module, 200.0, 0.0, 1000.0, NumberType.TIME).setTitle("Fade")))

    private val easingGroup = setupGroup(movingGroup.add(SettingGroup(Setting("Easing", module))))

//    @JvmField val movingInEasing = setupArray(easingGroup.add(SettingArray("Moving In Easing", module, Easing.Linear, EasingEnum.inEasings).setTitle("Moving In")))
    @JvmField val movingOutEasing = setupArray(easingGroup.add(SettingArray("Moving Out Easing", module, Easing.Linear, EasingEnum.outEasings).setTitle("Moving Out")))

    @JvmField val fadeInEasing = setupArray(easingGroup.add(SettingArray("Fade In Easing", module, Easing.Linear, EasingEnum.inEasings).setTitle("Fade In")))
    @JvmField val fadeOutEasing = setupArray(easingGroup.add(SettingArray("Fade Out Easing", module, Easing.Linear, EasingEnum.outEasings).setTitle("Fade Out")))


    override fun preInit() : SlideRenderingRewritePattern {
        super.preInit()

        if(group != null) {
            group?.add(movingGroup)
        }

        return this
    }

    override fun init() : SlideRenderingRewritePattern {
        super.init()

        module.register(movingGroup)
        module.register(lengthsGroup)
        module.register(movingLength)
        module.register(fadeLength)
        module.register(easingGroup)
//        module.register(movingInEasing)
        module.register(movingOutEasing)
        module.register(fadeInEasing)
        module.register(fadeOutEasing)


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