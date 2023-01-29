package com.kisman.cc.util.enums

import com.kisman.cc.util.Colour
import com.kisman.cc.util.client.interfaces.Validable
import net.minecraft.tileentity.*

/**
 * @author _kisman_
 * @since 23:41 of 24.12.2022
 */
enum class StorageESPTileEntities(
    clazz : Class<out TileEntity>,
    val color : Colour
) {
    Chest(TileEntityChest::class.java, Colour(0.94f, 0.6f, 0.11f)),
    EnderChest(TileEntityEnderChest::class.java, Colour(0.53f, 0.11f, 0.94f)),
    Furnace(TileEntityFurnace::class.java, Colour(0.34f, 0.32f, 0.34f)),
    FlowerPot(TileEntityFlowerPot::class.java, Colour(0.27f, 0.18f, 0f)),
    Dispenser(TileEntityDispenser::class.java, Colour(0.34f, 0.32f, 0.34f)),
    Dropper(TileEntityDropper::class.java, Colour(0.34f, 0.32f, 0.34f)),
    Hopper(TileEntityHopper::class.java, Colour(0.53f, 0.11f, 0.34f)),
    Shulker(TileEntityShulkerBox::class.java, Colour(0.8f, 0.08f, 0.93f))

    ;

    val validator : Validator = Validator(clazz)

    class Validator(
        val clazz : Class<out TileEntity>
    ) : Validable<TileEntity> {
        override fun valid(t : TileEntity) : Boolean = clazz.isInstance(t)
    }
}