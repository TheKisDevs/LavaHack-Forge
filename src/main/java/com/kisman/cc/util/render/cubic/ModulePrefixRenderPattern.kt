package com.kisman.cc.util.render.cubic

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.RainbowUtil
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.enums.RenderingRewriteModes
import net.minecraft.util.math.AxisAlignedBB

class ModulePrefixRenderPattern(module : Module, prefix : String) : DelegateRenderPattern() {
    val group = SettingGroup(Setting(prefix, module))

    val mode = group.add(Setting(prefix + "Render Mode", module, RenderingRewriteModes.Filled))
    val lineWidth = group.add(Setting(prefix + "Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible {
        mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient
    })

    val rainbow = group.add(Setting("$prefix Rainbow", module, false))
    val rainbowSpeed = group.add(Setting("$prefix RainbowSpeed", module, 1.0, 0.25, 5.0, false))
    val rainbowSat = group.add(Setting("$prefix Saturation", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean})
    val rainbowBright = group.add(Setting("$prefix Brightness", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean})
    val rainbowGlow = group.add(Setting("$prefix  Glow", module, "None", listOf("None", "Glow", "ReverseGlow")).setVisible{rainbow.valBoolean})

    //Colors
    val color1 = group.add(Setting(prefix + "Render Color", module, "Render Color", Colour(255, 0, 0, 255)))
    val color2 = group.add(Setting(prefix + "Render Second Color", module, "Render Second Color", Colour(0, 120, 255, 255)).setVisible { (
            mode.valEnum == RenderingRewriteModes.FilledGradient ||
                    mode.valEnum == RenderingRewriteModes.OutlineGradient ||
                    mode.valEnum == RenderingRewriteModes.BothGradient ||
                    mode.valEnum == RenderingRewriteModes.GlowOutline ||
                    mode.valEnum == RenderingRewriteModes.Glow
            )
    })

    override fun initialize(){
        rSetting("group", group)
        rSetting("mode", mode)
        rSetting("lineWidth", lineWidth)
        rSetting("rainbow", rainbow)
        rSetting("rainbowSpeed", rainbowSpeed)
        rSetting("rainbowSat", rainbowSat)
        rSetting("rainbowBright", rainbowBright)
        rSetting("rainbowGlow", rainbowGlow)
        rSetting("color1", color1)
        rSetting("color2", color2)
    }

    override fun getRenderBuilder(): RenderBuilder {
        val c1 = getColor1()
        val c2 = getColor2()
        return DelegateRenderBuilder(this)
            .mode((mode.valEnum as RenderingRewriteModes).mode)
            .color(c1, c2)
            .lineWidth(lineWidth.valFloat)
    }

    // only DelegateRenderBuilder should call this!
    override fun render(aabb : AxisAlignedBB, delegate : DelegateRenderBuilder){
        if(!rainbowGlow.valString.equals("None")){
            val cAabb = Rendering.correct(aabb)
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
        delegate.doRender();
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