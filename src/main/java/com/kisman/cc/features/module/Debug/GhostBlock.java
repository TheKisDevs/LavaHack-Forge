package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;

public class GhostBlock extends Module {

    public GhostBlock(){
        super("GhostBlock", Category.DEBUG);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }

        ItemStack curStack = mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem);

        ItemStack newStack = new ItemStack(Blocks.OBSIDIAN, 1);

        mc.player.inventory.setInventorySlotContents(mc.player.inventory.currentItem, newStack);
        mc.player.inventory.setItemStack(newStack);
        mc.player.inventory.setPickedItemStack(newStack);

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, EnumHand.MAIN_HAND, 0, 0, 0));
        mc.player.swingArm(EnumHand.MAIN_HAND);

        mc.player.inventory.setInventorySlotContents(mc.player.inventory.currentItem, curStack);
        mc.player.inventory.setItemStack(curStack);
        mc.player.inventory.setPickedItemStack(curStack);
    }
}
