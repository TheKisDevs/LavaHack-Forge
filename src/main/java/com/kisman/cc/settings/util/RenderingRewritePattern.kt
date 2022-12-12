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
import kotlin.math.roundToInt

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class RenderingRewritePattern(
    module : Module
) : AbstractPattern<RenderingRewritePattern>(
    module
) {
    val mode = setupSetting(Setting("Render Mode", module, RenderingRewriteModes.None).setTitle("Mode"))
    val abyss = setupSetting(Setting("Abyss", module, false))
    val lineWidth = setupSetting(Setting("Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible { mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient }.setTitle("Width"))
    val scaleGroup = setupGroup(SettingGroup(Setting("Scale", module)))
    val scaleState = setupSetting(scaleGroup.add(Setting("Scale State", module, false).setTitle("State")))
    val scaleOffset = setupSetting(scaleGroup.add(Setting("Scale Offset", module, 0.002, 0.002, 0.2, false)))
    val depth = setupSetting(Setting("Depth", module, false))

    val rainbowGroup = setupGroup(SettingGroup(Setting("Rainbow", module)))
    val rainbow = setupSetting(rainbowGroup.add(Setting("Rainbow", module, false)))
    val rainbowSpeed = setupSetting(rainbowGroup.add(Setting("Rainbow Speed", module, 1.0, 0.25, 5.0, false).setVisible(rainbow).setTitle("Speed")))
    val rainbowSat = setupSetting(rainbowGroup.add(Setting("Saturation", module, 100.0, 0.0, 100.0, true).setVisible(rainbow).setTitle("Sat")))
    val rainbowBright = setupSetting(rainbowGroup.add(Setting("Brightness", module, 100.0, 0.0, 100.0, true).setVisible(rainbow).setTitle("Bright")))
//    val rainbowGlow = setupSetting(rainbowGroup.add(Setting("Glow", module, "None", listOf("None", "Glow", "ReverseGlow")).setVisible(rainbow).setTitle("Glow")))

    //Colors
    val colorGroup = setupGroup(SettingGroup(Setting("Colors", module)))
    val filledColorGroup = setupGroup(colorGroup.add(SettingGroup(Setting("Filled", module))))
    val filledColor1 = setupSetting(filledColorGroup.add(Setting("Render Color", module, "First", Colour(255, 0, 0, 255))))
    val filledColor2 = setupSetting(filledColorGroup.add(Setting("Render Second Color", module, "Second", Colour(0, 120, 255, 255))))
    val outlineColorGroup = setupGroup(colorGroup.add(SettingGroup(Setting("Outline", module))))
    val outlineColor1 = setupSetting(outlineColorGroup.add(Setting("Render Outline Color", module, "First", Colour(255, 0, 0, 255))))
    val outlineColor2 = setupSetting(outlineColorGroup.add(Setting("Render Outline Second Color", module, "Second", Colour(0, 120, 255, 255))))
    val wireColorGroup = setupGroup(colorGroup.add(SettingGroup(Setting("Wire", module))))
    val wireColor1 = setupSetting(wireColorGroup.add(Setting("Render Wire Color", module, "First", Colour(255, 0, 0, 255))))
    val wireColor2 = setupSetting(wireColorGroup.add(Setting("Render Wire Second Color", module, "Second", Colour(0, 120, 255, 255))))

    override fun preInit() : RenderingRewritePattern {
        if(group != null) {
            group!!.add(mode)
            group!!.add(abyss)
            group!!.add(lineWidth)
            group!!.add(scaleGroup)
            group!!.add(depth)
            group!!.add(rainbowGroup)
            group!!.add(colorGroup)
        }

        return this
    }

    override fun init() : RenderingRewritePattern {
        module.register(mode)
        module.register(abyss)
        module.register(lineWidth)
        module.register(scaleGroup)
        module.register(scaleState)
        module.register(scaleOffset)
        module.register(depth)
        module.register(rainbowGroup)
        module.register(rainbow)
        module.register(rainbowSpeed)
        module.register(rainbowSat)
        module.register(rainbowBright)
//        module.register(rainbowGlow)
        module.register(colorGroup)
        module.register(filledColorGroup)
        module.register(filledColor1)
        module.register(filledColor2)
        module.register(outlineColorGroup)
        module.register(outlineColor1)
        module.register(outlineColor2)
        module.register(wireColorGroup)
        module.register(wireColor1)
        module.register(wireColor2)

        return this
    }

    open fun isActive() : Boolean = mode.valEnum != RenderingRewriteModes.None

    open fun draw(
        aabb : AxisAlignedBB,
        color1 : Colour,
        color2 : Colour,
        mode : Rendering.Mode?
    ) {
        draw(
            aabb,
            color1,
            color2,
            color1,
            color2,
            color1,
            color2,
            mode
        )
    }

    open fun draw(
        aabb : AxisAlignedBB,
        filledColor1 : Colour,
        filledColor2 : Colour,
        outlineColor1 : Colour,
        outlineColor2 : Colour,
        wireColor1 : Colour,
        wireColor2 : Colour,
        mode : Rendering.Mode?
    ) {
        if(!isActive() || mode == null) {
            return
        }

        var a = aabb
        if(abyss.valBoolean){
            a = AxisAlignedBB(a.minX, a.minY + 1.0, a.minZ, a.maxX, a.maxY + 0.075, a.maxZ)
        }
        /*if(!rainbowGlow.valString.equals("None")){
            val cAabb = Rendering.correct(a)
            var colour1 = getColor1()
            colour1 = colour1.withAlpha((colour1.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0))
            var colour2 = getColor2()
            colour2 = colour2.withAlpha((colour2.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0))
            var outAlpha1 = 255
            var outAlpha2 = 255
            val reverse = rainbowGlow.valString.equals("ReverseGlow")
            if(reverse){
                outAlpha1 = 0
            } else {
                outAlpha2 = 0
            }
            outAlpha1 = (outAlpha1 - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)
            outAlpha2 = (outAlpha2 - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)
            draw0(cAabb, colour1, colour2, Rendering.Mode.BOX_GRADIENT)
            draw0(cAabb, colour1.withAlpha(outAlpha1), colour2.withAlpha(outAlpha2), Rendering.Mode.CUSTOM_OUTLINE)
            return
        }*/
        draw0(
            Rendering.correct(a),
            getFilledColor1(),
            getFilledColor2(),
            getOutlineColor1(),
            getOutlineColor2(),
            getWireColor1(),
            getWireColor2(),
//            filledColor1.withAlpha((filledColor1.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)),
//            filledColor2.withAlpha((filledColor2.alpha - ((alphaSubtract * 255.0).roundToInt())).coerceAtLeast(0)),
            mode
        )
    }

    private fun modifyBB(
        aabb : AxisAlignedBB
    ) : AxisAlignedBB = aabb.also { if(scaleState.valBoolean) it.grow(scaleOffset.valDouble) }

    private fun draw0(
        aabb : AxisAlignedBB,
        filledColor1 : Colour,
        filledColor2 : Colour,
        outlineColor1 : Colour,
        outlineColor2 : Colour,
        wireColor1 : Colour,
        wireColor2 : Colour,
        mode : Rendering.Mode?
    ) {
        Rendering.setup(depth.valBoolean)
        Rendering.draw0(modifyBB(aabb), lineWidth.valFloat, filledColor1, filledColor2, outlineColor1, outlineColor2, wireColor1, wireColor2, mode)
        Rendering.release(depth.valBoolean)
    }

    private fun draw0(
        aabb : AxisAlignedBB,
        color1 : Colour,
        color2 : Colour,
        mode : Rendering.Mode?
    ) {
        Rendering.setup(depth.valBoolean)
        Rendering.draw0(modifyBB(aabb), lineWidth.valFloat, color1, color2, color1, color2, color1, color2, mode)
        Rendering.release(depth.valBoolean)
    }

    open fun draw(
        aabb : AxisAlignedBB,
        color1: Colour,
        color2 : Colour,
        mode : RenderingRewriteModes
    ) {
        draw(
            aabb,
            color1,
            color2,
            mode.mode
        )
    }

    open fun draw(
        aabb : AxisAlignedBB,
        color1: Colour,
        color2 : Colour
    ) {
        draw(
            aabb,
            color1,
            color2,
            (mode.valEnum as RenderingRewriteModes).mode
        )
    }

    open fun draw(
        pos : BlockPos,
        color1 : Colour,
        color2 : Colour
    ) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            ),
            color1,
            color2
        )
    }

    open fun draw(aabb : AxisAlignedBB) {
        draw(
            aabb,
            getFilledColor1(),
            getFilledColor2(),
            getOutlineColor1(),
            getOutlineColor2(),
            getWireColor1(),
            getWireColor2(),
            (mode.valEnum as RenderingRewriteModes).mode
        )
    }

    open fun draw(pos : BlockPos) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            )
        )
    }

    private fun getFilledColor1() : Colour {
//        val glow = rainbowGlow.valString
        /*var alpha = filledColor1.colour.a
        if(glow.equals("ReverseGlow")){
            alpha = 0
        }*/
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, filledColor1.colour.a, rainbowSpeed.valDouble)
        } else {
            filledColor1.colour
        }
    }

    private fun getFilledColor2() : Colour {
        /*val glow = rainbowGlow.valString
        var alpha = filledColor2.colour.a
        if(glow.equals("Glow")){
            alpha = 0
        }*/
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, filledColor2.colour.a, rainbowSpeed.valDouble)
        } else {
            filledColor2.colour
        }
    }

    private fun getOutlineColor1() : Colour {
//        val glow = rainbowGlow.valString
        /*var alpha = filledColor1.colour.a
        if(glow.equals("ReverseGlow")){
            alpha = 0
        }*/
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, outlineColor1.colour.a, rainbowSpeed.valDouble)
        } else {
            outlineColor1.colour
        }
    }

    private fun getOutlineColor2() : Colour {
        /*val glow = rainbowGlow.valString
        var alpha = filledColor2.colour.a
        if(glow.equals("Glow")){
            alpha = 0
        }*/
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, outlineColor2.colour.a, rainbowSpeed.valDouble)
        } else {
            outlineColor2.colour
        }
    }

    private fun getWireColor1() : Colour {
//        val glow = rainbowGlow.valString
        /*var alpha = filledColor1.colour.a
        if(glow.equals("ReverseGlow")){
            alpha = 0
        }*/
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, wireColor1.colour.a, rainbowSpeed.valDouble)
        } else {
            wireColor1.colour
        }
    }

    private fun getWireColor2() : Colour {
        /*val glow = rainbowGlow.valString
        var alpha = filledColor2.colour.a
        if(glow.equals("Glow")){
            alpha = 0
        }*/
        return if(rainbow.valBoolean) {
            RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, wireColor2.colour.a, rainbowSpeed.valDouble)
        } else {
            wireColor2.colour
        }
    }
}