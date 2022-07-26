package com.kisman.cc.util.minecraft

import net.minecraft.util.ResourceLocation

/**
 * @author _kisman_
 * @since 13:57 of 24.07.2022
 */
class ResourceLocationHandler(
    val name : String,
    val shader : String
) : ResourceLocation(
    "another:resource"
) {
    override fun toString(): String {
        return name
    }
}