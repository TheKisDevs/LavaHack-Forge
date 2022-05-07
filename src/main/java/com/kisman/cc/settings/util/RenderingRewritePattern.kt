package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.RainbowUtil
import com.kisman.cc.util.Rendering
import com.kisman.cc.util.enums.RenderingRewriteModes
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

class RenderingRewritePattern(
    val module : Module,
    val visible : Supplier<Boolean>,
    val prefix : String?
) {
    constructor(module : Module, visible : Supplier<Boolean>) : this(module, visible, null)

    val mode = Setting((if(prefix != null) "$prefix " else "") + "Render Mode", module, RenderingRewriteModes.Filled).setVisible { visible.get() }
    val lineWidth = Setting((if(prefix != null) "$prefix " else "") + "Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible {
        visible.get() && mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient
    }

    val rainbow = Setting("Rainbow", module, false)

    //Colors
    val color1 = Setting((if(prefix != null) "$prefix " else "") + "Render Color", module, (if(prefix != null) "$prefix " else "") + "Render Color", Colour(255, 0, 0, 255)).setVisible { visible.get() }
    val color2 = Setting((if(prefix != null) "$prefix " else "") + "Render Second Color", module, (if(prefix != null) "$prefix " else "") + "Render Second Color", Colour(0, 120, 255, 255)).setVisible {
        visible.get() && (
                mode.valEnum == RenderingRewriteModes.FilledGradient ||
                        mode.valEnum == RenderingRewriteModes.OutlineGradient ||
                        mode.valEnum == RenderingRewriteModes.BothGradient ||
                        mode.valEnum == RenderingRewriteModes.GlowOutline ||
                        mode.valEnum == RenderingRewriteModes.Glow
                )
    }

    fun init() {
        Kisman.instance.settingsManager.rSetting(mode)
        Kisman.instance.settingsManager.rSetting(lineWidth)
        Kisman.instance.settingsManager.rSetting(rainbow)
        Kisman.instance.settingsManager.rSetting(color1)
        Kisman.instance.settingsManager.rSetting(color2)
    }

    fun draw(aabb : AxisAlignedBB) {
        Rendering.draw(
            Rendering.correct(aabb),
            lineWidth.valFloat,
            getColor1(),
            getColor2(),
            (mode.valEnum as RenderingRewriteModes).mode
        )
    }

    fun draw(pos : BlockPos) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            )
        )
    }

    private fun getColor1() : Colour {
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(0, 100, 50, color1.colour.a, 1.0)
        } else {
            color1.colour
        }
    }

    private fun getColor2() : Colour {
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(50, 100, 50, color2.colour.a, 1.0)
        } else {
            color2.colour
        }
    }
}