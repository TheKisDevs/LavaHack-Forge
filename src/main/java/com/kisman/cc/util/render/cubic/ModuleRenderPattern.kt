package com.kisman.cc.util.render.cubic

import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.RainbowUtil
import com.kisman.cc.util.Rendering
import com.kisman.cc.util.enums.RenderingRewriteModes

class ModuleRenderPattern(module : Module) : RenderPattern() {

    val mode = Setting("Render Mode", module, RenderingRewriteModes.Filled)
    val lineWidth = Setting("Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible {
        mode.valEnum != RenderingRewriteModes.Filled && mode.valEnum != RenderingRewriteModes.FilledGradient
    }

    val rainbow = Setting("Rainbow", module, false)
    val rainbowSpeed = Setting("RainbowSpeed", module, 1.0, 0.25, 5.0, false)
    val rainbowSat = Setting("Saturation", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean}
    val rainbowBright = Setting("Brightness", module, 100.0, 0.0, 100.0, true).setVisible{rainbow.valBoolean}
    val rainbowGlow = Setting("Glow", module, "None", listOf("None", "Glow", "ReverseGlow")).setVisible{rainbow.valBoolean}

    //Colors
    val color1 = Setting("Render Color", module, "Render Color", Colour(255, 0, 0, 255))
    val color2 = Setting("Render Second Color", module, "Render Second Color", Colour(0, 120, 255, 255)).setVisible { (
                mode.valEnum == RenderingRewriteModes.FilledGradient ||
                        mode.valEnum == RenderingRewriteModes.OutlineGradient ||
                        mode.valEnum == RenderingRewriteModes.BothGradient ||
                        mode.valEnum == RenderingRewriteModes.GlowOutline ||
                        mode.valEnum == RenderingRewriteModes.Glow
                )
    }

    override fun initialize(){
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
        var c1 = color1.colour
        var c2 = color2.colour
        val glow1 = rainbowGlow.valString;
        var alpha1 = color1.colour.a;
        if(glow1.equals("ReverseGlow")){
            alpha1 = 0
        }
        val glow2 = rainbowGlow.valString;
        var alpha2 = color1.colour.a;
        if(glow2.equals("Glow")){
            alpha2 = 0
        }
        if(rainbow.valBoolean){
            c1 = RainbowUtil.rainbow2(0, rainbowSat.valInt, rainbowBright.valInt, alpha1, rainbowSpeed.valDouble)
            c2 = RainbowUtil.rainbow2(50, rainbowSat.valInt, rainbowBright.valInt, alpha2, rainbowSpeed.valDouble)
        }
        if(!rainbowGlow.valString.equals("None")){
            val reverse = rainbowGlow.valString.equals("ReverseGlow")
            var renderMode = Rendering.Mode.GLOW;
            if(reverse){
                renderMode = Rendering.Mode.REVERSE_GLOW;
            }
            return RenderBuilder.build()
                .mode(renderMode)
                .color(c1, c2)
                .lineWidth(lineWidth.valFloat)

        }
        return RenderBuilder.build()
            .mode((mode.valEnum as RenderingRewriteModes).mode)
            .color(c1, c2)
            .lineWidth(lineWidth.valFloat)

    }
}