package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.createDoubleArray
import com.kisman.cc.util.render.objects.screen.AbstractObject
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


/**
 * @author _kisman_
 * @since 15:19 of 05.08.2022
 */
class InventoryHud : HudModule(
    "InventoryHud",
    "Shows your inventory",
    true
) {
    private val scale = register(Setting("Scale", this, 1.0, 0.5, 2.0, false))

    private val offsets = register(SettingGroup(Setting("Offsets", this)))
    private val offset1 = register(offsets.add(Setting("Offset 1", this, 20.0, 16.0, 25.0, true).setTitle("1")))
    private val offset2 = register(offsets.add(Setting("Offset 2", this, 18.0, 16.0, 20.0, true).setTitle("2")))

    private val outlineWidth = register(SettingGroup(Setting("Outline Width", this)))
    private val insideOutlineWidth = register(register(outlineWidth.add(Setting("Inside Outline Width", this, 1.0, 0.1, 5.0, false).setTitle("Inside"))))
    private val outsideOutlineWidth = register(register(outlineWidth.add(Setting("Outside Outline Width", this, 1.0, 0.1, 5.0, false).setTitle("Outside"))))

    private val elements = register(SettingGroup(Setting("Elements", this)))
    private val outlineGroup = register(elements.add(SettingGroup(Setting("Outline", this))))
    private val insideOutline = register(outlineGroup.add(Setting("Inside Outline", this, true).setTitle("Inside")))
    private val outsideOutline = register(outlineGroup.add(Setting("Outside Outline", this, false).setTitle("Outside")))
    private val fill = register(elements.add(Setting("Fill", this, false)))

    private val colors = register(SettingGroup(Setting("Colors", this)))
    private val outlineColors = register(colors.add(SettingGroup(Setting("Elements", this))))
    private val insideOutlineColor = register(outlineColors.add(Setting("Inside Outline", this, "Inside", Colour(255, 255, 255, 120))))
    private val outsideOutlineColor = register(outlineColors.add(Setting("Outside Outline", this, "Outside", Colour(255, 255, 255, 120))))
    private val fillColor = register(colors.add(Setting("Fill Color", this, "Fill", Colour(255, 255, 255, 120))))

    private val slots = register(SettingGroup(Setting("Slots", this)))
    private val inventory = register(slots.add(Setting("Inventory", this, true)))
    private val hotbar = register(slots.add(Setting("Hotbar", this, false)))
    private val xcarry = register(slots.add(Setting("XCarry", this, false)))
    private val armor = register(slots.add(Setting("Armor", this, false)))
    private val offhand = register(slots.add(Setting("Offhand", this, false)))

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        val x = getX().toInt()
        val y = getY().toInt()

        var hotbarY = y

        var height = 0.0

        val startX = x + (if(armor.valBoolean) offset1.valInt else 0)

//        val vectors = ArrayList<DoubleArray>()

//        vectors.add(createDoubleArray(
//            x.toDouble(),
//            y.toDouble()
//        ))

//        var maxY = 0.0
//        var maxX = 0.0

        if(inventory.valBoolean) {
            for (item in 9 until mc.player.inventory.mainInventory.size) {
                val slotX = startX + 1 + item % 9 * offset2.valInt
                val slotY = y + 1 + (item / 9 - 1) * offset2.valInt

                if(hotbarY < slotY) {
                    hotbarY = slotY
                }

                drawSlot(
                    mc.player.inventory.mainInventory[item],
                    slotX,
                    slotY
                )
            }

            height += offset1.valInt * 3.0

//            maxX = x +
//            maxY = y + 1.0 + 3 * offset2.valInt
        }

        if(hotbar.valBoolean) {
            val slotY = y + (if(inventory.valBoolean) offset1.valInt * 3 else 0) + 1

            for(item in 0 until 9) {
                val slotX = startX + 1 + item * offset2.valInt

                drawSlot(
                    mc.player.inventory.mainInventory[item],
                    slotX,
                    slotY
                )
            }

            height += offset1.valInt
        }

        if(armor.valBoolean) {
            val slotX = x + 1

            for (item in 0 until mc.player.inventory.armorInventory.size) {
                val slotY = y + 1 + item * offset2.valInt

                drawSlot(
                    mc.player.inventory.armorInventory[item],
                    slotX,
                    slotY
                )
            }

            height = offset1.valInt * 4.0
        }

        if(xcarry.valBoolean) {
            for(item in 1 until 5) {
                val slotX = startX + (if(inventory.valBoolean || hotbar.valBoolean) offset1.valInt * 9 else 0) + (if(item == 2 || item == 4) offset2.valInt else 0)
                val slotY = y + 1 + (if(item > 2) offset2.valInt else 0)

                drawSlot(
                    mc.player.inventoryContainer.inventory[item],
                    slotX,
                    slotY
                )
            }
        }

        if(offhand.valBoolean) {
            val slotX = startX + (if(inventory.valBoolean || hotbar.valBoolean) offset1.valInt * 9 else 0)
            val slotY = y + (if(xcarry.valBoolean) offset1.valInt * 2 else 0) + 1

            drawSlot(
                mc.player.inventory.offHandInventory[0],
                slotX,
                slotY
            )
        }


//        if(vectors.isNotEmpty()) {
//
//        }


        setH(height)

        setW(100.0)
    }

    private fun drawSlot(
        stack : ItemStack,
        x : Int,
        y : Int
    ) {
        GlStateManager.pushMatrix()
        GlStateManager.enableDepth()
        RenderHelper.enableGUIStandardItemLighting()
        mc.getRenderItem().renderItemAndEffectIntoGUI(
            stack,
            x,
            y
        )
        mc.getRenderItem().renderItemOverlays(
            mc.fontRenderer,
            mc.player.inventory.offHandInventory[0],
            x,
            y
        )
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableDepth()
        GlStateManager.popMatrix()

        /*if(outsideOutline.valBoolean) {
            AbstractObject(
                arrayListOf(
                    createDoubleArray(
                        x - 1.0,
                        y - 1.0
                    ),
                    createDoubleArray(
                        x - 1.0 + offset2.valInt,
                        y - 1.0
                    ),
                    createDoubleArray(
                        x - 1.0,
                        y - 1.0
                    ),
                    createDoubleArray(
                        x - 1.0 + offset2.valInt,
                        y - 1.0 + offset2.valInt
                    )
                ),
                insideOutlineColor.colour.color,
                true,
                outsideOutlineWidth.valFloat
            )
        }*/
    }
}