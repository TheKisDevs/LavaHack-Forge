package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageMultiLineHudModule
import com.kisman.cc.features.hud.MultiLineElement
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.client.AnimateableFeature
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextFormatting
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author _kisman_
 * @since 7:47 of 30.03.2023
 */
class Coords2 : AverageMultiLineHudModule(
    "Coords",
    "Shows your position and rotation"
) {
    private val position = register(Setting("Position", this, true))
    private val rotation = register(Setting("Rotation", this, true))

    private val positionSpoof = register(Setting("Position Spoof", this, false))
    private val rotationSpoof = register(Setting("Rotation Spoof", this, false))

    private val position1 = AnimateableFeature(this)
    private val rotation1 = AnimateableFeature(this)

    private val random = Random()

    override fun elements(
        elements : ArrayList<MultiLineElement>
    ) {
        fun nextFloat(
            coords : Boolean
        ) = if((coords && position.valBoolean && positionSpoof.valBoolean) || (!coords && rotation.valBoolean && rotationSpoof.valBoolean)) {
            random.nextFloat()
        } else {
            1f
        }

        val posX : Int
        val posZ : Int
        val nPosX : Int
        val nPosZ : Int

        if (mc.player.dimension == -1) {
            posX = mc.player.posX.toInt()
            posZ = mc.player.posZ.toInt()
            nPosX = (mc.player.posX * 8).toInt()
            nPosZ = (mc.player.posZ * 8).toInt()
        } else {
            posX = mc.player.posX.toInt()
            posZ = mc.player.posZ.toInt()
            nPosX = (mc.player.posX / 8).toInt()
            nPosZ = (mc.player.posZ / 8).toInt()
        }

        val posY = mc.player.posY.toInt()

        val position0 = "${TextFormatting.RESET}X${TextFormatting.GRAY}: " +
                "(${TextFormatting.RESET}${(posX * nextFloat(true)).toInt()}${TextFormatting.GRAY})[${TextFormatting.RESET}${(nPosX * nextFloat(true)).toInt()}${TextFormatting.GRAY}] " +
                "${TextFormatting.RESET}Y${TextFormatting.GRAY}: " +
                "(${TextFormatting.RESET}${(posY * nextFloat(true)).toInt()}${TextFormatting.GRAY}) " +
                "${TextFormatting.RESET}Z${TextFormatting.GRAY}: " +
                "(${TextFormatting.RESET}${(posZ * nextFloat(true)).toInt()}${TextFormatting.GRAY})[${TextFormatting.RESET}${(nPosZ * nextFloat(true)).toInt()}${TextFormatting.GRAY}]"

        val rotation0 = "${TextFormatting.RESET}Yaw${TextFormatting.GRAY}: " +
                    "[${TextFormatting.RESET}${(MathHelper.wrapDegrees(mc.player.rotationYaw) * nextFloat(false)).toInt()}${TextFormatting.GRAY}] " +
                    "${TextFormatting.RESET}Pitch${TextFormatting.GRAY}: " +
                    "[${TextFormatting.RESET}${(mc.player.rotationPitch * nextFloat(false)).toInt()}${TextFormatting.GRAY}]"

        elements.add(MultiLineElement(position1, position0) { position.valBoolean })
        elements.add(MultiLineElement(rotation1, rotation0) { rotation.valBoolean })
    }
}