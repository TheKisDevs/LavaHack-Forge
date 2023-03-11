package com.kisman.cc.features.module.Debug.submoduletest

import com.kisman.cc.features.module.Debug.SubModuleTest
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 12:40 of 06.03.2023
 */
@ModuleInfo(
    name = "SubModule2",
    desc = "Description of SubModule2",
    submodule = true
)
class SubModule2 : Module() {
    @SubscribeEvent
    fun onRender(
        event : RenderGameOverlayEvent.Text
    ) {
        CustomFontUtil.drawStringWithShadow("ong submodule2!!", 100.0, 100.0, -1)
    }
}