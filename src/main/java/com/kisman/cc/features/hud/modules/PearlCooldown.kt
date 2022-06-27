package com.kisman.cc.features.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.Render2DUtil
import com.kisman.cc.util.render.customfont.CustomFontUtil
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

/**
 * @author _kisman_
 * @since 20:39 of 20.06.2022
 */
class PearlCooldown : HudModule(
    "PearlCooldown",
    "Shows your pearl cooldown, very helps on crystalpvp.cc",
    true
) {
    private val astolfo = register(Setting("Astolfo", this, false))

    private val cooldownOnCC = 60_000L

    private var lastPearlTimestamp = 0L

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(send)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(send)
    }

    private val send = Listener<PacketEvent.Send>(EventHook {
        if(haveCooldown()) {
//            return@EventHook
        }
        val packet = it.packet
        if((packet is CPacketPlayerTryUseItem || packet is CPacketPlayerTryUseItemOnBlock) && mc.player.heldItemMainhand.item == Items.ENDER_PEARL) {
            lastPearlTimestamp = System.currentTimeMillis()
        }
    })

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        CustomFontUtil.drawStringWithShadow("Pearl Cooldown", getX(), getY(), (if(astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else -1))
        setW(CustomFontUtil.getStringWidth("Pearl Cooldown").toDouble())
        setH((CustomFontUtil.getFontHeight() + 2).toDouble())

        Render2DUtil.drawRectWH(getX(), getY() + CustomFontUtil.getFontHeight() + 1, getW(), 1.0, Color.GRAY.rgb)//1 is offset
        Render2DUtil.drawRectWH(getX(), getY() + CustomFontUtil.getFontHeight() + 1, getXCoordByCooldown(getCurrentCooldown()).coerceIn(0.0, getW()), 1.0, (if(astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else -1))//1 is height of progress bare
//        println(getXCoordByCooldown(getCurrentCooldown()).coerceIn(0.0, w))
//        println(getCurrentCooldown())
//        println("\n")
    }

    private fun haveCooldown() : Boolean {
        return getCurrentCooldown() >= getCooldown()
    }

    private fun getXCoordByCooldown(cooldown : Long): Double {
        return (getCooldown() / cooldown) * getW()
    }

    private fun getCurrentCooldown() : Long {
        return System.currentTimeMillis() - lastPearlTimestamp
    }

    private fun getCooldown(): Long {
        //TODO more servers with cooldown for pearls!!!!!
        return cooldownOnCC
    }
}