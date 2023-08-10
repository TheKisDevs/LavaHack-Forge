package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.render.ShaderCharms
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.client.collections.Pair
import com.kisman.cc.util.enums.RenderingRewriteModes
import com.kisman.cc.util.client.interfaces.Drawable
import com.kisman.cc.util.enums.DirectionVertexes
import com.kisman.cc.util.enums.Gradients
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.CustomTessellator
import com.kisman.cc.util.render.Rendering
import net.minecraft.client.Minecraft
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.function.Supplier

//TODO: Finish using custom tessellator
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class RenderingRewritePattern(
    module : Module,
    private val canPartial : Boolean = false,
    /*private val */customTessellator : Boolean = false
) : AbstractPattern<RenderingRewritePattern>(
    module
) {
    constructor(
        module : Module
    ) : this(
        module,
        false,
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

    val colors = RenderingRewriteColorsPattern(module).also {
        _allSettings.addAll(it._allSettings)
        _settings.addAll(it._settings)
        _groups.addAll(it._groups)
    }

    private val tessellator = if(customTessellator) {
        CustomTessellator(2097152)
    } else {
        null
    }

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

            colors.group(group!!).preInit()
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
        colors.init()

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

    open fun startTessellator() {
        Rendering.updateTessellator(tessellator)
        Rendering.resetBuffer()
    }

    open fun endTessellator() {
        Rendering.resetTessellator()
        Rendering.resetBuffer()
    }

    open fun drawTessellator() {
        tessellator?.draw = true
        tessellator?.draw()
        tessellator?.draw = false
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
            color1,
            color2,
            color1,
            color2,
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
        filledColor3 : Colour,
        filledColor4 : Colour,
        filledColor5 : Colour,
        filledColor6 : Colour,
        filledColor7 : Colour,
        filledColor8 : Colour,
        outlineColor1 : Colour,
        outlineColor2 : Colour,
        outlineColor3 : Colour,
        outlineColor4 : Colour,
        outlineColor5 : Colour,
        outlineColor6 : Colour,
        outlineColor7 : Colour,
        outlineColor8 : Colour,
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

        Rendering.setup(depth.valBoolean)
        mode.draw(
            modifyBB(Rendering.correct(a)),
            filledColor1.color,
            filledColor2.color,
            filledColor3.color,
            filledColor4.color,
            filledColor5.color,
            filledColor6.color,
            filledColor7.color,
            filledColor8.color,
            outlineColor1.color,
            outlineColor2.color,
            outlineColor3.color,
            outlineColor4.color,
            outlineColor5.color,
            outlineColor6.color,
            outlineColor7.color,
            outlineColor8.color,
            null,
            null,
            depth.valBoolean,
            false,
            ArrayList(),
            lineWidth.valFloat

        )
        Rendering.release(depth.valBoolean)

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
        filledColor3 : Colour,
        filledColor4 : Colour,
        filledColor5 : Colour,
        filledColor6 : Colour,
        filledColor7 : Colour,
        filledColor8 : Colour,
        outlineColor1 : Colour,
        outlineColor2 : Colour,
        outlineColor3 : Colour,
        outlineColor4 : Colour,
        outlineColor5 : Colour,
        outlineColor6 : Colour,
        outlineColor7 : Colour,
        outlineColor8 : Colour,
        alphaCoeff : Double
    ) {
        draw(
            Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(
                Minecraft.getMinecraft().world,
                pos
            ),
            filledColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor3.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor4.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor5.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor6.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor7.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor8.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor3.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor4.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor5.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor6.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor7.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor8.withAlpha((alphaCoeff * 255.0).toInt()),
            (mode.valEnum as RenderingRewriteModes).mode,
            emptyList()
        )
    }

    open fun draw(
        bb : AxisAlignedBB,
        filledColor1 : Colour,
        filledColor2 : Colour,
        filledColor3 : Colour,
        filledColor4 : Colour,
        filledColor5 : Colour,
        filledColor6 : Colour,
        filledColor7 : Colour,
        filledColor8 : Colour,
        outlineColor1 : Colour,
        outlineColor2 : Colour,
        outlineColor3 : Colour,
        outlineColor4 : Colour,
        outlineColor5 : Colour,
        outlineColor6 : Colour,
        outlineColor7 : Colour,
        outlineColor8 : Colour,
        alphaCoeff : Double
    ) {
        draw(
            bb,
            filledColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor3.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor4.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor5.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor6.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor7.withAlpha((alphaCoeff * 255.0).toInt()),
            filledColor8.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor1.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor2.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor3.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor4.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor5.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor6.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor7.withAlpha((alphaCoeff * 255.0).toInt()),
            outlineColor8.withAlpha((alphaCoeff * 255.0).toInt()),
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
            colors.filledColor1.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.filledColor2.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.filledColor3.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.filledColor4.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.filledColor5.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.filledColor6.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.filledColor7.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.filledColor8.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor1.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor2.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor3.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor4.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor5.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor6.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor7.color().withAlpha((alphaCoeff * 255.0).toInt()),
            colors.outlineColor8.color().withAlpha((alphaCoeff * 255.0).toInt()),
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
            colors.filledColor1.color(),
            colors.filledColor2.color(),
            colors.filledColor3.color(),
            colors.filledColor4.color(),
            colors.filledColor5.color(),
            colors.filledColor6.color(),
            colors.filledColor7.color(),
            colors.filledColor8.color(),
            colors.outlineColor1.color(),
            colors.outlineColor2.color(),
            colors.outlineColor3.color(),
            colors.outlineColor4.color(),
            colors.outlineColor5.color(),
            colors.outlineColor6.color(),
            colors.outlineColor7.color(),
            colors.outlineColor8.color(),
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
}

class RenderingRewriteColorsPattern(
    module : Module
) : AbstractPattern<RenderingRewriteColorsPattern>(
    module
) {
    private val colors = setupGroup(SettingGroup(Setting("Colors", module)))
    val filled = setupGroup(colors.add(SettingGroup(Setting("Filled", module))))
    private val filledRainbow = setupGroup(filled.add(SettingGroup(Setting("Rainbow", module))))
    val outline = setupGroup(colors.add(SettingGroup(Setting("Outline", module))))
    private val outlineRainbow = setupGroup(outline.add(SettingGroup(Setting("Rainbow", module))))

    val filledColor1 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Color", "1st")
    val filledColor2 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Second Color", "2nd")
    val filledColor3 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Third Color", "3rd")
    val filledColor4 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Fourth Color", "4th")
    val filledColor5 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Fifth Color", "5th")
    val filledColor6 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Sixth Color", "6th")
    val filledColor7 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Seventh Color", "7th")
    val filledColor8 = RenderingRewriteColorPattern(module, filled, filledRainbow, "Render Eighth Color", "8th")
    val outlineColor1 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Color", "1st")
    val outlineColor2 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Second Color", "2nd")
    val outlineColor3 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Third Color", "3rd")
    val outlineColor4 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Fourth Color", "4th")
    val outlineColor5 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Fifth Color", "5th")
    val outlineColor6 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Sixth Color", "6th")
    val outlineColor7 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Seventh Color", "7th")
    val outlineColor8 = RenderingRewriteColorPattern(module, outline, outlineRainbow, "Render Outline Eighth Color", "8th")

    override fun preInit() : AbstractPattern<RenderingRewriteColorsPattern> {
        if(group != null) {
            group!!.add(colors)
        }

        return this
    }

    override fun init() : RenderingRewriteColorsPattern {
        module.register(colors)
        module.register(filled)
        module.register(filledRainbow)
        module.register(outline)
        module.register(outlineRainbow)

        filledColor1.init()
        filledColor2.init()
        filledColor3.init()
        filledColor4.init()
        filledColor5.init()
        filledColor6.init()
        filledColor7.init()
        filledColor8.init()
        outlineColor1.init()
        outlineColor2.init()
        outlineColor3.init()
        outlineColor4.init()
        outlineColor5.init()
        outlineColor6.init()
        outlineColor7.init()
        outlineColor8.init()

        return this
    }

    override fun prefix(
        prefix : String
    ) : RenderingRewriteColorsPattern {
        filledColor1.prefix(prefix)
        filledColor2.prefix(prefix)
        filledColor3.prefix(prefix)
        filledColor4.prefix(prefix)
        filledColor5.prefix(prefix)
        filledColor6.prefix(prefix)
        filledColor7.prefix(prefix)
        filledColor8.prefix(prefix)
        outlineColor1.prefix(prefix)
        outlineColor2.prefix(prefix)
        outlineColor3.prefix(prefix)
        outlineColor4.prefix(prefix)
        outlineColor5.prefix(prefix)
        outlineColor6.prefix(prefix)
        outlineColor7.prefix(prefix)
        outlineColor8.prefix(prefix)

        return super.prefix(prefix)
    }
}

//TODO: rainbow with custom speed
class RenderingRewriteColorPattern(
    module : Module,
    colors : SettingGroup,
    rainbow : SettingGroup,
    private val name : String,
    title : String
) : AbstractPattern<RenderingRewriteColorPattern>(
    module
) {
    val color = setupSetting(colors.add(Setting(name, module, Colour(255, 0, 0, 255)).setTitle(title)))
    private val rainbow0 = setupGroup(rainbow.add(SettingGroup(Setting(title, module))))
    private val gradient = setupList(rainbow0.add(SettingsList("mode", Setting("Gradient Mode", module, Gradients.None).setTitle("Mode"), "diff", Setting("Gradient Diff", module, 1.0, 0.0, 360.0, NumberType.TIME).setTitle("Diff"))))
    private val astolfo = setupSetting(rainbow0.add(Setting("Astolfo", module, false)))
    private val pulsive = setupList(rainbow0.add(SettingGroup(Setting("Pulsive", module)).add(SettingsList("color1", Setting("Pulsive Color 1", module, Colour(255, 0, 0, 255)).setTitle("First"), "color2", Setting("Pulsive Color 2", module, Colour(0, 0, 255, 255)).setTitle("Second")))))

    override fun preInit() : AbstractPattern<RenderingRewriteColorPattern> {
        return this
    }

    override fun init() : RenderingRewriteColorPattern {
        module.register(color)
        module.register(rainbow0)
        module.register(gradient)
        module.register(astolfo)
        module.register(pulsive)

        return this
    }

    override fun prefix(
        prefix : String
    ) : RenderingRewriteColorPattern {
        return super.prefix("$prefix $name")
    }

    fun color() = color(1)

    fun color(
        offset : Int?
    ) = when(gradient["mode"].valEnum) {
        Gradients.None -> {
            if(astolfo.valBoolean) {
                Colour(ColorUtils.astolfoColors(100, 100), color.colour.alpha)
            } else {
                color.colour!!
            }
        }

        else -> {
            Colour((gradient["mode"].valEnum as Gradients).get(
                (offset ?: 0) * gradient["diff"].valInt,
                color.colour.saturation,
                color.colour.brightness,
                null,
//                gradient["speed"].valFloat,
                pulsive["color1"].colour,
                pulsive["color2"].colour
            ), color.colour.alpha)
        }
    }
}