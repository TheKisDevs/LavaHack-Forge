package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.render.esp.*

/**
 * @author _kisman_
 * @since 18:39 of 06.03.2023
 */
@ModuleInfo(
    name = "ESP",
    desc = "bro idk",
    category = Category.RENDER,
    toggled = true,
    toggleable = false,
    modules = [
        BlockESP::class,
        EntityESPRewrite::class,
        HoleESPRewrite2::class,
        ItemESPRewrite::class,
        StorageESP::class
    ]
)
class ESP : Module()