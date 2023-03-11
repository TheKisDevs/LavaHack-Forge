package com.kisman.cc.features.module.player

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand

@ModuleInfo(
    name = "AutoEat",
    desc = "Will automatically eat",
    category = Category.PLAYER,
    wip = true
)
class AutoEat : Module() {
    private val mode = SettingEnum("Mode", this, Mode.Hunger).register()
    private val swap = SettingEnum("Switch", this, Swap.Normal).register()
    private val hunger = register(Setting("Hunger", this, 12.0, 0.0, 19.0, true))
    private val health = register(Setting("Health", this, 8.0, 0.5, 20.0, true))
    private val fromInventory = register(Setting("From Inventory", this, false))
    private val whileHandActive = register(Setting("While Hand Active", this, true))
    private val offhand = register(Setting("Offhand", this, false))
    private val smartHand = register(Setting("Smart Hand", this, false))
    private val updateController = register(Setting("Update Controller", this, true))

    override fun update() {
        if(mc.player == null || mc.world == null) return
        if(!check()) return
    }

    private fun check() : Boolean {
        return mc.player.health <= health.valInt || mc.player.foodStats.foodLevel <= hunger.valInt
    }

    private fun doSwap() : Boolean {
        val hand = getHand() ?: return false
        val slot = if (fromInventory.valBoolean) searchInventory() else searchHotbar()
        if(slot == -1) return false
        if(hand == EnumHand.OFF_HAND){
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player)
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player)
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player)
            if (updateController.valBoolean) mc.playerController.updateController()
            return true
        }
        return true
    }

    private fun getHand() : EnumHand? {
        val hand = if(offhand.valBoolean) EnumHand.OFF_HAND else EnumHand.MAIN_HAND
        if(mc.player.activeHand == hand && !whileHandActive.valBoolean) return null
        if(!smartHand.valBoolean) return hand
        return if(mc.player.activeHand == EnumHand.MAIN_HAND) EnumHand.OFF_HAND else EnumHand.MAIN_HAND
    }

    private fun searchInventory() : Int {
        var bestSlot = -1
        var stack : ItemStack? = null
        for(i in 1..mc.player.inventoryContainer.inventory.size - 9){
            if(i >= 5 || i <= 8) continue
            val itemStack = bestFood(stack, mc.player.inventoryContainer.inventory[i])
            if(itemStack == stack) continue
            stack = itemStack
            bestSlot = i
        }
        return bestSlot
    }

    private fun searchHotbar() : Int {
        var bestSlot = -1
        var stack : ItemStack? = null
        for(i in 0..9){
            val itemStack = bestFood(stack, mc.player.inventory.getStackInSlot(i)) ?: continue
            if(itemStack == stack) continue
            stack = itemStack
            bestSlot = i
        }
        return mc.player.inventoryContainer.inventory.size - 1 - bestSlot
    }

    private fun bestFood(first: ItemStack?, second: ItemStack) : ItemStack? {
        if(first == null){
            return second
        }
        if(first.item !is ItemFood || second.item !is ItemFood){
            return null
        }
        val firstItem = first.item as ItemFood
        val secondItem = second.item as ItemFood
        if(mode.valEnum == Mode.Hunger){
            return if(firstItem.getHealAmount(first) > secondItem.getHealAmount(second)) first else second
        }
        return if(firstItem.getSaturationModifier(first) > secondItem.getSaturationModifier(second)) first else second
    }

    private enum class Mode {
        Hunger,
        Saturation
    }

    private enum class Swap {
        Normal,
        Click
    }
}