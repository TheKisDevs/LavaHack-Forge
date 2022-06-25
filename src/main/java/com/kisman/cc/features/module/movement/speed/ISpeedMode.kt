package com.kisman.cc.features.module.movement.speed

/**
 * @author _kisman_
 * @since 13:39 of 24.06.2022
 */
@FunctionalInterface
interface ISpeedMode {
    fun onEnable()
    fun update()
}