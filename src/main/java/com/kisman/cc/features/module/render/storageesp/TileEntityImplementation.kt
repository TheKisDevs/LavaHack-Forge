package com.kisman.cc.features.module.render.storageesp

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.enums.StorageESPTileEntities
import com.kisman.cc.util.interfaces.ITileEntityImplementation
import net.minecraft.tileentity.TileEntity

/**
 * @author _kisman_
 * @since 23:34 of 24.12.2022
 */
class TileEntityImplementation(
    val tileEntity : StorageESPTileEntities,
    val module : Module
) : ITileEntityImplementation {
    private val group = module.register(SettingGroup(Setting(tileEntity.name, module)))
    private val renderer = RenderingRewritePattern(module).group(group).preInit().init()

    override fun valid(tile : TileEntity) : Boolean = renderer.isActive() && tileEntity.validator.valid(tile)

    override fun process(tile : TileEntity) {
        if(valid(tile)) {
            renderer.draw(tile.pos)
        }
    }
}