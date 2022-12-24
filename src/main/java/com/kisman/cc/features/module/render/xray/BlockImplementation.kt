package com.kisman.cc.features.module.render.xray

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.enums.XRayBlocks
import com.kisman.cc.util.interfaces.IBlockImplementation
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 23:10 of 24.12.2022
 */
class BlockImplementation(
    val block : XRayBlocks,
    val module : Module
) : IBlockImplementation {
    private val group = module.register(SettingGroup(Setting(block.name, module)))
    private val renderer = RenderingRewritePattern(module).group(group).preInit().init()

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