package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.client.guimodifier.ContainerModifier
import com.kisman.cc.features.module.client.guimodifier.DevelopmentHelper
import com.kisman.cc.features.module.client.guimodifier.GuiAnimator
import com.kisman.cc.features.module.client.guimodifier.GuiShader

/**
 * @author _kisman_
 * @since 13:35 of 23.04.2023
 */
@ModuleInfo(
    name = "GuiModifier",
    category = Category.CLIENT,
    toggled = true,
    toggleable = false,
    modules = [
        ContainerModifier::class,
        DevelopmentHelper::class,
        GuiAnimator::class,
        GuiShader::class
    ]
)
class GuiModifier : Module()