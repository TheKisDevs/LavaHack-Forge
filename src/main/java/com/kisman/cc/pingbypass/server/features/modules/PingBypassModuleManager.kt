package com.kisman.cc.pingbypass.server.features.modules

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module

/**
 * @author _kisman_
 * @since 21:56 of 23.08.2022
 */
object PingBypassModuleManager {
    val modules = ArrayList<Module>()

    fun init() {
        for(module in Kisman.instance.moduleManager.modules) {
            if(module.isPingBypassModule) {
                modules.add(module)
            }
        }
    }

    fun getModulesByCategory(category : Category) : ArrayList<Module> {
        val list = ArrayList<Module>()

        for(module in modules) {
            if(module.category == category) {
                list.add(module)
            }
        }

        return list
    }

    fun getModulesByCategory(category : PingBypassCategory) : ArrayList<Module> = getModulesByCategory(category.category)
}