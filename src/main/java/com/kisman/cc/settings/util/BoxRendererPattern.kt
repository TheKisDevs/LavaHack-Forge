package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.BoxRenderModes
import com.kisman.cc.util.render.objects.Box
import com.kisman.cc.util.render.objects.BoxObject
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

class BoxRendererPattern(
    val module: Module,
    val visible : Supplier<Boolean>,
    val prefix : String?,
    val canColor : Boolean
) {
    constructor(
            module : Module
    ) : this(
            module,
            Supplier { true },
            null,
            false
    )

    constructor(
            module : Module,
            canColor : Boolean
    ) : this(
            module,
            Supplier { true },
            null,
            canColor
    )

    val mode = Setting("${(if (prefix != null) "$prefix " else "")}Mode", module, BoxRenderModes.Filled).setVisible { visible.get() }
    val depth = Setting("${(if (prefix != null) "$prefix " else "")}Depth", module, false).setVisible { visible.get() }
    val alpha = Setting("${(if (prefix != null) "$prefix " else "")}Alpha", module, true).setVisible { visible.get() }
    val width = Setting("${(if (prefix != null) "$prefix " else "")}Width", module, 2.0, 0.25, 5.0, false).setVisible { !mode.valEnum.equals(BoxRenderModes.Filled) && visible.get() }
    val offset = Setting("${(if (prefix != null) "$prefix " else "")}Offset", module, 0.002, 0.002, 0.2, false).setVisible { visible.get() }

    val color = Setting("${(if (prefix != null) "$prefix " else "")}Offset", module, "${(if (prefix != null) "$prefix " else "")}Offset", Colour(255, 255, 255, 255)).setVisible { visible.get() }

    fun init() : BoxRendererPattern {
        Kisman.instance.settingsManager.rSetting(mode)
        Kisman.instance.settingsManager.rSetting(depth)
        Kisman.instance.settingsManager.rSetting(alpha)
        Kisman.instance.settingsManager.rSetting(width)
        Kisman.instance.settingsManager.rSetting(offset)

        if(canColor) Kisman.instance.settingsManager.rSetting(color)

        return this
    }

    fun draw(ticks : Float, pos : BlockPos) {
        draw(ticks, pos, color.colour.a)
    }

    fun draw(ticks : Float, pos : BlockPos, alphaVal : Int) {
        draw(ticks, color.colour, pos, alphaVal)
    }

    fun draw(ticks: Float, color: Colour, pos: BlockPos, alphaVal: Int) {
        BoxObject(
            Box.byAABB(
                Minecraft.getMinecraft().world.getBlockState(pos).getSelectedBoundingBox(Minecraft.getMinecraft().world, pos)
                    .grow(offset.valDouble)),
            color.withAlpha(alphaVal),
            mode.valEnum as BoxRenderModes,
            width.valFloat,
            depth.valBoolean,
            alpha.valBoolean
        ).draw(ticks)
    }
}