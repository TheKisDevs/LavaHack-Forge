package com.kisman.cc.features.module.render.xray

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ShaderableModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.enums.XRayBlocks
import com.kisman.cc.util.client.interfaces.IBlockImplementation
import net.minecraft.util.math.BlockPos
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 23:10 of 24.12.2022
 */
class BlockImplementation(
    val block : XRayBlocks,
    val module : ShaderableModule,
    val flag : Int
) : IBlockImplementation {
    private val group = module.register(SettingGroup(Setting(block.name, module)))
    private val renderer = RenderingRewritePattern(module).prefix(block.name).group(group).preInit().init()

    init {
        module.addFlag(Supplier { renderer.canRender() })

        renderer.filledColor1.colour = block.color
        renderer.outlineColor1.colour = block.color
    }

    override fun valid(
        pos : BlockPos
    ) : Boolean = renderer.isActive() && block.validator.valid(pos)

    override fun process(
        pos : BlockPos
    ) {
        if(valid(pos)) {
            renderer.draw(pos)
        }
    }
}