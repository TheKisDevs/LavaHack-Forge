package com.kisman.cc.module.misc

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderBlock
import com.kisman.cc.gui.csgo.components.Slider
import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.util.BoxRendererPattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.CrystalUtils
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.hypixel.dungeonrooms.RoomDetectionUtil
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.BlockStoneBrick
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
    private val render = register(SettingGroup(Setting("Render", this)))
    private val hyperionExploitG = register(SettingGroup(Setting("Hyperion Exploit", this)))

    private val range = register(esp.add(Setting("Range", this, 30.0, 10.0, 50.0, true)))

    private val espLever = register(esp.add(Setting("Lever", this, false)))
    private val espLeverColor = register(esp.add(Setting("Lever Color", this, Colour(0, 0, 255, 255)).setVisible { espLever.valBoolean }))
    private val crackedStoneBricks = register(esp.add(Setting("Cracked Stone Bricks", this, false)))
    private val crackedStoneBricksColor = register(esp.add(Setting("Cracked Stone Bricks Color", this, Colour(255, 0, 255, 255)).setVisible { crackedStoneBricks.valBoolean }))

    private val renderer = BoxRendererPattern(this).initWithGroup(render)

    private val hyperionExploit = register(hyperionExploitG.add(Setting("Hyperion Exploit", this, false)))
    private val heAOTESlot = register(hyperionExploitG.add(Setting("HE AOTE Slot", this, 3.0, 1.0, 9.0, true)))
    private val heAOTDSlot = register(hyperionExploitG.add(Setting("HE AOTD Slot", this, 1.0, 1.0, 9.0, true)))
    private val heDelay = register(hyperionExploitG.add(Setting("HE Delay", this, 100.0, 0.0, 1000.0, Slider.NumberType.TIME)))
    private val heLogic = register(hyperionExploitG.add(Setting("HE Logic", this, HyperionExploitLogic.Manual)))
    private val heManualDelay = register(hyperionExploitG.add(Setting("HE Manual Delay", this, 1000.0 / 20.0, 0.0, 1000.0, Slider.NumberType.TIME).setVisible { heLogic.valEnum == HyperionExploitLogic.Manual }))

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
        toRender.clear()

        val block = it.state.block
        if(
            block == Blocks.LEVER && espLever.valBoolean
            || RoomDetectionUtil.whitelistedBlocks.contains(RoomDetectionUtil.getID(it.pos)) && crackedStoneBricks.valBoolean
        ) toRender.add(it.pos)
    })

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        for(pos in toRender) {
            val block = mc.world.getBlockState(pos).block
            if(block == Blocks.LEVER && espLever.valBoolean) renderer.draw(event.partialTicks, espLeverColor.colour, pos, espLeverColor.colour.a)
            if(RoomDetectionUtil.whitelistedBlocks.contains(RoomDetectionUtil.getID(pos))) renderer.draw(event.partialTicks, crackedStoneBricksColor.colour, pos, crackedStoneBricksColor.colour.a)
        }
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