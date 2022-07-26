package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.RainbowUtil
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.enums.RenderingRewriteModes
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

@Suppress("MemberVisibilityCanBePrivate", "unused", "HasPlatformType")
class RenderingRewritePattern(
    val module : Module,
    val visible : Supplier<Boolean> = (Supplier { true }),
    val prefix : String?,
    val group : SettingGroup? = null
) {
    constructor(module : Module, visible : Supplier<Boolean>, prefix : String) : this(module, visible, prefix, null)
    constructor(module : Module, visible : Supplier<Boolean>) : this(module, visible, null, null)

    val mode = Setting((if(prefix != null) "$prefix " else "") + "Render Mode", module, RenderingRewriteModes.None).setVisible { visible.get() }.setTitle("Mode")
    val abyss = Setting("Abyss", module, false)
    val lineWidth = Setting((if(prefix != null) "$prefix " else "") + "Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible {
        visible.get() && mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient
    }.setTitle("Width")

    val rainbowGroup = SettingGroup(Setting("Rainbow", module))
    val rainbow = rainbowGroup.add(Setting((if(prefix != null) "$prefix " else "") + "Rainbow", module, false))
    val rainbowSpeed = rainbowGroup.add(Setting((if(prefix != null) "$prefix " else "") + "Rainbow Speed", module, 1.0, 0.25, 5.0, false).setTitle("Speed"))
    val rainbowSat = rainbowGroup.add(Setting((if(prefix != null) "$prefix " else "") + "Saturation", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean}.setTitle("Sat"))
    val rainbowBright = rainbowGroup.add(Setting((if(prefix != null) "$prefix " else "") + "Brightness", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean}.setTitle("Bright"))
    val rainbowGlow = rainbowGroup.add(Setting((if(prefix != null) "$prefix " else "") + "Glow", module, "None", listOf("None", "Glow", "ReverseGlow")).setVisible{rainbow.valBoolean}.setTitle("Glow"))

    //Colors
    val colorGroup = SettingGroup(Setting("Colors", module))
    val color1 = colorGroup.add(Setting((if(prefix != null) "$prefix " else "") + "Render Color", module, "First Color", Colour(255, 0, 0, 255)).setVisible { visible.get() })
    val color2 = colorGroup.add(Setting((if(prefix != null) "$prefix " else "") + "Render Second Color", module, "Second Color", Colour(0, 120, 255, 255)).setVisible {
        visible.get() && (
                mode.valEnum == RenderingRewriteModes.FilledGradient ||
                        mode.valEnum == RenderingRewriteModes.OutlineGradient ||
                        mode.valEnum == RenderingRewriteModes.BothGradient ||
                        mode.valEnum == RenderingRewriteModes.GlowOutline ||
                        mode.valEnum == RenderingRewriteModes.Glow
                )
    })

    fun preInit() : RenderingRewritePattern {
        if(group == null) {
            return this
        }
        group.add(mode)
        group.add(abyss)
        group.add(lineWidth)
        group.add(rainbowGroup)
        group.add(colorGroup)
        return this
    }

    fun init() : RenderingRewritePattern {
        module.register(mode)
        module.register(abyss)
        module.register(lineWidth)
        module.register(rainbowGroup)
        module.register(rainbow)
        module.register(rainbowSpeed)
        module.register(rainbowSat)
        module.register(rainbowBright)
        module.register(rainbowGlow)
        module.register(colorGroup)
        module.register(color1)
        module.register(color2)

        return this
    }

    fun draw(aabb : AxisAlignedBB) {
        if(mode.valEnum == RenderingRewriteModes.None) {
            return
        }

        var a = aabb
        if(abyss.valBoolean){
            a = AxisAlignedBB(a.minX, a.minY + 1.0, a.minZ, a.maxX, a.maxY + 0.075, a.maxZ)
        }
        if(!rainbowGlow.valString.equals("None")){
            val cAabb = Rendering.correct(a)
            val colour1 = getColor1()
            val colour2 = getColor2()
            var outAlpha1 = 255
            var outAlpha2 = 255
            val reverse = rainbowGlow.valString.equals("ReverseGlow")
            if(reverse){
                outAlpha1 = 0
            } else {
                outAlpha2 = 0
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
        val glow = rainbowGlow.valString
        var alpha = color1.colour.a
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
        val glow = rainbowGlow.valString
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