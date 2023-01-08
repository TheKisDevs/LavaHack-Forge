package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.client.Config
import com.kisman.cc.features.module.render.ShaderCharms
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.RainbowUtil
import com.kisman.cc.util.enums.RenderingRewriteModes
import com.kisman.cc.util.interfaces.Drawable
import com.kisman.cc.util.render.Rendering
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

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
    val shader = setupSetting(Setting("Shader", module, false))

    val rainbowGroup = setupGroup(SettingGroup(Setting("Rainbow", module)))
    val rainbow = setupSetting(rainbowGroup.add(Setting("Rainbow", module, false)))
    val rainbowSpeed = setupSetting(rainbowGroup.add(Setting("Rainbow Speed", module, 1.0, 0.25, 5.0, false).setVisible(rainbow).setTitle("Speed")))
    val rainbowSat = setupSetting(rainbowGroup.add(Setting("Saturation", module, 100.0, 0.0, 100.0, true).setVisible(rainbow).setTitle("Sat")))
    val rainbowBright = setupSetting(rainbowGroup.add(Setting("Brightness", module, 100.0, 0.0, 100.0, true).setVisible(rainbow).setTitle("Bright")))

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
            if(module is Drawable) {
                group!!.add(shader)
            }
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
        if(module is Drawable) {
            module.register(shader)
        }
        module.register(rainbowGroup)
        module.register(rainbow)
        module.register(rainbowSpeed)
        module.register(rainbowSat)
        module.register(rainbowBright)
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

        if(module is Drawable) {
            module.renderPatterns.add(this)

            ShaderCharms.modules[module] = Supplier {
                fun processPattern(
                    pattern : RenderingRewritePattern
                ) : Boolean = pattern.isActive() && !pattern.canRender()

                fun processPatterns() : Boolean {
                    var flag = false

                    for(pattern in module.renderPatterns) {
                        if(processPattern(pattern)) {
                            flag = true
                            continue
                        }
                    }

                    return flag
                }

                module.toggled && processPatterns()
            }
        }

        return this
    }

    open fun isActive() : Boolean = mode.valEnum != RenderingRewriteModes.None

    open fun canRender() : Boolean = !shader.valBoolean

    open fun canRender(
        callingFromDraw : Boolean
    ) : Boolean = (callingFromDraw && !canRender()) || (!callingFromDraw && canRender())

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

        draw0(
            Rendering.correct(a),
            filledColor1,
            filledColor2,
            outlineColor1,
            outlineColor2,
            wireColor1,
            wireColor2,
            mode
        )
    }

    private fun modifyBB(
        aabb : AxisAlignedBB
    ) : AxisAlignedBB = if(scaleState.valBoolean) {
        aabb.grow(scaleOffset.valDouble)
    } else {
        aabb
    }

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
        //TODO: remove it and cleanup!!!
        if(Config.instance.test2.valBoolean) {
            Rendering.start1(depth.valBoolean)
        } else {
            Rendering.setup(depth.valBoolean)
        }

        Rendering.draw0(modifyBB(aabb), lineWidth.valFloat, filledColor1, filledColor2, outlineColor1, outlineColor2, wireColor1, wireColor2, mode, depth.valBoolean)

        if(Config.instance.test2.valBoolean) {
            Rendering.end1(depth.valBoolean)
        } else {
            Rendering.release(depth.valBoolean)
        }
    }

    private fun draw0(
        aabb : AxisAlignedBB,
        color1 : Colour,
        color2 : Colour,
        mode : Rendering.Mode?
    ) {
        Rendering.draw0(modifyBB(aabb), lineWidth.valFloat, color1, color2, mode, depth.valBoolean)
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
        filledColor1 : Colour,
        filledColor2 : Colour,
        outlineColor1 : Colour,
        outlineColor2 : Colour,
        wireColor1 : Colour,
        wireColor2 : Colour,
        alphaCoeff : Double
    ) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            ),
            filledColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            wireColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            wireColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            (mode.valEnum as RenderingRewriteModes).mode
        )
    }

    open fun draw(
        bb : AxisAlignedBB,
        filledColor1 : Colour,
        filledColor2 : Colour,
        outlineColor1 : Colour,
        outlineColor2 : Colour,
        wireColor1 : Colour,
        wireColor2 : Colour,
        alphaCoeff : Double
    ) {
        draw(
            bb,
            filledColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            wireColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            wireColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            (mode.valEnum as RenderingRewriteModes).mode
        )
    }

    open fun draw(
        pos : BlockPos,
        alphaCoeff : Double
    ) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            ),
            getFilledColor1().withAlpha((alphaCoeff * 255).toInt().coerceIn(0..255)),
            getFilledColor2().withAlpha((alphaCoeff * 255).toInt().coerceIn(0..255)),
            getOutlineColor1().withAlpha((alphaCoeff * 255).toInt().coerceIn(0..255)),
            getOutlineColor2().withAlpha((alphaCoeff * 255).toInt().coerceIn(0..255)),
            getWireColor1().withAlpha((alphaCoeff * 255).toInt().coerceIn(0..255)),
            getWireColor2().withAlpha((alphaCoeff * 255).toInt().coerceIn(0..255)),
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

    open fun draw(
        aabb : AxisAlignedBB
    ) {
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

    open fun draw(
        pos : BlockPos
    ) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            )
        )
    }

    fun getFilledColor1() : Colour = if(rainbow.valBoolean) {
        RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, filledColor1.colour.a, rainbowSpeed.valDouble)
    } else {
        filledColor1.colour
    }


    fun getFilledColor2() : Colour = if(rainbow.valBoolean) {
        RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, filledColor2.colour.a, rainbowSpeed.valDouble)
    } else {
        filledColor2.colour
    }


    fun getOutlineColor1() : Colour = if(rainbow.valBoolean) {
        RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, outlineColor1.colour.a, rainbowSpeed.valDouble)
    } else {
        outlineColor1.colour
    }

    fun getOutlineColor2() : Colour = if(rainbow.valBoolean) {
        RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, outlineColor2.colour.a, rainbowSpeed.valDouble)
    } else {
        outlineColor2.colour
    }

    fun getWireColor1() : Colour = if(rainbow.valBoolean) {
        RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, wireColor1.colour.a, rainbowSpeed.valDouble)
    } else {
        wireColor1.colour
    }

    fun getWireColor2() : Colour = if(rainbow.valBoolean) {
        RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, wireColor2.colour.a, rainbowSpeed.valDouble)
    } else {
        wireColor2.colour
    }
}