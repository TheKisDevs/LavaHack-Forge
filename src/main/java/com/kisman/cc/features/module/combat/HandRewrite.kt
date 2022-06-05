package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.enums.HandRewriteSlotMappings
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.item.*
import org.lwjgl.input.Mouse

class HandRewrite : Module(
        "HandRewrite",
        "Need to update btw, dont use mainhand feature",
        Category.COMBAT
) {
    private val mainhandMode = register(Setting("Mainhand Mode", this, Modes.None))
    private val mainhandSlot = register(Setting("Mainhand Slot", this, 1.0, 1.0, 9.0, true))
    private val mainhandRightClickMode = register(Setting("Mainhand Right Click Mode", this, Modes.None))
    private val mainhandLowHPMode = register(Setting("Mainhand Low HP Mode", this, Modes.None))
    private val mainhandLowHPVal = register(Setting("Mainhand Low HP Val", this, 10.0, 1.0, 36.0, true))
    private val offhandMode = register(Setting("Offhand Mode", this, Modes.None))
    private val offhandRightClickMode = register(Setting("Offhand Right Click Mode", this, Modes.None))
    private val offhandRightClickNoUsefulItems = register(Setting("Offhand Right Click No Useful Items", this, true))
    private val offhandLowHPMode = register(Setting("Offhand Low HP Mode", this, Modes.None))
    private val offhandLowHPVal = register(Setting("Offhand Low HP Val", this, 10.0, 1.0, 36.0, true))

    private val usageUpdateController = register(Setting("Usage UpdateController", this, true))
    private val hotbarFirst = register(Setting("Hotbar First", this, false))

    private val offhandSlot = 45

    override fun update() {
        if(mc.player == null || mc.world == null || mc.playerController == null) return

        if(mainhandMode.valEnum != Modes.None && canBeSwitched(false, right = false, lowHP = false)) doHandRewrite(
                false,
                right = Mouse.isButtonDown(1) && mc.currentScreen == null && canBeSwitched(false, right = true, lowHP = false),
                lowHP = (
                        mc.player.health + mc.player.absorptionAmount < mainhandLowHPVal.valInt &&
                                mainhandLowHPMode.valEnum != Modes.None &&
                                canBeSwitched(false, right = false, lowHP = true)
                        )
        )
        if(offhandMode.valEnum != Modes.None && canBeSwitched(true, right = false, lowHP = false)) doHandRewrite(
                true,
                right = Mouse.isButtonDown(1) && mc.currentScreen == null && (!offhandRightClickNoUsefulItems.valBoolean || !usefulItemsCheck()) && canBeSwitched(true, right = true, lowHP = false),
                lowHP = (
                        mc.player.health + mc.player.absorptionAmount < offhandLowHPVal.valInt &&
                                offhandLowHPMode.valEnum != Modes.None &&
                                canBeSwitched(true, right = false, lowHP = true)
                        )
        )
    }

    private fun canBeSwitched(offhand : Boolean, right : Boolean, lowHP : Boolean) : Boolean {
        return findItem(offhand, right, lowHP) != -1

//        return if(offhand) {
//            InventoryUtil.findItemInInventory(((if(right) offhandRightClickMode.valEnum else if(lowHP) offhandLowHPMode.valEnum else offhandMode.valEnum) as Modes).item) != -1
//        } else {
//            InventoryUtil.findItemInInventory(((if(right) mainhandRightClickMode.valEnum else if(lowHP) mainhandLowHPMode.valEnum else mainhandMode.valEnum) as Modes).item) != -1
//        }
    }

    private fun usefulItemsCheck() : Boolean {
        return (mc.player.heldItemMainhand.item is ItemFood)
                || (mc.player.heldItemMainhand.item is ItemPotion)
                || (mc.player.heldItemMainhand.item is ItemBucketMilk)
                || (mc.player.heldItemMainhand.item is ItemExpBottle)
                || (mc.player.heldItemMainhand.item is ItemBow)
                || (mc.player.heldItemMainhand.item is ItemEnderPearl)
    }

    private fun doHandRewrite(offhand : Boolean, right : Boolean, lowHP : Boolean) {
        if(itemCheck(offhand, right, lowHP)) {
            doSwitch(offhand, right, lowHP)
        }
    }

    private fun getItem(offhand : Boolean, right : Boolean, lowHP : Boolean) : Item? {
        return (if(offhand) (((if(right) offhandRightClickMode.valEnum else if(lowHP) offhandLowHPMode.valEnum else offhandMode.valEnum) as Modes).item) else ((if(right) mainhandRightClickMode.valEnum else if(lowHP) mainhandLowHPMode.valEnum else mainhandMode.valEnum) as Modes).item)
    }

    private fun findItem(offhand : Boolean, right : Boolean, lowHP : Boolean) : Int {
        if(hotbarFirst.valBoolean) {
            for(i in mc.player.inventoryContainer.inventory.size - 1 downTo 1) {
                if(i == 5 || i == 6 || i == 7 || i == 8) continue

                val stack = mc.player.inventoryContainer.inventory[i]

                if(stack.isEmpty) continue
                if(stack.item == getItem(offhand, right, lowHP)) {
                    return i
                }
            }
        } else {
            for(i in 0 until mc.player.inventoryContainer.inventory.size) {
                if(i == 5 || i == 6 || i == 7 || i == 8) continue

                val stack = mc.player.inventoryContainer.inventory[i]

                if(stack.isEmpty) continue
                if(stack.item == getItem(offhand, right, lowHP)) {
                    return i
                }
            }
        }
        return -1
    }

    private fun doSwitch(offhand : Boolean, right : Boolean, lowHP : Boolean) {
        if(offhand) {

            switch(findItem(offhand, right, lowHP))
        } else {
            switch(findItem(offhand, right, lowHP), HandRewriteSlotMappings.get(mainhandSlot.valInt))
        }
        ChatUtility.info().printClientModuleMessage("${if(offhand) "Offhand" else " Mainhand"} now has a ${
            if(offhand) {
                if(lowHP) {
                    offhandLowHPMode.valEnum.name
                } else if (right) {
                    offhandRightClickMode.valEnum.name
                } else {
                    offhandMode.valEnum.name
                }
            } else {
                if(lowHP) {
                    mainhandLowHPMode.valEnum.name
                } else if (right) {
                    mainhandRightClickMode.valEnum.name
                } else {
                    mainhandMode.valEnum.name
                }
            }
        }")

//        if(offhand) {
//            switch(findItem(offhand))
//            switch(InventoryUtil.findItemInInventory(((if(right) offhandRightClickMode.valEnum else if(lowHP) offhandLowHPMode.valEnum else offhandMode.valEnum) as Modes).item))
//            ChatUtility.complete().printClientModuleMessage("Offhand now has a ${if(right) offhandRightClickMode.valEnum.name else offhandMode.valEnum.name}")
//        } else {
//            switch(InventoryUtil.findItemInInventory(((if(right) mainhandRightClickMode.valEnum else if(lowHP) mainhandLowHPMode.valEnum else mainhandMode.valEnum) as Modes).item), HandRewriteSlotMappings.get(mainhandSlot.valInt))
//            ChatUtility.complete().printClientModuleMessage("Mainhand now has a ${if(right) mainhandRightClickMode.valEnum.name else mainhandMode.valEnum.name}")
//        }
    }

    private fun itemCheck(offhand : Boolean, right : Boolean, lowHP : Boolean) : Boolean {
        return (if(offhand) mc.player.heldItemMainhand.item else get(HandRewriteSlotMappings.get(mainhandSlot.valInt).windowClickSlot).item) != getItem(offhand, right, lowHP)

//        return if(offhand) {
//            mc.player.heldItemOffhand.item != getItem(offhand)
//        } else {
//            get(HandRewriteSlotMappings.get(mainhandSlot.valInt).windowClickSlot).item != ((if(right) mainhandRightClickMode.valEnum else if(lowHP) offhandLowHPMode.valEnum else mainhandMode.valEnum) as Modes).item
//        }
    }

    private fun get(slot : Int) : ItemStack {
        return mc.player.inventoryContainer.inventory[slot]
    }

    //Offhand
    private fun switch(slotOfItem : Int) {
        windowClick(slotOfItem, false)
        windowClick(slotOfItem, true)
        windowClick(slotOfItem, false)
        if(usageUpdateController.valBoolean) mc.playerController.updateController()
    }

    //Mainhand
    private fun switch(slotOfItem : Int, slotToSwitch : HandRewriteSlotMappings) {
        windowClick(slotOfItem, false)
        windowClick(slotToSwitch.windowClickSlot, false)
        windowClick(slotOfItem, false)
        if(usageUpdateController.valBoolean) mc.playerController.updateController()
    }

    private fun windowClick(slot : Int, offhand : Boolean) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, (if(offhand) offhandSlot else slot), 0, ClickType.PICKUP, mc.player)
    }

    enum class Modes(
            val item : Item?
    ) {
        None(null),
        Crystal(Items.END_CRYSTAL),
        Gap(Items.GOLDEN_APPLE),
        Totem(Items.TOTEM_OF_UNDYING),
        Exp(Items.EXPERIENCE_BOTTLE)
    }
}