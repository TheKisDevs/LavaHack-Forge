package com.kisman.cc.features.module.combat.autorer.render

import com.kisman.cc.features.module.combat.autorer.PlaceInfo
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.render.pattern.SlideRendererPattern

class AutoRerRenderer : SlideRendererPattern() {

    fun onRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        renderer : RenderingRewritePattern,
        placeInfo : PlaceInfo,
        text : Boolean
    ) {
        update(placeInfo)
        renderWorld(
            movingLength,
            fadeLength,
            renderer,
            if(text)  buildString { append("%.1f".format(lastTargetDamage)); if (this.isNotEmpty()) append('/'); append("%.1f".format(lastSelfDamage)) }
            else null
        )
    }

    fun update(
        placeInfo : PlaceInfo
    ) {
        val newBlockPos = placeInfo.blockPos

        update(newBlockPos)

        if(!placeInfo.selfDamage.isNaN() && !placeInfo.targetDamage.isNaN()) {
            lastSelfDamage = placeInfo.selfDamage
            lastTargetDamage = placeInfo.targetDamage
        } else {
            lastSelfDamage = 0f
            lastTargetDamage = 0f
        }
    }
}