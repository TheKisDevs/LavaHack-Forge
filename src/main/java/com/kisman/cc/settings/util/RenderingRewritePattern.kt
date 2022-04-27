package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Rendering
import java.util.function.Supplier

class RenderingRewritePattern(
    val module : Module,
    val visible : Supplier<Boolean>
) {
    val mode = Setting("Render Mode", module, Rendering.Mode.BOX).setVisible { visible.get() }
    val lineWidth = Setting("Render Line Width", module, 1.0, 0.1, 5.0, false).setVisible { visible.get() && mode.valEnum != Rendering.Mode.BOX && mode.valEnum != Rendering.Mode.GRADIENT }

    //Colors
    val color1 = Setting("Render Color", module, "Render Color", Colour(255, 0, 0)).setVisible { visible.get() }
    val color2 = Setting("Render Second Color", module, "Render Second Color", Colour(0, 120, 255)).setVisible { visible.get() && (mode.valEnum == Rendering.Mode.BOTH_GRADIENT || mode.valEnum == Rendering.Mode.OUTLINE_GRADIENT || mode.valEnum == Rendering.Mode.GRADIENT) }

    fun init() {
        Kisman.instance.settingsManager.rSetting(mode)
        Kisman.instance.settingsManager.rSetting(lineWidth)
        Kisman.instance.settingsManager.rSetting(color1)
        Kisman.instance.settingsManager.rSetting(color2)
    }
}