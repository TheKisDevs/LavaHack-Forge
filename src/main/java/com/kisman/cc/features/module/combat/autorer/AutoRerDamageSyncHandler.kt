package com.kisman.cc.features.module.combat.autorer

import com.kisman.cc.util.client.collections.Bind
import com.kisman.cc.util.world.DamageSyncHandler
import net.minecraft.entity.Entity
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 17:21 of 11.08.2022
 */
class AutoRerDamageSyncHandler(
    state : Supplier<Boolean>,
    delay : Supplier<Long>,
    minOffset : Supplier<Double>,
    private val placeCheck : Supplier<Boolean>,
    private val breakCheck : Supplier<Boolean>
) : DamageSyncHandler(
    state,
    delay,
    minOffset
) {
    constructor(
        handler : DamageSyncHandler,
        placeCheck : Supplier<Boolean>,
        breakCheck : Supplier<Boolean>
    ) : this(
        handler.state,
        handler.delay,
        handler.minOffset,
        placeCheck,
        breakCheck
    )

    fun canPlace(
        damage : Float,
        target : Entity?
    ) : Bind<Boolean, Float> {
        return if(target != null && placeCheck.get()) {
            check(
                damage,
                target
            )
        } else {
            Bind(
                target != null,
                damage
            )
        }
    }

    fun canBreak(
        damage : Float,
        target : Entity?
    ) : Bind<Boolean, Float> {
        return if(target != null && breakCheck.get()) {
            check(
                damage,
                target
            )
        } else {
            Bind(
                target != null,
                damage
            )
        }
    }
}