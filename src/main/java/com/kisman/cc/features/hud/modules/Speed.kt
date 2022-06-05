package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.enums.SpeedUnits
import com.kisman.cc.util.render.ColorUtils
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.floor

/**
 * @author _kisman_
 * @since 14.05.2022
 */
class Speed : HudModule(
        "Speed",
        "Displays your current speed.",
        true
) {
    private val astolfo = register(Setting("Astolfo", this, true))
    private val speedUnit = register(Setting("Speed Unit", this, SpeedUnits.KMH))

    val timer = TimerUtils()

    private var prevPosX : Double = 0.0
    private var prevPosZ : Double = 0.0

    init {
        super.setDisplayInfo { "[${(speedUnit.valEnum as SpeedUnits).displayInfo}]" }
    }

    override fun onEnable() {
        super.onEnable()
        timer.reset()
    }

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        if(timer.passedMillis(1000L)) {
            prevPosX = mc.player.prevPosX
            prevPosZ = mc.player.prevPosZ
        }

        val deltaX = mc.player.posX - prevPosX
        val deltaZ = mc.player.posZ - prevPosZ

        val distance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ)

        val speed : String = when(speedUnit.valEnum as SpeedUnits) {
            SpeedUnits.BPS -> {
                (speedUnit.valEnum as SpeedUnits).formatter.format((distance * 20))
            }
            SpeedUnits.KMH -> {
                (speedUnit.valEnum as SpeedUnits).formatter.format(floor((distance / 1000) / (0.05 / 3600)))
            }
        }

        val text = "Speed: ${TextFormatting.GRAY}$speed ${(speedUnit.valEnum as SpeedUnits).displayInfo}"

        CustomFontUtil.drawStringWithShadow(text, x, y, (if (astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else -1))

        w = CustomFontUtil.getStringWidth(text).toDouble()
        h = CustomFontUtil.getFontHeight().toDouble()
    }
}