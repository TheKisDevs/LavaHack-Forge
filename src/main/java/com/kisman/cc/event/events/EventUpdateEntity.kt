package com.kisman.cc.event.events

import com.kisman.cc.event.Event
import net.minecraft.entity.Entity

/**
 * @author _kisman_
 * @since 17:58 of 06.03.2023
 */
class EventUpdateEntity(
    val entity : Entity
) : Event()