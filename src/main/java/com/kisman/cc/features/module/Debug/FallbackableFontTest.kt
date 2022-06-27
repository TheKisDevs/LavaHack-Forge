package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.render.customfont.FallbackableFont
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 15:33 of 07.06.2022
 */
class FallbackableFontTest : Module(
    "FallbackableFontTest",
    "Test of fallback fonts",
    Category.DEBUG
) {
    val font = FallbackableFont(CustomFontUtil.getFontTTF("comfortaa-bold", 18), CustomFontUtil.getFontTTF("futura-normal", 18))

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        font.drawString("Привет uww!", 100.0, 100.0, -1, true)
    }

}