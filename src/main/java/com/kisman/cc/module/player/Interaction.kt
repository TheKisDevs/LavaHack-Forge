package com.kisman.cc.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventRenderGetEntitiesINAABBexcluding
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.BlockUtil2
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemPickaxe
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class Interaction : Module(
        "Interaction",
        "NoMiningTrace + MultiTask + RoofInteract",
        Category.PLAYER
) {
    private val items = register(SettingGroup(Setting("Items", this))) as SettingGroup

    private val iPacketCrystal = register(items.add(Setting("Packet Crystal", this, false)))

    private val blocks = register(SettingGroup(Setting("Blocks", this))) as SettingGroup

    private val noMiningTrace : Setting = register(blocks.add(Setting("No Mining Trace", this, false)))
    private val nmtPickaxeOnly : Setting = register(blocks.add(Setting("NMT Pickaxe Only", this, false).setVisible { noMiningTrace.valBoolean }))
    @JvmField
    val multiTask : Setting = register(blocks.add(Setting("Multi Task", this, false)))
    private val roofInteract = register(blocks.add(Setting("Roof Interact", this, false)))
    private val fastBreak = register(blocks.add(Setting("Fast Break", this, false)))

    private val fastUse = register(SettingGroup(Setting("Fast Use", this))) as SettingGroup

    private val fuAll = register(fastUse.add(Setting("All", this, false)))
    
    private val fuExp = register(fastUse.add(Setting("Exp", this, false).setVisible { !fuAll.valBoolean }))
    private val fuObby = register(fastUse.add(Setting("Obby", this, false).setVisible { !fuAll.valBoolean }))
    private val fuEnderChest = register(fastUse.add(Setting("EnderChest", this, false).setVisible { !fuAll.valBoolean }))
    private val fuMinecart = register(fastUse.add(Setting("Minecart", this, false).setVisible { !fuAll.valBoolean }))
    private val fuCrystal = register(fastUse.add(Setting("Crystal", this, false).setVisible { !fuAll.valBoolean }))

    private var mousePos : BlockPos? = null

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(renderGetEntitiesINAABBexcluding)
        Kisman.EVENT_BUS.subscribe(send)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(send)
        Kisman.EVENT_BUS.unsubscribe(renderGetEntitiesINAABBexcluding)
    }
    
    override fun update() {
        if(mc.player == null || mc.world == null) return

        doFastUse()
        doPacketCrystal()
    }

    @SubscribeEvent fun onLeftClickBlock(event : PlayerInteractEvent.LeftClickBlock) {
        if(fastBreak.valBoolean && mc.playerController.curBlockDamageMP + BlockUtil2.getHardness(event.pos) >= 1) {
            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, mc.objectMouseOver.sideHit))
        }
    }

    private fun doPacketCrystal() {
        if(iPacketCrystal.valBoolean && mc.gameSettings.keyBindUseItem.isKeyDown && (mc.player.heldItemMainhand.item == Items.END_CRYSTAL || mc.player.heldItemOffhand.item == Items.END_CRYSTAL)) {
            val result = mc.objectMouseOver ?: return

            when(result.typeOfHit) {
                RayTraceResult.Type.MISS -> {
                    mousePos = null
                }
                RayTraceResult.Type.BLOCK -> {
                    mousePos = mc.objectMouseOver.blockPos
                }
                RayTraceResult.Type.ENTITY -> {
                    val entity : Entity? = result.entityHit
                    if(mousePos == null || entity == null) {
                        return
                    }
                    if(mousePos != BlockPos(entity.posX, entity.posY - 1, entity.posZ)) {
                        return
                    }
                    mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(mousePos, EnumFacing.DOWN, (if(mc.player.heldItemOffhand.item == Items.END_CRYSTAL) EnumHand.OFF_HAND else EnumHand.MAIN_HAND), 0f, 0f, 0f))
                }
            }
        }
    }
    
    private fun doFastUse() {
        try {
            if(
                    fuAll.valBoolean
                    || (mc.player.heldItemMainhand.item == Items.EXPERIENCE_BOTTLE && fuExp.valBoolean)
                    || (mc.player.heldItemMainhand.item == ItemBlock.getItemFromBlock(Blocks.OBSIDIAN) && fuObby.valBoolean)
                    || (mc.player.heldItemMainhand.item == ItemBlock.getItemFromBlock(Blocks.ENDER_CHEST) && fuEnderChest.valBoolean)
                    || (mc.player.heldItemMainhand.item == Items.MINECART && fuMinecart.valBoolean)
                    || (mc.player.heldItemMainhand.item == Items.END_CRYSTAL && fuCrystal.valBoolean)
            ) mc.rightClickDelayTimer = 0
        } catch (ignored : ArrayIndexOutOfBoundsException) {}
    }

    private val renderGetEntitiesINAABBexcluding = Listener<EventRenderGetEntitiesINAABBexcluding>(EventHook {
        if(noMiningTrace.valBoolean && (!nmtPickaxeOnly.valBoolean || mc.player.heldItemMainhand.item is ItemPickaxe)) it.cancel()
    })

    private val send = Listener<PacketEvent.Send>(EventHook {
        if(it.packet is CPacketPlayerTryUseItemOnBlock && roofInteract.valBoolean) {
            val packet = it.packet as CPacketPlayerTryUseItemOnBlock
            if(packet.pos.y >= 255 && packet.direction == EnumFacing.UP) {
                mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(packet.pos, EnumFacing.DOWN, packet.hand, packet.facingX, packet.facingY, packet.facingZ))
            }
        }
    })
}