package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.render.charms.PopCharmsRewrite2
import com.kisman.cc.settings.util.CharmsRewriteRendererPattern

/**
 * @author _kisman_
 * @since 18:29 of 14.07.2022
 */
@ModuleInfo(
    name = "CharmsRewrite",
    display = "Charms",
    desc = "Modifies entity renderer.",
    category = Category.RENDER,
    modules = [
        PopCharmsRewrite2::class
    ]
)
object CharmsRewrite : Module() {
    val pattern = CharmsRewriteRendererPattern(this)

    init {
        pattern.init()
    }
}