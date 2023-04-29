package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.enums.AABBProgressModifiers
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
    @JvmField val mutationLength = setupSetting(lengthsGroup.add(Setting("Mutation Length", module, 0.0, 0.0, 1000.0, NumberType.TIME).setTitle("Mutation")))

    private val easingGroup = setupGroup(slideGroup.add(SettingGroup(Setting("Easing", module))))

    @JvmField val movingOutEasing = setupArray(easingGroup.add(SettingArray("Moving Out Easing", module, Easing.Linear, EasingEnum.allEasingsNormal).setTitle("Moving Out")))
    @JvmField val fadeInEasing = setupArray(easingGroup.add(SettingArray("Fade In Easing", module, Easing.Linear, EasingEnum.allEasingsNormal).setTitle("Fade In")))

    @JvmField val fadeOutEasing = setupArray(easingGroup.add(SettingArray("Fade Out Easing", module, Easing.Linear, EasingEnum.allEasingsNormal).setTitle("Fade Out")))
    @JvmField val mutationEasing = setupArray(easingGroup.add(SettingArray("Mutation Easing", module, EasingEnum.EasingReverse.Linear, EasingEnum.allEasingsReverse).setTitle("Mutation")))

    private val mutationGroup = setupGroup(slideGroup.add(SettingGroup(Setting("Mutation", module))))
    @JvmField val alphaFadeMutation = setupSetting(mutationGroup.add(Setting("Mutation Alpha Fade", module, false).setTitle("Alpha")))
    @JvmField val aabbMutation = setupSetting(mutationGroup.add(Setting("AABB Mutation", module, false).setTitle("AABB")))
    @JvmField val aabbMutationLogic = setupEnum(mutationGroup.add(SettingEnum("AABB Mutation Logic", module, AABBProgressModifiers.CentredBox).setTitle("AABB")))


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
        module.register(mutationLength)
        module.register(easingGroup)
        module.register(movingOutEasing)
        module.register(fadeInEasing)
        module.register(fadeOutEasing)
        module.register(mutationEasing)
        module.register(mutationGroup)
        module.register(alphaFadeMutation)
        module.register(aabbMutation)
        module.register(aabbMutationLogic)

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