package com.kisman.cc.features.module.render.blockesp

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.enums.BlockESPBlocks
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 9:56 of 01.11.2022
 */
class BlockImplementation(
    val block : BlockESPBlocks,
    val module : Module
) {
    val group = SettingGroup(Setting(block.toString(), module))
    val renderer = RenderingRewritePattern(module).group(group).preInit().init()

    fun valid(
        pos : BlockPos
    ) : Boolean = renderer.isActive() && block.handler.valid(pos)

    fun process(
        pos : BlockPos
    ) {
        if(valid(pos)) {
            renderer.draw(pos)
        }
    }
}