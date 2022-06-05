package com.kisman.cc.features.command.commands

import com.kisman.cc.features.command.Command
import com.kisman.cc.util.entity.EntityVoid
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d


/**
 * Thanks to FFP
 *
 * @author _kisman_
 * @since 17:02 of 04.06.2022
 */
class UseCommand : Command("use") {
    companion object {
        var instance : UseCommand? = null
    }

    init {
        instance = this
    }

    override fun runCommand(s: String?, args: Array<out String>?) {
        var x = 0
        var y = 0
        var z = 0
        var cursor = 1
        var sneak = false
        var attack = false
        val mode: Mode?

        /* Parse arguments */
        while (cursor < args?.size!!) {
            if (args[cursor] == "sneak") {
                sneak = true
                cursor += 1
            } else if (args[cursor] == "attack") {
                attack = true
                cursor += 1
            } else break
        }

        if (args.size - cursor >= 3) { // Block position
            try {
                x = args[cursor++].toInt()
                y = args[cursor++].toInt()
                z = args[cursor].toInt()
            } catch (e: NumberFormatException) {
                error("Usage: $syntax")
                return
            }
            mode = Mode.BLOCK
        } else if (args.size - cursor >= 1) { // Entity id
            x = try {
                args[cursor].toInt()
            } catch (e: NumberFormatException) {
                error("Usage: $syntax")
                return
            }
            mode = Mode.ENTITY
        } else { // Raytrace
            val target_ray = mc.objectMouseOver

            if(target_ray == null) {
                error("No Target")
                return
            }

            if (target_ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                val pos = target_ray.blockPos
                x = pos.x
                y = pos.y
                z = pos.z
                mode = Mode.BLOCK
            } else if (target_ray.typeOfHit == RayTraceResult.Type.ENTITY) {
                x = target_ray.entityHit.getEntityId()
                mode = Mode.ENTITY
            } else {
                error("No Target")
                return
            }
        }


        /* Execute command */
        var ret: String? = null

        if (sneak) {
            mc.player.connection.sendPacket(
                CPacketEntityAction(
                    EntityVoid(mc.world, mc.player.getEntityId()),
                    CPacketEntityAction.Action.START_SNEAKING
                )
            )
        }

        when (mode) {
            Mode.BLOCK -> {
                val look: Vec3d = mc.player.lookVec
                mc.player.connection.sendPacket(
                    CPacketPlayerTryUseItemOnBlock(
                        BlockPos(x, y, z),
                        EnumFacing.UP,
                        EnumHand.MAIN_HAND,
                        look.x as Float,
                        look.y as Float,
                        look.z as Float
                    )
                )
                ret = String.format("Using block (%d, %d, %d)", x, y, z)
            }
            Mode.ENTITY -> {
                if (attack) {
                    mc.player.connection.sendPacket(CPacketUseEntity(EntityVoid(mc.world, x)))
                } else {
                    mc.player.connection.sendPacket(
                            CPacketUseEntity(
                                EntityVoid(
                                    mc.world, x
                                ), EnumHand.MAIN_HAND
                            )
                    )
                }
                ret = String.format("Using entity [%d]", x)
            }
        }

        if (sneak) {
            // Stop sneaking, so we don't dismount on next update
            mc.player.connection.sendPacket(
                CPacketEntityAction(
                    EntityVoid(mc.world, mc.player.getEntityId()),
                    CPacketEntityAction.Action.STOP_SNEAKING
                )
            )
        }

        complete(ret)
    }

    override fun getDescription(): String {
        return "idk"
    }

    override fun getSyntax(): String {
        return "use sneak/attack <entity_id> | <block_x> <block_y> <block_z>"
    }

    enum class Mode {
        BLOCK, ENTITY
    }
}