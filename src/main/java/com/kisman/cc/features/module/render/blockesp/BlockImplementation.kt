package com.kisman.cc.features.module.render.blockesp

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.enums.BlockESPBlocks
import com.kisman.cc.util.interfaces.IBlockImplementation
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 9:56 of 01.11.2022
 */
class BlockImplementation(
    val block : BlockESPBlocks,
    val module : Module
) : IBlockImplementation {
    private val group = module.register(SettingGroup(Setting(block.toString(), module)))
    private val renderer = RenderingRewritePattern(module).prefix(block.name).group(group).preInit().init()

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