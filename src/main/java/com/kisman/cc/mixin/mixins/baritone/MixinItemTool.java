package com.kisman.cc.mixin.mixins.baritone;

import baritone.utils.accessor.IItemTool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemTool.class)
public class MixinItemTool implements IItemTool {
    @Shadow protected Item.ToolMaterial toolMaterial;
    @Override public int getHarvestLevel() {return toolMaterial.getHarvestLevel();}
}
