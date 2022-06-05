package com.kisman.cc.features.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventEntityFreeCam
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.entity.FreeCamCamera
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.MovementInput
import net.minecraft.util.MovementInputFromOptions
import org.lwjgl.input.Keyboard


/**
 * @author _kisman_
 * @since 10:43 of 05.06.2022
 */
class FreeCam : Module(
    "FreeCam",
    "PaidCamera",
    Category.PLAYER
) {
    private val control = register(Setting("Control", this, Keyboard.KEY_LMENU))
    private val follow = register(Setting("Follow", this, false))
    private val copyInv = register(Setting("Copy Inv", this, false))
    private val speeds = register(SettingGroup(Setting("Speeds", this)))
    private val sHorizontal = register(speeds.add(Setting("Horizontal", this, 1.0, 0.1, 2.0, false)))
    private val sVertical = register(speeds.add(Setting("Vertical", this, 1.0, 0.1, 2.0, false)))

    private var cachedAEntity : EntityPlayerSP? = null
    private var lastATick = -1
    private var oldREntity : EntityPlayerSP? = null
    private var cam : FreeCamCamera? = null
    private var movementC : MovementInput = object : MovementInputFromOptions(mc.gameSettings) {
        override fun updatePlayerMoveState() {
            if (!Keyboard.isKeyDown(control.key)) super.updatePlayerMoveState()
            else {
                moveStrafe = 0f
                moveForward = 0f
                forwardKeyDown = false
                backKeyDown = false
                leftKeyDown = false
                rightKeyDown = false
                jump = false
                sneak = false
            }
        }
    }

    private val movementP : MovementInput = object : MovementInputFromOptions(mc.gameSettings) {
        override fun updatePlayerMoveState() {
            if (Keyboard.isKeyDown(control.key)) super.updatePlayerMoveState()
            else {
                moveStrafe = 0f
                moveForward = 0f
                forwardKeyDown = false
                backKeyDown = false
                leftKeyDown = false
                rightKeyDown = false
                jump = false
                sneak = false
            }
        }
    }

    override fun onEnable() {
        Kisman.EVENT_BUS.subscribe(freecamEntity)
        if(mc.player == null || mc.world == null) {
            return
        }
        cam = FreeCamCamera(copyInv.valBoolean, follow.valBoolean, sHorizontal.valFloat, sVertical.valFloat)
        cam!!.movementInput = movementC
        mc.player.movementInput = movementP
        mc.world.addEntityToWorld(-800, cam)
        oldREntity = mc.getRenderViewEntity() as EntityPlayerSP?
        mc.setRenderViewEntity(cam)
        mc.renderChunksMany = false
    }

    override fun onDisable() {
        Kisman.EVENT_BUS.unsubscribe(freecamEntity)
        if(mc.player == null || mc.world == null) {
            return
        }
        if (cam != null) mc.world.removeEntity(cam)
        cam = null
        mc.player.movementInput = MovementInputFromOptions(mc.gameSettings)
        mc.setRenderViewEntity(oldREntity)
        mc.renderChunksMany = true
    }

    private val freecamEntity = Listener<EventEntityFreeCam>(EventHook {
        if(getAEntity() != null) {
            it.entity = getAEntity()
        }
    })

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }
        cam?.copyInventory = copyInv.valBoolean
        cam?.follow = follow.valBoolean
        cam?.hSpeed = sHorizontal.valFloat
        cam?.vSpeed = sVertical.valFloat
    }

    private fun getAEntity() : EntityPlayerSP? {
        if (cachedAEntity == null) cachedAEntity = mc.player
        val currentTick = mc.player.ticksExisted
        if (lastATick !== currentTick) {
            lastATick = currentTick
            if (toggled) {
                if (Keyboard.isKeyDown(control.key)) cachedAEntity = mc.player
                else cachedAEntity = if (mc.getRenderViewEntity() == null) mc.player else mc.getRenderViewEntity() as EntityPlayerSP?
            } else cachedAEntity = mc.player
        }
        return cachedAEntity
    }
}