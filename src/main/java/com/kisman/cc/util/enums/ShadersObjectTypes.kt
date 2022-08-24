package com.kisman.cc.util.enums

import com.kisman.cc.util.Globals.mc

/**
 * @author _kisman_
 * @since 13:34 of 16.08.2022
 */
@Suppress("UNCHECKED_CAST")
enum class ShadersObjectTypes {
    Entity { override fun <T> list(): List<T> = mc.world.loadedEntityList as MutableList<T> },
    TileEntity { override fun <T> list(): MutableList<T> = mc.world.loadedTileEntityList as MutableList<T> },
    Hand { override fun <T> list(): List<T>? = null };

    abstract fun <T> list() : List<T>?
}