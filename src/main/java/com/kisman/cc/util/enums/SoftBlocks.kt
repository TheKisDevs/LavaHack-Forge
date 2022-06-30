package com.kisman.cc.util.enums

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item

/**
 * @author _kisman_
 * @since 18:59 of 29.06.2022
 */
@Suppress("UNCHECKED_CAST")
enum class SoftBlocks(
    val items: List<Item>,
    val blocks : List<Block>
) {
    RedstoneWire(Blocks.REDSTONE_WIRE, ElementType.Block),
    Button(arrayListOf(Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON), ElementType.Blocks),
    Sapling(Blocks.SAPLING, ElementType.Block),
    Seed(arrayListOf(Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.CARROT, Items.NETHER_WART, Items.POTATO), ElementType.Items),
    Banner(Items.BANNER, ElementType.Item),
    String(Items.STRING, ElementType.Item),
    Lever(Blocks.LEVER, ElementType.Block),
    Web(Blocks.WEB, ElementType.Block),
    RedstoneTorch(Blocks.REDSTONE_TORCH, ElementType.Block),
    Torch(Blocks.TORCH, ElementType.Block),
    ArmorStand(Items.ARMOR_STAND, ElementType.Item)
;

    constructor(list : List<*>, type : ElementType) :
            this(
                (if(type == ElementType.Items) (list as List<Item>) else emptyList<Item>()),
                (if(type == ElementType.Blocks) (list as List<Block>) else emptyList<Block>())
            )

    constructor(obj : Any, type : ElementType) :
            this(
                (if(type == ElementType.Item) (arrayListOf<Item>(obj as Item)) else emptyList<Item>()),
                (if(type == ElementType.Block) (arrayListOf<Block>(obj as Block)) else emptyList<Block>())
            )

    enum class ElementType {
        Item, Items, Block, Blocks
    }
}