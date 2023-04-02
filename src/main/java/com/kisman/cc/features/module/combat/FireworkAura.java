package com.kisman.cc.features.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.entity.player.InventoryUtil;
import com.kisman.cc.util.enums.dynamic.SwapEnum2;
import com.kisman.cc.util.thread.ThreadUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ModuleInfo(
        name = "FireworkAura",
        category = Category.COMBAT,
        wip = true
)
public class FireworkAura extends Module {

    private final Setting placeRange = register(new Setting("PlaceRange", this, 5, 0, 6, false));
    private final Setting placeWallRange = register(new Setting("PlaceWallRange", this, 3, 0, 6, false));
    private final Setting targetRange = register(new Setting("TargetRange", this, 8, 0, 16, false));
    private final Setting raytrace = register(new Setting("Raytrace", this, false));
    private final Setting packetPlace = register(new Setting("PacketPlace", this, false));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting placeDelay = register(new Setting("PlaceDelay", this, 200, 0, 2000, NumberType.TIME));
    private final SettingEnum<SwapEnum2.Swap> swap = new SettingEnum<>("Switch", this, SwapEnum2.Swap.Silent).register();

    private Thread thread = null;

    private Info info = null;

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        doFireworkAura();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        thread.interrupt();
        info = null;
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        if(mc.player == null || mc.world == null)
            return;

        ThreadUtils.async(() -> info = getInfo());
    }

    private Info getInfo(){
        List<Info> list = new ArrayList<>();
        for(EntityPlayer player : mc.world.playerEntities){
            if(mc.player.getDistance(player) > targetRange.getValDouble())
                continue;
            List<BlockPos> blockPosList = Arrays.asList(
                    new BlockPos(player.posX + 0.3, player.posY, player.posZ + 0.3),
                    new BlockPos(player.posX + 0.3, player.posY, player.posZ - 0.3),
                    new BlockPos(player.posX - 0.3, player.posY, player.posZ + 0.3),
                    new BlockPos(player.posX - 0.3, player.posY, player.posZ - 0.3)
            );
            for(BlockPos pos : blockPosList){
                if(
                        mc.world.getBlockState(pos.down()).getBlock().isReplaceable(mc.world, pos.down())
                        && mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)
                        && mc.world.getBlockState(pos.up()).getBlock().isReplaceable(mc.world, pos.up())
                        && !mc.world.getBlockState(pos.up(2)).getBlock().isReplaceable(mc.world, pos.up(2))
                        && isBlockGood(pos.down())
                ){
                    list.add(new Info(pos.down(), player));
                }
            }
        }
        return list.stream().min(Comparator.comparingDouble(o -> getDistance(o.pos))).orElse(null);
    }

    private double getDistance(BlockPos pos){
        return mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    private boolean isBlockGood(BlockPos pos){
        double distance = getDistance(pos);
        if(distance > placeRange.getValDouble())
            return false;
        if(raytrace.getValBoolean())
            return EntityUtil.canSee(pos.up());
        return EntityUtil.canSee(pos.up()) || distance <= placeWallRange.getValDouble();
    }

    private void doFireworkAura(){
        AtomicBoolean started = new AtomicBoolean(true);
        thread = new Thread(() -> {
            while(!Thread.interrupted()){
                if(!started.get()) {
                    try {
                        ThreadUtils.sleep(placeDelay.getValInt());
                    } catch (InterruptedException e) {
                        Kisman.LOGGER.error(e);
                        toggle();
                        return;
                    }
                }
                doPlace(Thread.currentThread());
                started.set(false);
            }
        });
        thread.start();
    }

    private void doPlace(Thread thread){
        if(thread.isInterrupted())
            return;

        if(mc.player == null || mc.world == null)
            return;

        int slot = InventoryUtil.getHotbarItemSlot(Items.FIREWORKS);
        if(slot == -1)
            return;

        int oldSlot = mc.player.inventory.currentItem;

        swap.getValEnum().getTask().doTask(slot, false);

        //TODO: BlockUtil2.placeBlock usage
//        BlockUtil.placeBlock2(info.pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packetPlace.getValBoolean());

        swap.getValEnum().getTask().doTask(oldSlot, true);
    }

    private static class Info {

        public final BlockPos pos;

        public final EntityPlayer target;

        public Info(BlockPos pos, EntityPlayer target) {
            this.pos = pos;
            this.target = target;
        }
    }
}
