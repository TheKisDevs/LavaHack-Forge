package com.kisman.cc.features.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventBlockReachDistance
import com.kisman.cc.event.events.EventRenderGetEntitiesINAABBexcluding
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.combat.KillAuraRewrite
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.manager.friend.FriendManager
import com.kisman.cc.util.world.BlockUtil2
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemPickaxe
import net.minecraft.network.play.client.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult.Type.*
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object Interaction : Module(
        "Interaction",
        "NoMiningTrace + MultiTask + RoofInteract + FastUse + NoFriendDamage",
        Category.PLAYER
) {
    private val items = register(SettingGroup(Setting("Items", this)))

    private val iPacketCrystal = register(items.add(Setting("Packet Crystal", this, false)))
    private val bowSpamGroup = register(items.add(SettingGroup(Setting("Bow Spam", this))))
    private val bowSpamState = register(bowSpamGroup.add(Setting("Bow Spam State", this, false).setTitle("State")))
    private val bowSpamLength = register(bowSpamGroup.add(Setting("Bow Spam Length", this, 3.0, 3.0, 21.0, true).setTitle("Length")))
    private val criticals = register(items.add(register(SettingGroup(Setting("Criticals", this))).add(SettingsList("state", Setting("Criticals State", this, false).setTitle("State"), "strict", Setting("Criticals Strict", this, false).setTitle("Strict"), "ka", Setting("Criticals Only KA", this, false).setTitle("With KillAura")))))

    private val blocks = register(SettingGroup(Setting("Blocks", this))) as SettingGroup

    private val noMiningTrace : Setting = register(blocks.add(Setting("No Mining Trace", this, false).setDisplayName("NoMiningTrace")))
    private val nmtPickaxeOnly : Setting = register(blocks.add(Setting("NMT Pickaxe Only", this, false).setVisible { noMiningTrace.valBoolean }.setTitle("Pickaxe Only")))
    @JvmField val multiTask : Setting = register(blocks.add(Setting("Multi Task", this, false)))
    private val roofInteract = register(blocks.add(Setting("Roof Interact", this, false)))
    private val fastBreak = register(blocks.add(Setting("Fast Break", this, false)))
    private val noInteractVal = register(blocks.add(Setting("No Interact", this, false)))
    private val noInteract = register(blocks.add(SettingGroup(Setting("No Interact", this).setVisible { noInteractVal.valBoolean })))
    @JvmField val reach : Setting = register(blocks.add(Setting("Reach", this, false)))
    @JvmField val reachDistance : Setting = register(blocks.add(Setting("Reach Distance", this, 5.0, 1.0, 10.0, true).setVisible { reach.valBoolean }))
    private val autoMine = register(blocks.add(Setting("Auto Mine", this, false)))

    private val fastUse = register(SettingGroup(Setting("Fast Use", this)))

    private val fuAll = register(fastUse.add(Setting("All", this, false)))
    
    private val fuExp = register(fastUse.add(Setting("Exp", this, false).setVisible { !fuAll.valBoolean }))
    private val fuObby = register(fastUse.add(Setting("Obby", this, false).setVisible { !fuAll.valBoolean }))
    private val fuEnderChest = register(fastUse.add(Setting("EnderChest", this, false).setVisible { !fuAll.valBoolean }))
    private val fuMinecart = register(fastUse.add(Setting("Minecart", this, false).setVisible { !fuAll.valBoolean }))
    private val fuCrystal = register(fastUse.add(Setting("Crystal", this, false).setVisible { !fuAll.valBoolean }))

    private val entities = register(SettingGroup(Setting("Entities", this))) as SettingGroup

    private val noFriendDamage = register(entities.add(Setting("No Friend Damage", this, false)))

    private val ntBlocks = register(noInteract.add(SettingGroup(Setting("Blocks", this))))

    private val ntEnderChest = register(ntBlocks.add(Setting("NT Ender Chest", this, false).setTitle("Ender Chest")))
    private val ntCraftingTable = register(ntBlocks.add(Setting("NT Crafting Table", this, false).setTitle("Craft")))
    private val ntChest = register(ntBlocks.add(Setting("NT Chest", this, false).setTitle("Chest")))
    private val ntFurnace = register(ntBlocks.add(Setting("NT Furnace", this, false).setTitle("Furnace")))
    private val ntAnvil = register(ntBlocks.add(Setting("NT Anvil", this, false).setTitle("Anvil")))
    private val ntArmorStand = register(ntBlocks.add(Setting("NT Armor Stand", this, false).setTitle("Armor Stand")))

    private val ntReplacePacket = register(ntBlocks.add(Setting("NT Replace Packet", this, false).setTitle("Replace Packet")))

    private var mousePos : BlockPos? = null

    init {
        noMiningTrace.setDisplayInfo { if(nmtPickaxeOnly.valBoolean) "[Pickaxe Only]" else "" }
        reach.setDisplayInfo { reachDistance.valInt.toString() }
    }

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(renderGetEntitiesINAABBexcluding)
        Kisman.EVENT_BUS.subscribe(send)
        Kisman.EVENT_BUS.subscribe(blockReachDistance)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(blockReachDistance)
        Kisman.EVENT_BUS.unsubscribe(send)
        Kisman.EVENT_BUS.unsubscribe(renderGetEntitiesINAABBexcluding)
    }
    
    override fun update() {
        if(mc.player == null || mc.world == null) return

        doFastUse()
        doPacketCrystal()
        doBowSpam()
        doAutoMine()
    }

    @SubscribeEvent fun onLeftClickBlock(event : PlayerInteractEvent.LeftClickBlock) {
        if(fastBreak.valBoolean && mc.playerController.curBlockDamageMP + BlockUtil2.getHardness(event.pos) >= 1) {
            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, mc.objectMouseOver.sideHit))
        }
    }

    private fun doAutoMine() {
        if(autoMine.valBoolean) {
            val result = mc.objectMouseOver ?: return

            mc.playerController.onPlayerDamageBlock(result.blockPos, result.sideHit)
        }
    }

    private fun doBowSpam() {
        if(bowSpamState.valBoolean && mc.player.heldItemMainhand.item == Items.BOW && mc.player.isHandActive && mc.player.itemInUseMaxCount >= bowSpamLength.valInt) {
            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.horizontalFacing))
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(mc.player.activeHand))
            mc.player.stopActiveHand()
        }
    }

    private fun doPacketCrystal() {
        if(iPacketCrystal.valBoolean && mc.gameSettings.keyBindUseItem.isKeyDown && (mc.player.heldItemMainhand.item == Items.END_CRYSTAL || mc.player.heldItemOffhand.item == Items.END_CRYSTAL)) {
            val result = mc.objectMouseOver ?: return

            when(result.typeOfHit!!) {
                MISS -> {
                    mousePos = null
                }
                BLOCK -> {
                    mousePos = mc.objectMouseOver.blockPos
                }
                ENTITY -> {
                    val entity : Entity? = result.entityHit
                    if(mousePos == null || entity == null) {
                        return
                    }
                    if(mousePos != BlockPos(entity.posX, entity.posY - 1, entity.posZ)) {
                        return
                    }
                    mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(mousePos!!, EnumFacing.DOWN, (if(mc.player.heldItemOffhand.item == Items.END_CRYSTAL) EnumHand.OFF_HAND else EnumHand.MAIN_HAND), 0f, 0f, 0f))
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

    private val blockReachDistance = Listener<EventBlockReachDistance>(EventHook {
        if(reach.valBoolean) {
            it.distance = reachDistance.valFloat
        }
    })

    private val renderGetEntitiesINAABBexcluding = Listener<EventRenderGetEntitiesINAABBexcluding>(EventHook {
        if(noMiningTrace.valBoolean && (!nmtPickaxeOnly.valBoolean || mc.player.heldItemMainhand.item is ItemPickaxe)) it.cancel()
    })

    private val send = Listener<PacketEvent.Send>(EventHook {
        val packet = it.packet

        if(packet is CPacketPlayerTryUseItemOnBlock && mc.objectMouseOver != null && noInteractVal.valBoolean) {
            when(mc.objectMouseOver.typeOfHit!!) {
                ENTITY -> {
                    if(ntArmorStand.valBoolean && mc.objectMouseOver.entityHit is EntityArmorStand) {
                        if(ntReplacePacket.valBoolean) {
                            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(packet.hand))
                        }
                        it.cancel()
                    }
                }
                BLOCK -> {
                    val block = mc.world.getBlockState(mc.objectMouseOver.blockPos).block
                    if(
                            (block == Blocks.ENDER_CHEST && ntEnderChest.valBoolean)
                            || (block == Blocks.CRAFTING_TABLE && ntCraftingTable.valBoolean)
                            || (block == Blocks.CHEST && ntChest.valBoolean)
                            || (block == Blocks.FURNACE && ntFurnace.valBoolean)
                            || (block == Blocks.ANVIL && ntAnvil.valBoolean)
                    ) {
                        if(ntReplacePacket.valBoolean) {
                            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(packet.hand))
                        }
                        it.cancel()
                    }
                }
                MISS -> {}
            }
        }

        if(packet is CPacketUseEntity && noFriendDamage.valBoolean) {
            val entity = packet.getEntityFromWorld(mc.world)
            if(entity is EntityPlayer && FriendManager.instance.isFriend(entity.name)) {
                it.cancel()
                return@EventHook
            }
        }

        if(packet is CPacketPlayerTryUseItemOnBlock && roofInteract.valBoolean) {
            if(packet.pos.y >= 255 && packet.direction == EnumFacing.UP) {
                mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(packet.pos, EnumFacing.DOWN, packet.hand, packet.facingX, packet.facingY, packet.facingZ))
            }
        }

        if(packet is CPacketUseEntity && criticals["state"].valBoolean) {
            if(packet.action == CPacketUseEntity.Action.ATTACK && mc.player.onGround && !mc.player.isInWater && !mc.player.isInLava && !mc.player.isInWeb && (!criticals["ka"].valBoolean || KillAuraRewrite.instance!!.isToggled)) {
                val entity = packet.getEntityFromWorld(mc.world)

                if(entity is EntityLivingBase) {
                    val x = mc.player.posX
                    val y = mc.player.posY
                    val z = mc.player.posZ

                    if(criticals["strict"].valBoolean) {
                        mc.player.connection.sendPacket(CPacketPlayer.Position(x, y + 0.07, z, false))
                        mc.player.connection.sendPacket(CPacketPlayer.Position(x, y + 0.08, z, false))
                        mc.player.connection.sendPacket(CPacketPlayer.Position(x, y, z, false))
                    }

                    mc.player.connection.sendPacket(CPacketPlayer.Position(x, y + 0.05, z, false))
                    mc.player.connection.sendPacket(CPacketPlayer.Position(x, y, z, false))
                    mc.player.connection.sendPacket(CPacketPlayer.Position(x, y + 0.012, z, false))
                    mc.player.connection.sendPacket(CPacketPlayer.Position(x, y, z, false))
                    mc.player.onCriticalHit(entity)
                }
            }
        }
    })
}