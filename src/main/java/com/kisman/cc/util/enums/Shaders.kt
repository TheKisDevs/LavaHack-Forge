package com.kisman.cc.util.enums

import com.kisman.cc.features.module.render.shader.FramebufferShader
import com.kisman.cc.features.module.render.shader.shaders.*

/**
 * @author _kisman_
 * @since 19:38 of 09.01.2023
 */
enum class Shaders(
    val buffer : FramebufferShader
) {
    AQUA(AquaShader.AQUA_SHADER),
    RED(RedShader.RED_SHADER),
    SMOKE(SmokeShader.SMOKE_SHADER),
    FLOW(FlowShader.FLOW_SHADER),
    ITEMGLOW(ItemShader.ITEM_SHADER),
    PURPLE(PurpleShader.PURPLE_SHADER),
    GRADIENT(GradientOutlineShader.INSTANCE),
    UNU(UnuShader.UNU_SHADER),
    GLOW(GlowShader.GLOW_SHADER),
    OUTLINE(OutlineShader.OUTLINE_SHADER),
    BlueFlames(BlueFlamesShader.BlueFlames_SHADER),
    CodeX(CodeXShader.CodeX_SHADER),
    Crazy(CrazyShader.CRAZY_SHADER),
    Golden(GoldenShader.GOLDEN_SHADER),
    HideF(HideFShader.HideF_SHADER),
    HotShit(HotShitShader.HotShit_SHADER),
    Kfc(KfcShader.KFC_SHADER),
    Sheldon(SheldonShader.SHELDON_SHADER),
    Smoky(SmokyShader.SMOKY_SHADER),
    SNOW(SnowShader.SNOW_SHADER),
    Techno(TechnoShader.TECHNO_SHADER),
    Circle(CircleShader),
    Circle2(Circle2Shader),
    Outline3(Outline3Shader),
    Gradient2(Gradient2Shader)
}