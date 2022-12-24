package com.kisman.cc.util.enums

import com.kisman.cc.util.interfaces.Validable
import net.minecraft.tileentity.*

/**
 * @author _kisman_
 * @since 23:41 of 24.12.2022
 */
enum class StorageESPTileEntities(
    clazz : Class<out TileEntity>
) {
    Chest(TileEntityChest::class.java),
    EnderChest(TileEntityEnderChest::class.java),
    Furnace(TileEntityFurnace::class.java),
    FlowerPot(TileEntityFlowerPot::class.java),
    Dispenser(TileEntityDispenser::class.java),
    Dropper(TileEntityDropper::class.java),
    Hopper(TileEntityHopper::class.java),
    Shulker(TileEntityShulkerBox::class.java)

    ;

    val validator : Validator = Validator(clazz)

    class Validator(
        val clazz : Class<out TileEntity>
    ) : Validable<TileEntity> {
        override fun valid(t : TileEntity) : Boolean = clazz.isInstance(t)
    }
}