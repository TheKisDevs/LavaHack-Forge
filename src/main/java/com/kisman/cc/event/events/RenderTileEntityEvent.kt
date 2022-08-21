package com.kisman.cc.event.events

import com.kisman.cc.event.Event
import net.minecraft.tileentity.TileEntity

/**
 * @author _kisman_
 * @since 20:19 of 17.08.2022
 */
open class RenderTileEntityEvent(
    val tileEntity : TileEntity
) : Event() {
    class Pre(
        tileEntity : TileEntity
    ) : RenderTileEntityEvent(
        tileEntity
    )

    class Post(
        tileEntity : TileEntity
    ) : RenderTileEntityEvent(
        tileEntity
    )
}