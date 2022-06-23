package com.kisman.cc.features.module.movement

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.world.BlockUtil2
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.entity.player.PlayerUtil
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.enums.dynamic.SwapEnum2
import net.minecraft.util.EnumHand

/**
 * @author _kisman_
 * @since 10:35 of 04.06.2022
 */
class SoftScaffold : Module(
    "SoftScaffold",
    "Like default Scaffold but for soft(no solid) blocks.",
    Category.MOVEMENT
) {
    private val switchMode = register(Setting("Switch Mode", this, SwapEnum2.Swap.Silent))
    private val noJump = register(Setting("No Jump", this, false))
    private val delay = register(Setting("Delay", this, 20.0, 0.0, 1000.0, NumberType.TIME))
    private val packet = register(Setting("Packet", this, false))
    private val onlyWhenOnGround = register(Setting("Only When onGround", this, true))

    private val timer = TimerUtils()

    override fun onEnable() {
        super.onEnable()
        timer.reset()
    }

    override fun update() {
        if(mc.player == null || mc.world == null || (onlyWhenOnGround.valBoolean && !mc.player.onGround)) {
            return
        }

        if(timer.passedMillis(delay.valLong)) {
            timer.reset()

            val softBlockSlot = InventoryUtil.findSoftBlocks(0, 9)

            if(softBlockSlot == -1) {
                ChatUtility.error().printClientModuleMessage("Out of valid blocks. Disabling!")
                super.setToggled(false)
            }

            val oldSlot = mc.player.inventory.currentItem

            val switcher = switchMode.valEnum as SwapEnum2.Swap

            switcher.task.doTask(softBlockSlot, false)

            if(mc.player.onGround) {
                val pos = PlayerUtil.GetLocalPlayerPosFloored()

                BlockUtil2.placeBlock(pos, EnumHand.MAIN_HAND, packet.valBoolean)
            }

            switcher.task.doTask(oldSlot, true)
        }

        doNoJump()
    }

    private fun doNoJump() {
        if(noJump.valBoolean) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.player.motionY = 0.0
        }
    }
}