package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import com.kisman.cc.util.render.ColorUtils
import net.minecraft.util.math.MathHelper
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraft.util.text.TextFormatting
import java.util.*
import kotlin.math.max

class Coords : HudModule(
    "Coords",
    "Show your current coords, rotations",
    true
) {
    private var posX = 0
    private var posZ = 0

    private var nPosX = 0
    private var nPosZ = 0

    private var posY = 0

    private val astolfo = register(Setting("Astolfo", this, true))

    private val showCoords = register(Setting("Show Coords", this, true))
    private val showRotation = register(Setting("Show Rotation", this, true))

    private val spoofs = register(SettingGroup(Setting("Spoofs", this)))
    private val coordSpoof = register(spoofs.add(Setting("Coord Spoof", this, false)))
    private val rotationSpoof = register(spoofs.add(Setting("Rotation Spoof", this, false)))

    private val offsets = register(Setting("Offsets", this, 0.0, 0.0, 5.0, true))

    private val random = Random()

    @SubscribeEvent
    fun onRender(event : RenderGameOverlayEvent.Text) {
        if (mc.player.dimension == 0) {
            posX = mc.player.posX.toInt()
            posZ = mc.player.posZ.toInt()
            nPosX = (mc.player.posX / 8).toInt()
            nPosZ = (mc.player.posZ / 8).toInt()
        } else if (mc.player.dimension == -1) {
            posX = mc.player.posX.toInt()
            posZ = mc.player.posZ.toInt()
            nPosX = (mc.player.posX * 8).toInt()
            nPosZ = (mc.player.posZ * 8).toInt()
        }

        posY = mc.player.posY.toInt()

        val color = if (astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else -1

        var width = 10.0
        var height = 10.0

        if(showCoords.valBoolean) {
            val coordString =
                "${TextFormatting.RESET}X${TextFormatting.GRAY}: " +
                        "(${TextFormatting.RESET}${(posX * nextFloat(true)).toInt()}${TextFormatting.GRAY})[${TextFormatting.RESET}${(nPosX * nextFloat(true)).toInt()}${TextFormatting.GRAY}] " +
                "${TextFormatting.RESET}Y${TextFormatting.GRAY}: " +
                        "(${TextFormatting.RESET}${(posY * nextFloat(true)).toInt()}${TextFormatting.GRAY}) " +
                "${TextFormatting.RESET}Z${TextFormatting.GRAY}: " +
                        "(${TextFormatting.RESET}${(posZ * nextFloat(true)).toInt()}${TextFormatting.GRAY})[${TextFormatting.RESET}${(nPosZ * nextFloat(true)).toInt()}${TextFormatting.GRAY}]"

            width = max(width, CustomFontUtil.getStringWidth(coordString).toDouble())
            height += CustomFontUtil.getFontHeight() + offsets.valInt

            CustomFontUtil.drawStringWithShadow(
                coordString,
                getX(),
                getY() + getH() - CustomFontUtil.getFontHeight(),
                color
            )
        }

        if(showRotation.valBoolean) {
            val rotationString =
                "${TextFormatting.RESET}Yaw${TextFormatting.GRAY}: " +
                        "[${TextFormatting.RESET}${(MathHelper.wrapDegrees(mc.player.rotationYaw) * nextFloat(false)).toInt()}${TextFormatting.GRAY}] " +
                "${TextFormatting.RESET}Pitch${TextFormatting.GRAY}: " +
                        "[${TextFormatting.RESET}${(mc.player.rotationPitch * nextFloat(false)).toInt()}${TextFormatting.GRAY}]"

            width = max(width, CustomFontUtil.getStringWidth(rotationString).toDouble())
            height += CustomFontUtil.getFontHeight()

            CustomFontUtil.drawStringWithShadow(
                rotationString,
                getX(),
                getY() + getH() - CustomFontUtil.getFontHeight() - height,
                color
            )
        }

        setW(width)
        setH(height)
    }

    private fun nextFloat(coords : Boolean) : Float {
        return if((coords && showCoords.valBoolean && coordSpoof.valBoolean) || (!coords && showRotation.valBoolean && rotationSpoof.valBoolean)) {
            random.nextFloat()
        } else {
            1f
        }
    }
}