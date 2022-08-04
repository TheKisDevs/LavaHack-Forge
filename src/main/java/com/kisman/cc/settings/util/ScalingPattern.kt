package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.enums.dynamic.ScalingEnum
import com.kisman.cc.util.render.Rendering
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 11:47 of 04.08.2022
 */
@Suppress("MemberVisibilityCanBePrivate", "PrivatePropertyName")
class ScalingPattern(
    module : Module
) : AbstractPattern<ScalingPattern>(
    module
) {
    private val group_ = setupGroup(SettingGroup(Setting("Scaling", module)))

    private val mode = SettingEnum("Smooth Scaling Mode", module, ScalingEnum.Scaling.Linear).setTitle("Mode").also {
        setupEnum(it)
        group_.add(it)
    }

    override fun preInit(): ScalingPattern {
        if(group != null) {
            group?.add(group_)
        }

        return this
    }

    override fun init(): ScalingPattern {
        module.register(group_)
        module.register(mode)

        return this
    }

    fun mutateProgress(progress : Double) : Double {
        return mode.valEnum.task.doTask(progress)
    }

    fun mutateBB(bb : AxisAlignedBB, progress : Double) : AxisAlignedBB {
        return Rendering.scale(bb, mutateProgress(progress))
    }

    fun mutateBlockBB(pos : BlockPos, progress : Double) : AxisAlignedBB {
        return Rendering.scale(pos, mutateProgress(progress))
    }
}