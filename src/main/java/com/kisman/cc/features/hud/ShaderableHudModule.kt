package com.kisman.cc.features.hud

import com.kisman.cc.util.compare
import com.kisman.cc.util.client.interfaces.Drawable
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 22:04 of 20.01.2023
 */
abstract class ShaderableHudModule(
    name : String,
    desc : String,
    drag : Boolean,
    @JvmField val preRender : Boolean,
    @JvmField val postRender : Boolean
) : Drawable,
    HudModule(
        name,
        desc,
        drag
) {
    init {
        super.register(shaderSetting)
    }

    constructor(
        name : String,
        drag : Boolean,
        preRender : Boolean,
        postRender : Boolean
    ) : this(
        name,
        "",
        drag,
        preRender,
        postRender
    )

    @JvmField var preNormalRender = Runnable { }
    @JvmField var shaderRender = Runnable { }
    @JvmField var postNormalRender = Runnable { }

    @SubscribeEvent fun onRender(
        event : RenderGameOverlayEvent.Text
    ) {
        reset()
        draw()

        if(!shaderSetting.valBoolean) {
            preNormalRender.run()
            shaderRender.run()
            postNormalRender.run()
        }
    }

    fun drawStringWithShadow(
        text : String,
        x : Double,
        y : Double,
        color : Int
    ) {
        if(shaderSetting.valBoolean) {
            CustomFontUtil.drawString(text, x, y + 1, color)
        } else {
            CustomFontUtil.drawStringWithShadow(text, x, y, color)
        }
    }

    fun reset() {
        preNormalRender = Runnable { }
        shaderRender = Runnable { }
        postNormalRender = Runnable { }
    }

    fun addPreNormal(
        runnable : Runnable
    ) {
        preNormalRender = compare(preNormalRender, runnable)
    }

    fun addPostNormal(
        runnable : Runnable
    ) {
        postNormalRender = compare(postNormalRender, runnable)
    }

    fun addShader(
        runnable : Runnable
    ) {
        shaderRender = compare(shaderRender, runnable)
    }
}