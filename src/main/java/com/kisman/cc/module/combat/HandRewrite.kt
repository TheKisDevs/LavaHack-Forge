package com.kisman.cc.module.combat

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.InventoryUtil
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.enums.HandRewriteSlotMappings
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraft.item.Item

class HandRewrite : Module(
        "HandRewrite",
        Category.COMBAT
) {
    private val mainhandMode = register(Setting("Mainhand Mode", this, Modes.None))
    private val mainhandSlot = register(Setting("Mainhand Slot", this, 1.0, 1.0, 9.0, true))
    private val mainhandRightClickMode = register(Setting("Mainhand Right Click Mode", this, Modes.None))
    private val offhandMode = register(Setting("Offhand Mode", this, Modes.None))
    private val offhandRightClickMode = register(Setting("Offhand Right Click Mode", this, Modes.None))

    private val hotbarFirst = register(Setting("Hotbar First", this, false))

    private val usageUpdateController = register(Setting("Usage UpdateController", this, true))

    private val offhandSlot = 45

    override fun update() {
        if(mc.player == null || mc.world == null || mc.playerController == null) return

        if(mainhandMode.valEnum != Modes.None) {
            doHandRewrite(false)
        }
        if(offhandMode.valEnum != Modes.None) {
            doHandRewrite(true)
        }
    }

    private fun doHandRewrite(offhand : Boolean) {
        if(itemCheck(offhand)) {
            doSwitch(offhand)
        }
    }

    private fun doSwitch(offhand : Boolean) {
        if(offhand) {
            switch(InventoryUtil.findItemInInventory((offhandMode.valEnum as Modes).item))
            ChatUtility.complete().printClientModuleMessage("Offhand now has a ${offhandMode.valEnum.name}")
        } else {
            switch(InventoryUtil.findItemInInventory((offhandMode.valEnum as Modes).item), HandRewriteSlotMappings.get(mainhandSlot.valInt))
            ChatUtility.complete().printClientModuleMessage("Mainhand now has a ${mainhandMode.valEnum.name}")
        }
    }

    private fun itemCheck(offhand : Boolean) : Boolean {
        return if(offhand) {
            mc.player.heldItemOffhand.item != (offhandMode.valEnum as Modes).item
        } else {
            mc.player.heldItemMainhand.item != (mainhandMode.valEnum as Modes).item
        }
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
        Totem(Items.TOTEM_OF_UNDYING)
    }
}