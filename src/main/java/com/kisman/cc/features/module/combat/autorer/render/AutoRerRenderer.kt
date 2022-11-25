package com.kisman.cc.features.module.combat.autorer.render

import com.kisman.cc.features.module.combat.autorer.PlaceInfo
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.render.pattern.SlideRendererPattern

class AutoRerRenderer : SlideRendererPattern() {
    @JvmField var lastSelfDamage = 0.0f
    @JvmField var lastTargetDamage = 0.0f

    override fun reset() {
        super.reset()
        lastSelfDamage = 0.0f
        lastTargetDamage = 0.0f
    }

    fun onRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        alphaFadeLength : Float,
        renderer : RenderingRewritePattern,
        placeInfo : PlaceInfo,
        text : Boolean
    ) {
        update(placeInfo, renderer)
        renderWorld(
            movingLength,
            fadeLength,
            alphaFadeLength,
            renderer,
            if(text) buildString { append("%.1f".format(lastTargetDamage)); if (this.isNotEmpty()) append('/'); append("%.1f".format(lastSelfDamage)) }
            else null
        )
    }

    fun update(
        placeInfo : PlaceInfo,
        renderer : RenderingRewritePattern
    ) {
        val newBlockPos = placeInfo.blockPos

        update(newBlockPos, renderer)

        if(!placeInfo.selfDamage.isNaN() && !placeInfo.targetDamage.isNaN()) {
            lastSelfDamage = placeInfo.selfDamage
            lastTargetDamage = placeInfo.targetDamage
        } else {
            lastSelfDamage = 0f
            lastTargetDamage = 0f
        }
    }
}