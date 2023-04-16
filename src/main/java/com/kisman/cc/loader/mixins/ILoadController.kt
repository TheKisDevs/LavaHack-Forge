package com.kisman.cc.loader.mixins

import net.minecraftforge.fml.common.ModContainer

/**
 * @author _kisman_
 * @since 13:55 of 10.04.2023
 */
interface ILoadController {
    fun forceActiveContainer(
        container : ModContainer?
    )
}