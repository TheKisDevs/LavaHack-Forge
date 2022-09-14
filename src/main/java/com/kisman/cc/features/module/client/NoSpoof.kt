package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.PingBypassModule
import com.kisman.cc.settings.Setting

/**
 * @author _kisman_
 * @since 12:25 of 20.08.2022
 */
@Beta
@PingBypassModule
object NoSpoof : Module(
    "NoSpoof",
    "PingBypass stuff",
    Category.CLIENT
) {
    private val position = register(Setting("Position", this, false))
    private val rotation = register(Setting("Rotation", this, false))
    private val ground = register(Setting("Ground", this, false))

    @JvmStatic fun noPosition() : Boolean = position.valBoolean
    @JvmStatic fun noRotation() : Boolean = rotation.valBoolean
    @JvmStatic fun noGround() : Boolean = ground.valBoolean
}