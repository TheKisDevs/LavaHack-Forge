package com.kisman.cc.util.world

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.client.collections.Bind
import net.minecraft.entity.Entity
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 14:19 of 11.08.2022
 */
open class DamageSyncHandler(
    val state : Supplier<Boolean>,
    val delay : Supplier<Long>,
    val minOffset : Supplier<Double>
) {
    companion object {
        private val map = HashMap<Entity, Bind<Float, TimerUtils>>()
    }

    fun reset() {
        map.clear()
    }

    fun clear() {
        if(!state.get()) {
            return
        }

        val toRemove = ArrayList<Entity>()

        for(entity in map.keys) {
            if(!mc.world.loadedEntityList.contains(entity)) {
                toRemove.add(entity)
            }
        }

        for(entity in toRemove) {
            map.remove(entity)
        }
    }

    fun check(
        damage : Float,
        target : Entity
    ) : Bind<Boolean, Float> {
        return if(map.containsKey(target) && state.get()) {
            if(map[target]!!.second.passedMillis(delay.get())) {
                reset()
                Bind(true, damage)
            } else if(damage - map[target]!!.first >= minOffset.get()) {
                map[target]!!.second.reset()
                Bind(true, damage - map[target]!!.first)
            } else {
                Bind(false, 0f)
            }
        } else {
            if(state.get()) {
                map[target] = Bind(damage, TimerUtils())
            }
            Bind(true, damage)
        }
    }
}