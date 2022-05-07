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
import org.lwjgl.input.Mouse

class HandRewrite : Module(
        "HandRewrite",
        Category.COMBAT
) {
    private val mainhandMode = register(Setting("Mainhand Mode", this, Modes.None))
    private val mainhandSlot = register(Setting("Mainhand Slot", this, 1.0, 1.0, 9.0, true))
    private val mainhandRightClickMode = register(Setting("Mainhand Right Click Mode", this, Modes.None))
    private val mainhandLowHPMode = register(Setting("Mainhand Low HP Mode", this, Modes.None))
    private val mainhandLowHPVal = register(Setting("Mainhand Low HP Val", this, 10.0, 1.0, 36.0, true))
    private val offhandMode = register(Setting("Offhand Mode", this, Modes.None))
    private val offhandRightClickMode = register(Setting("Offhand Right Click Mode", this, Modes.None))
    private val offhandLowHPMode = register(Setting("Offhand Low HP Mode", this, Modes.None))
    private val offhandLowHPVal = register(Setting("Offhand Low HP Val", this, 10.0, 1.0, 36.0, true))

    private val usageUpdateController = register(Setting("Usage UpdateController", this, true))

    private val offhandSlot = 45

    override fun update() {
        if(mc.player == null || mc.world == null || mc.playerController == null) return

        if(mainhandMode.valEnum != Modes.None) doHandRewrite(
                false,
                right = Mouse.isButtonDown(1),
                lowHP = (
                        mc.player.health + mc.player.absorptionAmount < mainhandLowHPVal.valInt &&
                                mainhandLowHPMode.valEnum != Modes.None
                        )
        )
        if(offhandMode.valEnum != Modes.None) doHandRewrite(
                true,
                right = Mouse.isButtonDown(1),
                lowHP = (
                        mc.player.health + mc.player.absorptionAmount < offhandLowHPVal.valInt &&
                                offhandLowHPMode.valEnum != Modes.None
                        )
        )
    }

    private fun doHandRewrite(offhand : Boolean, right : Boolean, lowHP : Boolean) {
        if(itemCheck(offhand, right, lowHP)) {
            doSwitch(offhand, right, lowHP)
        }
    }

    private fun doSwitch(offhand : Boolean, right : Boolean, lowHP : Boolean) {
        if(offhand) {
            switch(InventoryUtil.findItemInInventory(((if(right) offhandRightClickMode.valEnum else if(lowHP) offhandLowHPMode.valEnum else offhandMode.valEnum) as Modes).item))
            ChatUtility.complete().printClientModuleMessage("Offhand now has a ${if(right) offhandRightClickMode.valEnum.name else offhandMode.valEnum.name}")
        } else {
            switch(InventoryUtil.findItemInInventory(((if(right) mainhandRightClickMode.valEnum else if(lowHP) mainhandLowHPMode.valEnum else mainhandMode.valEnum) as Modes).item), HandRewriteSlotMappings.get(mainhandSlot.valInt))
            ChatUtility.complete().printClientModuleMessage("Mainhand now has a ${if(right) mainhandRightClickMode.valEnum.name else mainhandMode.valEnum.name}")
        }
    }

    private fun itemCheck(offhand : Boolean, right : Boolean, lowHP : Boolean) : Boolean {
        return if(offhand) {
            mc.player.heldItemOffhand.item != ((if(right) offhandRightClickMode.valEnum else if(lowHP) offhandLowHPMode.valEnum else offhandMode.valEnum) as Modes).item
        } else {
            mc.player.heldItemMainhand.item != ((if(right) mainhandRightClickMode.valEnum else if(lowHP) offhandLowHPMode.valEnum else mainhandMode.valEnum) as Modes).item
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