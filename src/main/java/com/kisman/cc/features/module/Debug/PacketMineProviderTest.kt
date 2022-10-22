package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.mixin.accessors.IMinecraft
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.minecraft.leftClick
import com.kisman.cc.util.providers.PacketMineProvider

/**
 * @author _kisman_
 * @since 13:03 of 22.10.2022
 */
class PacketMineProviderTest : Module(
    "PacketMineProviderTest",
    "Testing the packet mine provider",
    Category.DEBUG
) {
    private val mode = register(Setting("Mode", this, Mode.PacketMineProvider))

    override fun onEnable() {
        super.onEnable()

        if(mc.player == null || mc.world == null || mc.objectMouseOver == null || mc.objectMouseOver.blockPos == null) {
            return
        }

        if(mode.valEnum == Mode.PacketMineProvider) {
            PacketMineProvider.handleBlockClick(mc.objectMouseOver.blockPos, mc.objectMouseOver.sideHit)
        } else if(mode.valEnum == Mode.PlayerDamageBlock) {
            mc.playerController.onPlayerDamageBlock(mc.objectMouseOver.blockPos, mc.objectMouseOver.sideHit)
        } else {
            leftClick()
            (mc as IMinecraft).invokeSendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown && mc.inGameHasFocus)
        }

        toggle()
    }

    private enum class Mode { PacketMineProvider, PlayerDamageBlock, LeftClick }
}