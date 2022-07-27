package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.world.isEntityVisible
import net.minecraft.entity.Entity
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 19:21 of 25.07.2022
 */
class EntityCullingPattern(
    val module : Module,
    val visible : Supplier<Boolean>,
    val group : SettingGroup?
) {
    constructor(module : Module, visible : Supplier<Boolean>) : this(module, visible, null)
    constructor(module : Module) : this(module, Supplier { true })
    constructor(module : Module, group : SettingGroup) : this(module, Supplier { true }, group)

    val culling = Setting("Culling", module, false)

    fun preInit() : EntityCullingPattern {
        if(group == null) {
            return this
        }

        group.add(culling)

        return this
    }

    fun init() : EntityCullingPattern {
        module.register(group)
        module.register(culling)

        return this
    }

    fun check(entity : Entity) : Boolean {
        return !culling.valBoolean || isEntityVisible(entity)
    }
}