package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.features.module.combat.AutoRer
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.manager.friend.FriendManager
import com.kisman.cc.util.math.max
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 17:20 of 14.09.2022
 */
class TextRadar : HudModule(
    "TextRadar",
    "troll hack moment :sunglasses:",
    true
) {
    private val offsets = register(Setting("Offsets", this, 0.0, 0.0, 5.0, true))
    private val astolfo = register(Setting("Astolfo", this, true))
    private val color = register(Setting("Color", this, Colour(255, 0, 0, 0)))
    private val limit = register(Setting("Limit", this, 10.0, 1.0, 100.0, true))
    private val range = register(Setting("Range", this, 10.0, 1.0, 100.0, true))
    private val friendHighlight = register(Setting("Friend Highlight", this, true))
    private val autoRerTargetHighlight = register(Setting("Auto Rer Target Highlight", this, true))
    private val threads = threads()

    private val toRender = mutableListOf<String>()

    override fun onEnable() {
        super.onEnable()
        threads.reset()
        toRender.clear()
    }

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        threads.update(Runnable {
            mc.addScheduledTask {
                toRender.clear()

                for((index, player) in mc.world.playerEntities.withIndex()) {
                    if(player != mc.player) {
                        val distanceToPlayer = mc.player.getDistance(player)

                        if (distanceToPlayer <= range.valDouble && limit.valInt > index) {
                            toRender.add(
                                "${(player.health + player.absorptionAmount).toInt()} " + (if (FriendManager.instance.isFriend(
                                        player
                                    ) && friendHighlight.valBoolean
                                ) TextFormatting.AQUA else if (AutoRer.currentTarget != null && autoRerTargetHighlight.valBoolean) TextFormatting.RED else "") + " ${player.name + TextFormatting.RESET} ${distanceToPlayer.toInt()}"
                            )
                        }
                    }
                }
            }
        })

        setW(0.0)
        setH(0.0)

        for((index, line) in toRender.withIndex()) {
            CustomFontUtil.drawStringWithShadow(
                line,
                getX(),
                getY() + index * (CustomFontUtil.getFontHeight() + offsets.valInt),
                if(astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else color.colour.rgb
            )

            setW(getW().max(CustomFontUtil.getStringWidth(line).toDouble()))
            setH(((index + 1) * (CustomFontUtil.getFontHeight() + offsets.valInt)).toDouble())
        }
    }
}