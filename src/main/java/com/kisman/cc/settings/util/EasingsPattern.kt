package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.enums.EasingMode
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.render.Rendering
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 11:47 of 04.08.2022
 */
@Suppress("MemberVisibilityCanBePrivate", "PrivatePropertyName")
class EasingsPattern(
    module : Module
) : AbstractPattern<EasingsPattern>(
    module
) {
    private val group_ = setupGroup(SettingGroup(Setting("Easing", module)))

    private val mode = group_.add(Setting("Easing Mode", module, EasingMode.Normal))

    private val normalMode = SettingEnum("Easing Normal Mode", module, EasingEnum.Easing.Linear).setTitle("Normal").also {
        setupEnum(it)
        group_.add(it)
    }

    private val reverseMode = SettingEnum("Easing Reverse Mode", module, EasingEnum.EasingReverse.Linear).setTitle("Reverse").also {
        setupEnum(it)
        group_.add(it)
    }

    override fun preInit() : EasingsPattern {
        if(group != null) {
            group?.add(group_)
        }

        return this
    }

    override fun init() : EasingsPattern {
        module.register(group_)
        module.register(mode)
        module.register(normalMode)
        module.register(reverseMode)

        return this
    }

    fun mutateProgress(progress : Double) : Double = if(mode.valEnum == EasingMode.Normal) normalMode.valEnum.task.doTask(progress) else reverseMode.valEnum.task.doTask(progress)
    fun mutateProgress(progress : Float) : Float = mutateProgress(progress.toDouble()).toFloat()
    fun mutateBB(bb : AxisAlignedBB, progress : Double) : AxisAlignedBB = Rendering.scale(bb, mutateProgress(progress))
    fun mutateBlockBB(pos : BlockPos, progress : Double) : AxisAlignedBB = Rendering.scale(pos, mutateProgress(progress))
}