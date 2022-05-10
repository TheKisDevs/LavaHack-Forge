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
    val abyss = Setting("Abyss", module, false)
    val lineWidth = Setting((if(prefix != null) "$prefix " else "") + "Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible {
        visible.get() && mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient
    }

    val rainbow = Setting("Rainbow", module, false)
    val rainbowSpeed = Setting("RainbowSpeed", module, 1.0, 0.25, 5.0, false)
    val rainbowSat = Setting("Saturation", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean}
    val rainbowBright = Setting("Brightness", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean}
    val rainbowGlow = Setting("Glow", module, "None", listOf("None", "Glow", "ReverseGlow")).setVisible{rainbow.valBoolean}

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
        Kisman.instance.settingsManager.rSetting(abyss)
        Kisman.instance.settingsManager.rSetting(lineWidth)
        Kisman.instance.settingsManager.rSetting(rainbow)
        Kisman.instance.settingsManager.rSetting(rainbowSpeed)
        Kisman.instance.settingsManager.rSetting(rainbowSat)
        Kisman.instance.settingsManager.rSetting(rainbowBright)
        Kisman.instance.settingsManager.rSetting(rainbowGlow)
        Kisman.instance.settingsManager.rSetting(color1)
        Kisman.instance.settingsManager.rSetting(color2)
    }

    fun draw(aabb : AxisAlignedBB) {
        var a = aabb
        if(abyss.valBoolean){
            a = AxisAlignedBB(a.minX, a.minY + 1.0, a.minZ, a.maxX, a.maxY + 0.075, a.maxZ)
        }
        if(!rainbowGlow.valString.equals("None")){
            val cAabb = Rendering.correct(a);
            val colour1 = getColor1()
            val colour2 = getColor2()
            var outAlpha1 = 255
            var outAlpha2 = 255
            val reverse = rainbowGlow.valString.equals("ReverseGlow")
            if(reverse){
                outAlpha1 = 0;
            } else {
                outAlpha2 = 0;
            }
            Rendering.draw(cAabb, lineWidth.valFloat, colour1, colour2, Rendering.Mode.GRADIENT)
            Rendering.draw(cAabb, lineWidth.valFloat, colour1.withAlpha(outAlpha1), colour2.withAlpha(outAlpha2), Rendering.Mode.CUSTOM_OUTLINE)
            return
        }
        Rendering.draw(
            Rendering.correct(a),
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
        val glow = rainbowGlow.valString;
        var alpha = color1.colour.a;
        if(glow.equals("ReverseGlow")){
            alpha = 0
        }
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, alpha, rainbowSpeed.valDouble)
        } else {
            color1.colour
        }
    }

    private fun getColor2() : Colour {
        val glow = rainbowGlow.valString;
        var alpha = color2.colour.a
        if(glow.equals("Glow")){
            alpha = 0
        }
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, alpha, rainbowSpeed.valDouble)
        } else {
            color2.colour
        }
    }
}