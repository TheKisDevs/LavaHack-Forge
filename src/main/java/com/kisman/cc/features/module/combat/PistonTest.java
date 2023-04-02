package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.world.BlockUtil2;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

@ModuleInfo(
        name = "PistonTest",
        debug = true
)
public class PistonTest extends Module {

    public PistonTest(){
        super("PistonTest", Category.COMBAT);
    }

    private BlockPos pos = null;
    private boolean place = true;
    private int ticks = 0;

    @Override
    public void onEnable() {
        pos = null;
        place = true;
        ticks = 0;
        Kisman.EVENT_BUS.subscribe(listener);
    }

    @Override
    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketExplosion){
            place = true;
        }
    });

    @Override
    public void update() {
        if(mc.player == null || mc.world == null)
            return;
        if(mc.objectMouseOver == null){
            pos = null;
        } else {
            pos = mc.objectMouseOver.getBlockPos();
        }
        if(pos == null && place)
            return;
        ticks++;
        if(place){
            placePiston();
            placeRedstone();
            placeCrystal();
            place = false;
            ticks = 0;
            return;
        }
        if(ticks >= 2){
            Entity crystal = mc.world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityEnderCrystal)
                    .min(Comparator.comparingDouble(mc.player::getDistance))
                    .orElse(null);
            if(crystal == null){
                toggle();
                return;
            }
            mc.playerController.attackEntity(mc.player, crystal);
            ticks = Integer.MIN_VALUE;
        }
    }

    private void placeCrystal(){
        int slot = InventoryUtil.getHotbarItemSlot(Items.END_CRYSTAL);
        if(slot == -1){
            toggle();
            return;
        }
        int oldSlot = mc.player.inventory.currentItem;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.currentPlayerItem = slot;
        BlockUtil2.placeBlock(pos.up().offset(mc.player.getHorizontalFacing().getOpposite()), EnumHand.MAIN_HAND, false, false, false);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        mc.player.inventory.currentItem = oldSlot;
        mc.playerController.currentPlayerItem = oldSlot;
    }

    private void placePiston(){
        int slot = InventoryUtil.getBlockInHotbar(Blocks.PISTON);
        if(slot == -1){
            toggle();
            return;
        }
        int oldSlot = mc.player.inventory.currentItem;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.currentPlayerItem = slot;
        BlockUtil2.placeBlock(pos.up(), EnumHand.MAIN_HAND, false, false, false);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        mc.player.inventory.currentItem = oldSlot;
        mc.playerController.currentPlayerItem = oldSlot;
    }

    private void placeRedstone(){
        int slot = InventoryUtil.getBlockInHotbar(Blocks.REDSTONE_BLOCK);
        if(slot == -1){
            toggle();
            return;
        }
        int oldSlot = mc.player.inventory.currentItem;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.currentPlayerItem = slot;
        BlockUtil2.placeBlock(pos.up(2), EnumHand.MAIN_HAND, false, false, false);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        mc.player.inventory.currentItem = oldSlot;
        mc.playerController.currentPlayerItem = oldSlot;
    }
}
