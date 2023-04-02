package com.kisman.cc.features.module.render.blockesp

import com.kisman.cc.features.module.ShaderableModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.enums.BlockESPBlocks
import com.kisman.cc.util.client.interfaces.IBlockImplementation
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 9:56 of 01.11.2022
 */
class BlockImplementation(
    val block : BlockESPBlocks,
    val module : ShaderableModule,
    val flag : Int
) : IBlockImplementation {
    private val group = module.register(SettingGroup(Setting(block.toString(), module)))
    private val renderer = RenderingRewritePattern(module).prefix(block.name).group(group).preInit().init()

    init {
        module.addFlag(Supplier { renderer.canRender() })

        if(block.color != null) {
            renderer.colors.filledColor1.color.colour = block.color
            renderer.colors.outlineColor1.color.colour = block.color
        }
    }

    override fun valid(
        pos : BlockPos
    ) : Boolean = renderer.isActive() && block.handler.valid(pos)

    override fun process(
        pos : BlockPos
    ) {
        if(valid(pos)) {
            renderer.draw(pos)
        }
    }
}