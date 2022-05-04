package com.kisman.cc.module.misc;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GhostEnderPearl extends Module {

    public GhostEnderPearl(){
        super("GhostEnderPearl", Category.MISC);
    }

    private EntityEnderPearl pearl = null;

    @Override
    public void update() {
        if (mc.world == null || mc.player == null)
            return;

        if (pearl == null)
            return;

        if (pearl.posY <= mc.player.posY) {
            pearl.noClip = false;
            pearl = null;
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event){
        if(mc.world == null || mc.player == null)
            return;

        if(!isToggled())
            return;

        if(mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() != Items.ENDER_PEARL)
            return;

        EntityEnderPearl pearl = null;

        for(Entity entity : mc.world.entityList){
            if(!(entity instanceof EntityEnderPearl))
                continue;

            if (pearl != null) {
                int c = Double.compare(pearl.getDistanceSq(mc.player), entity.getDistanceSq(mc.player));
                if (c < 1)
                    continue;
            }
            pearl = (EntityEnderPearl) entity;

            System.out.println(pearl);
        }
    }
}
