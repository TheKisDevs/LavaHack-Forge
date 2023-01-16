package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.world.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ArrowBlocker extends Module {

    private final SettingEnum<Mode> mode = new SettingEnum<>("Mode", this, Mode.Place).register();

    private final Setting placeRange = new Setting("Range", this, 5, 1, 6, false);

    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Swap", this, SwapEnum2.Swap.Silent).register();
    private final Setting rotate = new Setting("Rotate", this, false);
    private final Setting packet = new Setting("Packet", this, false);

    public ArrowBlocker(){
        super("ArrowBlocker", Category.PLAYER, true);
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(mc.player == null || mc.world == null)
            return;

        doArrowBlockerPlace();
    }

    private void doArrowBlockerPlace(){
        for(Entity entity : mc.world.entityList){
            if(entity == null)
                continue;
            if(!(entity instanceof EntityArrow))
                continue;
            if(mc.player.getDistance(entity) > placeRange.getValDouble())
                continue;
            ChatUtility.info().printClientModuleMessage("doing things");
            doArrowPlace((EntityArrow) entity);
        }
    }

    private void doArrowPlace(EntityArrow arrow){
        double x = arrow.posX + (arrow.motionX * 0.89);
        double y = arrow.posX + (arrow.motionY * 0.77);
        double z = arrow.posZ + (arrow.motionZ * 0.89);
        BlockPos pos = new BlockPos(x, y, z);
        if(BlockUtil.getPossibleSides(pos).isEmpty())
            return;
        if(!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
            return;
        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;
        int oldSlot = mc.player.inventory.currentItem;
        swap.getValEnum().getTask().doTask(slot, false);
        BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean());
        swap.getValEnum().getTask().doTask(oldSlot, true);
    }

    private enum Mode {
        Place,
        Crystal
    }
}
