package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.BoxRenderModes
import com.kisman.cc.util.render.objects.world.Box
import com.kisman.cc.util.render.objects.world.BoxObject
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

class BoxRendererPattern(
    module: Module
) : AbstractPattern<BoxRendererPattern>(
    module
) {
    private val mode = setupSetting(Setting("Mode", module, BoxRenderModes.Filled).setVisible { visible.get() }.setTitle("Mode"))
    private val depth = setupSetting(Setting("Depth", module, false).setVisible { visible.get() }.setTitle("Depth"))
    private val alpha = setupSetting(Setting("Alpha", module, true).setVisible { visible.get() }.setTitle("Alpha"))
    private val width = setupSetting(Setting("Width", module, 2.0, 0.25, 5.0, false).setVisible { !mode.valEnum.equals(BoxRenderModes.Filled) && visible.get() }.setTitle("Width"))
    private val offset = setupSetting(Setting("Offset", module, 0.002, 0.002, 0.2, false).setVisible { visible.get() }.setTitle("Offset"))
    private val color = setupSetting(Setting("Color", module, "${(if (prefix != null) "$prefix " else "")}Color", Colour(255, 255, 255, 255)).setVisible { visible.get() }.setTitle("Color"))

    override fun preInit() : BoxRendererPattern {
        if(group != null) {
            group?.add(mode)
            group?.add(depth)
            group?.add(alpha)
            group?.add(width)
            group?.add(offset)
            group?.add(color)
        }

        return this
    }

    override fun init() : BoxRendererPattern {
        module.register(mode)
        module.register(depth)
        module.register(alpha)
        module.register(width)
        module.register(offset)
        module.register(color)

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