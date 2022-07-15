package com.kisman.cc.features.module.render

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.util.CharmsRewriteRendererPattern

/**
 * @author _kisman_
 * @since 18:29 of 14.07.2022
 */
object CharmsRewrite : Module(
    "CharmsRewrite",
    "Modify entity renderer.",
    Category.RENDER
) {
    val pattern = CharmsRewriteRendererPattern(this).init()
}