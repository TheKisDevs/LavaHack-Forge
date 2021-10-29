package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Test extends Module {
    public ItemStack[] xrayBlocks = new ItemStack[]{
            new ItemStack(Blocks.COAL_ORE),
            new ItemStack(Blocks.IRON_ORE),
            new ItemStack(Blocks.REDSTONE_ORE),
            new ItemStack(Blocks.LAPIS_ORE),
            new ItemStack(Blocks.DIAMOND_ORE)
    };

    public Test() {
        super("Test", "", Category.CLIENT);
        setmgr.rSetting(new Setting("TestItemsButton", this, "TestItemsButton", xrayBlocks));
        setmgr.rSetting(new Setting("ExampleEntityPreview", this, "Example", new EntityEnderCrystal(mc.world)));
    }

    public void test() {

    }
}
