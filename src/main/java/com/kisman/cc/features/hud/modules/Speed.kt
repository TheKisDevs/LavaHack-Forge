package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.ShaderableHudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.enums.SpeedUnits
import com.kisman.cc.util.render.ColorUtils
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import kotlin.math.floor

/**
 * @author _kisman_
 * @since 14.05.2022
 */
class Speed : ShaderableHudModule(
        "Speed",
        "Displays your current speed.",
        true,
    false,
    false
) {
    private val astolfo = register(Setting("Astolfo", this, true))
    private val color = register(Setting("Color", this, Colour(255, 255, 255, 255)))
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

    override fun draw() {
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

        shaderRender = Runnable { drawStringWithShadow(text, getX(), getY(), (if (astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else color.colour.rgb)) }

        setW(CustomFontUtil.getStringWidth(text).toDouble())
        setH(CustomFontUtil.getFontHeight().toDouble())
    }
}