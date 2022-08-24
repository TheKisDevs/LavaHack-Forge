package com.kisman.cc.pingbypass.server.features.modules

import com.kisman.cc.features.module.Category

/**
 * @author _kisman_
 * @since 22:32 of 23.08.2022
 */
enum class PingBypassCategory(
    val category : Category
) {
    COMBAT(Category.COMBAT),
    CLIENT(Category.CLIENT),
    MOVEMENT(Category.MOVEMENT),
    PLAYER(Category.PLAYER)
}