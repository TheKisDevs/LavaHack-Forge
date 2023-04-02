package com.kisman.cc.features.module.Debug;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.combat.Burrow2;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.thread.ThreadUtils;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class BurrowHelper extends Module {

    public BurrowHelper(){
        super("BurrowHelper", Category.DEBUG);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        int slot = InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        if(slot == -1)
            return;
        int oldSlot = mc.player.inventory.currentItem;
        double x = Math.floor(mc.player.posX);
        double y = mc.player.posY;
        double z = Math.floor(mc.player.posZ);
        BlockPos pos = Stream.of(EnumFacing.HORIZONTALS)
                .map(facing -> new BlockPos(x, y, z).offset(facing))
                .min(Comparator.comparingDouble(o -> mc.player.getDistance(o.getX() + 0.5, o.getY() + 0.5, o.getZ() + 0.5)))
                .orElse(null);
        if(pos == null)
            return;
        pos = pos.down();
        //TODO: BlockUtil2.placeBlock usage
        if(mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
//            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, true, false);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            mc.player.inventory.currentItem = oldSlot;
        }
        pos = pos.up();
        if(mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
//            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, true, false);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            mc.player.inventory.currentItem = oldSlot;
        }
        pos = pos.up();
        if(mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)){
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
//            BlockUtil.placeBlock2(pos, EnumHand.MAIN_HAND, true, false);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            mc.player.inventory.currentItem = oldSlot;
        }
        List<Vec3d> list = Arrays.asList(
                new Vec3d(x + 0.3, y, z + 0.3),
                new Vec3d(x + 0.3, y, z + 0.6),
                new Vec3d(x + 0.6, y, z + 0.3),
                new Vec3d(x + 0.6, y, z + 0.6)
        );
        Vec3d best = list.stream().min(Comparator.comparingDouble(o -> mc.player.getDistance(o.x, o.y, o.z))).orElse(null);
        mc.player.connection.sendPacket(new CPacketPlayer.Position(best.x, best.y, best.z, mc.player.onGround));
        mc.player.setPosition(best.x, best.y, best.z);
        ThreadUtils.async(() -> {
            try {
                ThreadUtils.sleep(60);
            } catch (InterruptedException e) {
                Kisman.LOGGER.error(e);
            }
            Burrow2.instance.enable();
        });
    }
}
