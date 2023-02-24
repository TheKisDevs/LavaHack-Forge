package com.kisman.cc.features.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.Colour
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.render.Rendering
import com.kisman.cc.util.world.rotate
import com.kisman.cc.util.world.rotation
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 10:58 of 23.02.2023
 */
class ChorusPredict : Module(
    "ChorusPredict",
    "Predicts choruses lol",
    Category.PLAYER
) {
    private val color = register(Setting("Color", this, Colour(255, 255, 255, 255)))
    private val remove = register(Setting("Remove", this, true))
    private val delay = register(Setting("Delay", this, 1000.0, 100.0, 10000.0, NumberType.TIME))
    private val rotate = register(Setting("Rotate", this, false))

    private val sounds = mutableMapOf<BlockPos, TimerUtils>()

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(receive)
        reset()
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    @SubscribeEvent
    fun onRenderWorld(
        event : RenderWorldLastEvent
    ) {
        for(sound in sounds.entries.toTypedArray()) {
            try {
                Rendering.TextRendering.drawText(
                    sound.key,
                    "Chorus Teleport",
                    color.colour.rgb
                )
            } catch(_ : NullPointerException) { }

//            println(sound.value.passedMillis(delay.valLong))

            if(sound.value.passedMillis(delay.valLong) && remove.valBoolean) {
                sounds.remove(sound.key)
            }
        }
    }

    private fun reset() {
        sounds.clear()
    }

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        val packet = it.packet

        if(packet is SPacketSoundEffect) {
            val sound = packet.sound

            if(sound.soundName == ResourceLocation("item.chorus_fruit.teleport")) {
                val pos = BlockPos(packet.x, packet.y, packet.z)

                sounds[pos] = timer()

                if(rotate.valBoolean) {
                    rotate(rotation(pos))
                }
            }
        }
    })
}