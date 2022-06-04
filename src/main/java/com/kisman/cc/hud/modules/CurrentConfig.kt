package com.kisman.cc.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.customfont.CustomFontUtil
import com.kisman.cc.util.render.ColorUtils
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 16:39 of 02.06.2022
 */
class CurrentConfig : HudModule(
    "CurrentConfig",
    "Shows your current config.",
    true
) {
    private val astolfo = register(Setting("Astolfo", this, true))

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        h = CustomFontUtil.getFontHeight().toDouble()
        val text = "Current Config${TextFormatting.GRAY}: ${if(Kisman.currentConfig != null) Kisman.currentConfig else "null"}"
        CustomFontUtil.drawStringWithShadow(text, x, y, (if(astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else -1))
        w = CustomFontUtil.getStringWidth(text).toDouble()
    }
}