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
    module : Module,
    private val customGroup : Boolean
) : AbstractPattern<EntityCullingPattern>(
    module
) {
    private val customGroup_ = setupGroup(SettingGroup(Setting("Culling", module)))

    val culling = Setting("Culling", module, false)

    override fun preInit() : EntityCullingPattern {
        if(group != null) {
            if(customGroup) {
                customGroup_.add(culling)
                group?.add(customGroup_)
            } else {
                group?.add(culling)
            }
        }

        return this
    }

    override fun init() : EntityCullingPattern {
        if(customGroup) {
            module.register(customGroup_)
        }
        module.register(culling)

        return this
    }

    fun check(entity : Entity) : Boolean {
        return !culling.valBoolean || isEntityVisible(entity)
    }
}