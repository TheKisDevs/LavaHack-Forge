package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.enums.FadeLogic
import com.kisman.cc.util.enums.RenderingRewriteModes
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.render.objects.world.Box
import net.minecraft.util.math.AxisAlignedBB
import java.util.function.Supplier
import kotlin.math.min

/**
 * @author _kisman_
 * @since 11:34 of 31.12.2022
 */
class FadeRenderingRewritePattern(
    module : Module,
    private val defaultFadeLogic : FadeLogic,
    private val customFadeLogic : Boolean,
    private val customTessellator : Boolean
) : RenderingRewritePattern(
    module,
    false,
    customTessellator
) {
    constructor(
        module : Module,
        defaultFadeLogic : FadeLogic,
        customFadeLogic : Boolean
    ) : this(
        module,
        defaultFadeLogic,
        customFadeLogic,
        false
    )

    //TODO: custom easings

    private val filledColorFadeGroup = setupGroup(colors.filled.add(SettingGroup(Setting("Fade", module))))
    private val filledColorFadeLogic = setupEnum(filledColorFadeGroup.add(SettingEnum("Filled Color Fade Logic", module, defaultFadeLogic).setTitle("Logic")))
    private val filledColorFadeDelay = setupSetting(filledColorFadeGroup.add(Setting("Filled Color Fade Delay", module, 0.0, 0.0, 10000.0, NumberType.TIME).setTitle("Delay")))

    private val outlineColorFadeGroup = setupGroup(colors.outline.add(SettingGroup(Setting("Fade", module))))
    private val outlineColorFadeLogic = setupEnum(outlineColorFadeGroup.add(SettingEnum("Outline Color Fade Logic", module, defaultFadeLogic).setTitle("Logic")))
    private val outlineColorFadeDelay = setupSetting(outlineColorFadeGroup.add(Setting("Outline Color Fade Delay", module, 0.0, 0.0, 10000.0, NumberType.TIME).setTitle("Delay")))

//    private val wireColorFadeGroup = setupGroup(wireColorGroup.add(SettingGroup(Setting("Fade", module))))
//    private val wireColorFadeLogic = setupEnum(wireColorFadeGroup.add(SettingEnum("Wire Color Fade Logic", module, defaultFadeLogic).setTitle("Logic")))
//    private val wireColorFadeDelay = setupSetting(wireColorFadeGroup.add(Setting("Wire Color Fade Delay", module, 0.0, 0.0, 10000.0, NumberType.TIME).setTitle("Delay")))

    override fun init() : FadeRenderingRewritePattern {
        super.init()

        if(customFadeLogic) {
            module.register(filledColorFadeLogic)
            module.register(outlineColorFadeLogic)
//            module.register(wireColorFadeLogic)
        }

        module.register(filledColorFadeGroup)
        module.register(filledColorFadeDelay)

        module.register(outlineColorFadeGroup)
        module.register(outlineColorFadeDelay)

//        module.register(wireColorFadeGroup)
//        module.register(wireColorFadeDelay)

        return this
    }

    override fun preInit() : FadeRenderingRewritePattern = this.also { super.preInit() }

    override fun prefix(
        prefix : String
    ) : FadeRenderingRewritePattern = super.prefix(prefix) as FadeRenderingRewritePattern

    override fun visible(
        visible : Supplier<Boolean>
    ) : FadeRenderingRewritePattern = super.visible(visible) as FadeRenderingRewritePattern

    override fun group(
        group : SettingGroup
    ) : FadeRenderingRewritePattern = super.group(group) as FadeRenderingRewritePattern

    fun draw(
        aabb : AxisAlignedBB,
        timeStamp : Long,
        range : Float,
        distance : Float
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
            timeStamp,
            range,
            distance
        )
    }

    fun draw(
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
        timeStamp : Long
    ) {
        if(!customFadeLogic && defaultFadeLogic == FadeLogic.Time) {
            draw(
                aabb,
                filledColor1,
                filledColor2,
                filledColor3,
                filledColor4,
                filledColor5,
                filledColor6,
                filledColor7,
                filledColor8,
                outlineColor1,
                outlineColor2,
                outlineColor3,
                outlineColor4,
                outlineColor5,
                outlineColor6,
                outlineColor7,
                outlineColor8,
                mode,
                timeStamp,
                1.0f,
                1.0f
            )
        }
    }

    fun draw(
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
        range : Float
    ) {
        val center = Box.byAABB(aabb).center()

        draw(
            aabb,
            filledColor1,
            filledColor2,
            filledColor3,
            filledColor4,
            filledColor5,
            filledColor6,
            filledColor7,
            filledColor8,
            outlineColor1,
            outlineColor2,
            outlineColor3,
            outlineColor4,
            outlineColor5,
            outlineColor6,
            outlineColor7,
            outlineColor8,
            mode,
            range,
            mc.player.getDistance(center.x, center.y, center.z).toFloat()
        )
    }

    fun draw(
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
        range : Float,
        distance : Float
    ) {
        if(!customFadeLogic && defaultFadeLogic == FadeLogic.Distance) {
            draw(
                aabb,
                filledColor1,
                filledColor2,
                filledColor3,
                filledColor4,
                filledColor5,
                filledColor6,
                filledColor7,
                filledColor8,
                outlineColor1,
                outlineColor2,
                outlineColor3,
                outlineColor4,
                outlineColor5,
                outlineColor6,
                outlineColor7,
                outlineColor8,
                mode,
                1L,
                range,
                distance
            )
        }
    }

    fun draw(
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
        timeStamp : Long,
        range : Float,
        distance : Float
    ) {
        fun modifyColor(
            color : Colour,
            delay : Long,
            logic : FadeLogic
        ) : Colour = if(delay == 0L) {
            color
        } else {
            val delta = when(logic) {
                FadeLogic.Time -> (min(System.currentTimeMillis() - timeStamp, delay) / delay).toFloat()
                FadeLogic.Distance -> 1 - (min(distance, range) / range)
            }

            color.withAlpha((color.a * delta).toInt())
        }

        draw(
            aabb,
            modifyColor(filledColor1, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(filledColor2, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(filledColor3, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(filledColor4, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(filledColor5, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(filledColor6, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(filledColor7, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(filledColor8, filledColorFadeDelay.valLong, filledColorFadeLogic.valEnum),
            modifyColor(outlineColor1, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            modifyColor(outlineColor2, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            modifyColor(outlineColor3, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            modifyColor(outlineColor4, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            modifyColor(outlineColor5, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            modifyColor(outlineColor6, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            modifyColor(outlineColor7, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            modifyColor(outlineColor8, outlineColorFadeDelay.valLong, outlineColorFadeLogic.valEnum),
            mode,
            emptyList()
        )
    }
}