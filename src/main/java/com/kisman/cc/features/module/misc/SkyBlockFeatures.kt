package com.kisman.cc.features.module.misc

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderBlock
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.TimerUtils
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.init.Blocks
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Mouse
import java.util.ArrayList

/**
 * @author _kisman_
 * @since 18:28 of 29.05.2022
 */
class SkyBlockFeatures : Module(
    "SkyBlockFeatures",
    "Legit features for Hypixel SkyBlock...",
    Category.MISC
) {
    private val esp = register(SettingGroup(Setting("EPS", this)))
    private val hyperionExploitG = register(SettingGroup(Setting("Hyperion Exploit", this)))

    private val lever = register(esp.add(Setting("Lever", this, false)))
    private val leverRendererGroup = register(esp.add(SettingGroup(Setting("Renderer", this))))
    private val leverRenderer = RenderingRewritePattern(this).group(leverRendererGroup).preInit().init()

    private val crackedStoneBricks = register(esp.add(Setting("Cracked Stone Bricks", this, false)))
    private val crackedStoneBricksRendererGroup = register(esp.add(SettingGroup(Setting("Renderer", this))))
    private val crackedStoneBricksRenderer = RenderingRewritePattern(this).group(leverRendererGroup).preInit().init()

    private val hyperionExploit = register(hyperionExploitG.add(Setting("Hyperion Exploit", this, false)))
    private val heAOTESlot = register(hyperionExploitG.add(Setting("HE AOTE Slot", this, 3.0, 1.0, 9.0, true)))
    private val heAOTDSlot = register(hyperionExploitG.add(Setting("HE AOTD Slot", this, 1.0, 1.0, 9.0, true)))
    private val heDelay = register(hyperionExploitG.add(Setting("HE Delay", this, 100.0, 0.0, 1000.0, NumberType.TIME)))
    private val heLogic = register(hyperionExploitG.add(Setting("HE Logic", this, HyperionExploitLogic.Manual)))
    private val heManualDelay = register(hyperionExploitG.add(Setting("HE Manual Delay", this, 1000.0 / 20.0, 0.0, 1000.0, NumberType.TIME).setVisible { heLogic.valEnum == HyperionExploitLogic.Manual }))

    private val toRender = ArrayList<BlockPos>()

    private val hyperionExploitTimer = TimerUtils()
    private val hyperionExploitManualTimer = TimerUtils()
    private val hyperionExploitManualState = 0
    private var flag1 = false

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(renderBlock)
        hyperionExploitTimer.reset()
        hyperionExploitManualTimer.reset()
        toRender.clear()
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(renderBlock)
    }

    override fun update() {
        if(!hyperionExploit.valBoolean) {
            if (hyperionExploitTimer.passedMillis(heDelay.valLong)) {
                hyperionExploitTimer.reset()
            }
        }

        if (hyperionExploitManualTimer.passedMillis(heManualDelay.valLong)) {
            hyperionExploitManualTimer.reset()
            if(hyperionExploit.valBoolean) {
                if (mc.player != null && mc.world != null) {
                    if (flag1) {
                        mc.player.inventory.currentItem = fixSlot(heAOTDSlot.valInt)
                        mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND)
                        mc.player.inventory.currentItem = fixSlot(heAOTESlot.valInt)
                        flag1 = false
                    }
                }
            }
        }
    }

    private val renderBlock = Listener<EventRenderBlock>(EventHook {
        if(
            mc.world.getBlockState(it.pos).block == Blocks.LEVER && lever.valBoolean
//            || RoomDetectionUtil.whitelistedBlocks.contains(RoomDetectionUtil.getID(it.pos)) && crackedStoneBricks.valBoolean
        ) {
            toRender.add(it.pos)
        }
    })

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        for(pos in ArrayList(toRender)) {
            val block = mc.world.getBlockState(pos).block
            if(block == Blocks.LEVER && lever.valBoolean) leverRenderer.draw(pos)
//            if(RoomDetectionUtil.whitelistedBlocks.contains(RoomDetectionUtil.getID(pos))) renderer.draw(event.partialTicks, crackedStoneBricksColor.colour, pos, crackedStoneBricksColor.colour.a)
        }
        toRender.clear()
    }

    @SubscribeEvent fun onMouseInput(event : InputEvent.MouseInputEvent) {
        if(mc.player != null && mc.world != null) {
            if(hyperionExploit.valBoolean) {
                if(Mouse.isCreated()) {
                    if(Mouse.getEventButtonState()) {
                        val button = Mouse.getEventButton()

                        if(button == 1) { //Right Click detection
                            doHyperionExploit()
                        }
                    }
                }
            }
        }

    }

    private fun doHyperionExploit() {
        when(heLogic.valEnum as HyperionExploitLogic) {
            HyperionExploitLogic.Instant -> {
                mc.player.inventory.currentItem = fixSlot(heAOTDSlot.valInt)
                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND)
                mc.player.inventory.currentItem = fixSlot(heAOTESlot.valInt)
            }
            HyperionExploitLogic.Manual -> {
                if(flag1) {
                    //Idk
                } else {

                    flag1 = true;
                }
            }
        }
    }

    private fun fixSlot(slot : Int) : Int {
        return slot - 1
    }

    enum class HyperionExploitLogic {
        Instant, Manual
    }
}