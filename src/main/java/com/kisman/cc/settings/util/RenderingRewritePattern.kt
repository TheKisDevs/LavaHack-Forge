package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.render.ShaderCharms
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.client.collections.Pair
import com.kisman.cc.util.enums.RenderingRewriteModes
import com.kisman.cc.util.client.interfaces.Drawable
import com.kisman.cc.util.enums.DirectionVertexes
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.Rendering
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.function.Supplier

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class RenderingRewritePattern(
    module : Module,
    private val canPartial : Boolean
) : AbstractPattern<RenderingRewritePattern>(
    module
) {
    constructor(
        module : Module
    ) : this(
        module,
        false
    )

    val mode = setupSetting(Setting("Render Mode", module, RenderingRewriteModes.None).setTitle("Mode"))
    val abyss = setupSetting(Setting("Abyss", module, false))
    val lineWidth = setupSetting(Setting("Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible { mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient }.setTitle("Width"))
    val scaleGroup = setupGroup(SettingGroup(Setting("Scale", module)))
    val scaleState = setupSetting(scaleGroup.add(Setting("Scale State", module, false).setTitle("State")))
    val scaleOffset = setupSetting(scaleGroup.add(Setting("Scale Offset", module, 0.002, 0.002, 0.2, false)))
    val depth = setupSetting(Setting("Depth", module, false))
    val shaderGroup = setupGroup(SettingGroup(Setting("Shader", module)))
    val shader = setupSetting(shaderGroup.add(Setting("Shader", module, false)))
    val shaderSecondLayer = setupSetting(shaderGroup.add(Setting("Shader Second Layer", module, false).setTitle("Second Layer")))
    val partial = setupSetting(Setting("Partial", module, false))

    val colorGroup = setupGroup(SettingGroup(Setting("Colors", module)))
    val filledColorGroup = setupGroup(colorGroup.add(SettingGroup(Setting("Filled", module))))
    val filledColor1 = setupSetting(filledColorGroup.add(Setting("Render Color", module, "First", Colour(255, 0, 0, 255))))
    val filledColor2 = setupSetting(filledColorGroup.add(Setting("Render Second Color", module, "Second", Colour(0, 120, 255, 255))))
    val filledRainbowGroup = setupGroup(filledColorGroup.add(SettingGroup(Setting("Rainbow", module))))
    val filledColor1RainbowGroup = setupGroup(filledRainbowGroup.add(SettingGroup(Setting("First", module))))
    val filledColor1RainbowState = setupSetting(filledColor1RainbowGroup.add(Setting("Filled Color1 Rainbow State", module, false).setTitle("State")))
    val filledColor1RainbowSpeed = setupSetting(filledColor1RainbowGroup.add(Setting("Filled Color1 Rainbow Speed", module, 1.0, 0.25, 5.0, false).setTitle("Speed")))
    val filledColor1RainbowSat = setupSetting(filledColor1RainbowGroup.add(Setting("Filled Color1 Rainbow Saturation", module, 100.0, 0.0, 100.0, true).setTitle("Sat")))
    val filledColor1RainbowBright = setupSetting(filledColor1RainbowGroup.add(Setting("Filled Color1 Rainbow Brightness", module, 100.0, 0.0, 100.0, true).setTitle("Bright")))
    val filledColor1RainbowOffset = setupSetting(filledColor1RainbowGroup.add(Setting("Filled Color1 Rainbow Offset", module, 0.0, 0.0, 100.0, true).setTitle("Offset")))
    val filledColor2RainbowGroup = setupGroup(filledRainbowGroup.add(SettingGroup(Setting("Second", module))))
    val filledColor2RainbowState = setupSetting(filledColor2RainbowGroup.add(Setting("Filled Color2 Rainbow State", module, false).setTitle("State")))
    val filledColor2RainbowSpeed = setupSetting(filledColor2RainbowGroup.add(Setting("Filled Color2 Rainbow Speed", module, 1.0, 0.25, 5.0, false).setTitle("Speed")))
    val filledColor2RainbowSat = setupSetting(filledColor2RainbowGroup.add(Setting("Filled Color2 Rainbow Saturation", module, 100.0, 0.0, 100.0, true).setTitle("Sat")))
    val filledColor2RainbowBright = setupSetting(filledColor2RainbowGroup.add(Setting("Filled Color2 Rainbow Brightness", module, 100.0, 0.0, 100.0, true).setTitle("Bright")))
    val filledColor2RainbowOffset = setupSetting(filledColor2RainbowGroup.add(Setting("Filled Color2 Rainbow Offset", module, 50.0, 0.0, 100.0, true).setTitle("Offset")))
    val outlineColorGroup = setupGroup(colorGroup.add(SettingGroup(Setting("Outline", module))))
    val outlineColor1 = setupSetting(outlineColorGroup.add(Setting("Render Outline Color", module, "First", Colour(255, 0, 0, 255))))
    val outlineColor2 = setupSetting(outlineColorGroup.add(Setting("Render Outline Second Color", module, "Second", Colour(0, 120, 255, 255))))
    val outlineRainbowGroup = setupGroup(outlineColorGroup.add(SettingGroup(Setting("Rainbow", module))))
    val outlineColor1RainbowGroup = setupGroup(outlineRainbowGroup.add(SettingGroup(Setting("First", module))))
    val outlineColor1RainbowState = setupSetting(outlineColor1RainbowGroup.add(Setting("Outline Color1 Rainbow State", module, false).setTitle("State")))
    val outlineColor1RainbowSpeed = setupSetting(outlineColor1RainbowGroup.add(Setting("Outline Color1 Rainbow Speed", module, 1.0, 0.25, 5.0, false).setTitle("Speed")))
    val outlineColor1RainbowSat = setupSetting(outlineColor1RainbowGroup.add(Setting("Outline Color1 Rainbow Saturation", module, 100.0, 0.0, 100.0, true).setTitle("Sat")))
    val outlineColor1RainbowBright = setupSetting(outlineColor1RainbowGroup.add(Setting("Outline Color1 Rainbow Brightness", module, 100.0, 0.0, 100.0, true).setTitle("Bright")))
    val outlineColor1RainbowOffset = setupSetting(outlineColor1RainbowGroup.add(Setting("Outline Color1 Rainbow Offset", module, 0.0, 0.0, 100.0, true).setTitle("Offset")))
    val outlineColor2RainbowGroup = setupGroup(outlineRainbowGroup.add(SettingGroup(Setting("Second", module))))
    val outlineColor2RainbowState = setupSetting(outlineColor2RainbowGroup.add(Setting("Outline Color2 Rainbow State", module, false).setTitle("State")))
    val outlineColor2RainbowSpeed = setupSetting(outlineColor2RainbowGroup.add(Setting("Outline Color2 Rainbow Speed", module, 1.0, 0.25, 5.0, false).setTitle("Speed")))
    val outlineColor2RainbowSat = setupSetting(outlineColor2RainbowGroup.add(Setting("Outline Color2 Rainbow Saturation", module, 100.0, 0.0, 100.0, true).setTitle("Sat")))
    val outlineColor2RainbowBright = setupSetting(outlineColor2RainbowGroup.add(Setting("Outline Color2 Rainbow Brightness", module, 100.0, 0.0, 100.0, true).setTitle("Bright")))
    val outlineColor2RainbowOffset = setupSetting(outlineColor2RainbowGroup.add(Setting("Outline Color2 Rainbow Offset", module, 50.0, 0.0, 100.0, true).setTitle("Offset")))
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
                group!!.add(shaderGroup)
            }
            if(canPartial) {
                group!!.add(partial)
            }
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
            module.register(shaderGroup)
            module.register(shader)
            module.register(shaderSecondLayer)
        }
        if(canPartial) {
            module.register(partial)
        }
        module.register(colorGroup)
        module.register(filledColorGroup)
        module.register(filledColor1)
        module.register(filledColor2)
        module.register(filledRainbowGroup)
        module.register(filledColor1RainbowGroup)
        module.register(filledColor1RainbowState)
        module.register(filledColor1RainbowSpeed)
        module.register(filledColor1RainbowSat)
        module.register(filledColor1RainbowBright)
        module.register(filledColor1RainbowOffset)
        module.register(filledColor2RainbowGroup)
        module.register(filledColor2RainbowState)
        module.register(filledColor2RainbowSpeed)
        module.register(filledColor2RainbowSat)
        module.register(filledColor2RainbowBright)
        module.register(filledColor2RainbowOffset)
        module.register(outlineColorGroup)
        module.register(outlineColor1)
        module.register(outlineColor2)
        module.register(outlineRainbowGroup)
        module.register(outlineColor1RainbowGroup)
        module.register(outlineColor1RainbowState)
        module.register(outlineColor1RainbowSpeed)
        module.register(outlineColor1RainbowSat)
        module.register(outlineColor1RainbowBright)
        module.register(outlineColor1RainbowOffset)
        module.register(outlineColor2RainbowGroup)
        module.register(outlineColor2RainbowState)
        module.register(outlineColor2RainbowSpeed)
        module.register(outlineColor2RainbowSat)
        module.register(outlineColor2RainbowBright)
        module.register(outlineColor2RainbowOffset)
        module.register(wireColorGroup)
        module.register(wireColor1)
        module.register(wireColor2)

        if(module is Drawable) {
            module.renderPatterns.add(this)

            ShaderCharms.modules[module] = Pair<Supplier<Boolean>>(
                Supplier {
                    fun processPattern(
                        pattern : RenderingRewritePattern
                    ) : Boolean = pattern.isActive() && !pattern.canRender()

                    fun processPatterns() : Boolean {
                        for(pattern in module.renderPatterns) {
                            if(processPattern(pattern)) {
                                return true
                            }
                        }

                        return false
                    }

                    module.toggled && processPatterns()
                },
                Supplier {
                    fun processPattern(
                        pattern : RenderingRewritePattern
                    ) : Boolean = pattern.canRenderSecondLayer()

                    fun processPatterns() : Boolean {
                        for(pattern in module.renderPatterns) {
                            if(processPattern(pattern)) {
                                return true
                            }
                        }

                        return false
                    }

                    processPatterns()
                }
            )
        }

        return this
    }

    open fun isActive() : Boolean = mode.valEnum != RenderingRewriteModes.None

    open fun canRender() : Boolean = !shader.valBoolean

    open fun canRender(
        callingFromDraw : Boolean
    ) : Boolean = (callingFromDraw && !canRender()) || (!callingFromDraw && canRender())

    open fun canRenderSecondLayer() : Boolean = !canRender() && shaderSecondLayer.valBoolean

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
            mode,
            emptyList()
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
        mode : Rendering.Mode?,
        sides : List<DirectionVertexes>
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
            mode,
            sides
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
        mode : Rendering.Mode?,
        sides : List<DirectionVertexes>
    ) {
        Rendering.setup(depth.valBoolean)
        Rendering.draw0(modifyBB(aabb), lineWidth.valFloat, filledColor1, filledColor2, outlineColor1, outlineColor2, wireColor1, wireColor2, mode, depth.valBoolean, canPartial && partial.valBoolean, ArrayList(sides))
        Rendering.release(depth.valBoolean)
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
            (mode.valEnum as RenderingRewriteModes).mode,
            emptyList()
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
            (mode.valEnum as RenderingRewriteModes).mode,
            emptyList()
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
            (mode.valEnum as RenderingRewriteModes).mode,
            emptyList()
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
        aabb : AxisAlignedBB,
        vararg sides : DirectionVertexes
    ) {
        draw(
            aabb,
            getFilledColor1(),
            getFilledColor2(),
            getOutlineColor1(),
            getOutlineColor2(),
            getWireColor1(),
            getWireColor2(),
            (mode.valEnum as RenderingRewriteModes).mode,
            sides.toList()
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

    fun getFilledColor1() : Colour = if(filledColor1RainbowState.valBoolean) {
        ColorUtils.rainbow2(filledColor1RainbowOffset.valInt, filledColor1RainbowSat.valInt, filledColor1RainbowBright.valInt, filledColor1.colour.a, filledColor1RainbowSpeed.valDouble)
    } else {
        filledColor1.colour
    }

    fun getFilledColor2() : Colour = if(filledColor2RainbowState.valBoolean) {
        ColorUtils.rainbow2(filledColor2RainbowOffset.valInt, filledColor2RainbowSat.valInt, filledColor2RainbowBright.valInt, filledColor2.colour.a, filledColor2RainbowSpeed.valDouble)
    } else {
        filledColor2.colour
    }

    fun getOutlineColor1() : Colour = if(outlineColor1RainbowState.valBoolean) {
        ColorUtils.rainbow2(outlineColor1RainbowOffset.valInt, outlineColor1RainbowSat.valInt, outlineColor1RainbowBright.valInt, outlineColor1.colour.a, outlineColor1RainbowSpeed.valDouble)
    } else {
        outlineColor1.colour
    }

    fun getOutlineColor2() : Colour = if(outlineColor2RainbowState.valBoolean) {
        ColorUtils.rainbow2(outlineColor2RainbowOffset.valInt, outlineColor2RainbowSat.valInt, outlineColor2RainbowBright.valInt, outlineColor2.colour.a, outlineColor2RainbowSpeed.valDouble)
    } else {
        outlineColor2.colour
    }

    fun getWireColor1() : Colour = wireColor1.colour

    fun getWireColor2() : Colour = wireColor2.colour
}